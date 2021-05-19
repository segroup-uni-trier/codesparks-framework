/*
 * Copyright (c) 2021. Oliver Moseler
 */

package de.unitrier.st.codesparks.core.visualization.thread;

import com.intellij.ui.JBColor;
import de.unitrier.st.codesparks.core.data.*;
import de.unitrier.st.codesparks.core.visualization.AArtifactVisualizationLabelFactory;
import de.unitrier.st.codesparks.core.visualization.CodeSparksGraphics;
import de.unitrier.st.codesparks.core.visualization.VisConstants;
import de.unitrier.st.codesparks.core.visualization.VisualizationUtil;
import de.unitrier.st.codesparks.core.visualization.popup.ThreadColor;

import javax.swing.*;
import java.awt.*;
import java.util.*;

public final class ThreadForkLabelFactory extends AArtifactVisualizationLabelFactory
{
    private final IThreadArtifactsDisplayDataProvider threadArtifactsDisplayData;

    public ThreadForkLabelFactory(final AMetricIdentifier primaryMetricIdentifier)
    {
        super(primaryMetricIdentifier, 0);
        this.threadArtifactsDisplayData = new DefaultThreadArtifactsDisplayDataProvider(primaryMetricIdentifier);
    }

    public ThreadForkLabelFactory(final AMetricIdentifier primaryMetricIdentifier, final int sequence)
    {
        super(primaryMetricIdentifier, sequence);
        this.threadArtifactsDisplayData = new DefaultThreadArtifactsDisplayDataProvider(primaryMetricIdentifier);
    }

    public ThreadForkLabelFactory(final AMetricIdentifier primaryMetricIdentifier, final int sequence, final int xOffsetLeft)
    {
        super(primaryMetricIdentifier, sequence, xOffsetLeft);
        this.threadArtifactsDisplayData = new DefaultThreadArtifactsDisplayDataProvider(primaryMetricIdentifier);
    }

    @Override
    public JLabel createArtifactLabel(final AArtifact artifact)
    {
        final Collection<AThreadArtifact> threadArtifacts = artifact.getThreadArtifactsWithNumericMetricValue(primaryMetricIdentifier);
        final int totalNumberOfThreads = threadArtifacts.size();
        if (totalNumberOfThreads < 1)
        {
            return emptyLabel();
        }

        final int X_OFFSET_LEFT = this.X_OFFSET_LEFT + 1;
        final int threadsPerColumn = 3;
        final int threadMetaphorWidth = 24;
        final int barChartWidth = 24;
        final int X_OFFSET_RIGHT = 0;
        final int TOP_OFFSET = 6;

        final int lineHeight = VisualizationUtil.getLineHeightFloor(VisConstants.getLineHeight(), threadsPerColumn);
//        System.out.println("ThreadFork: lineHeight=" + lineHeight);
        final CodeSparksGraphics graphics = getGraphics(X_OFFSET_LEFT + threadMetaphorWidth + barChartWidth + X_OFFSET_RIGHT, lineHeight + TOP_OFFSET);

        // Thread metaphor
        graphics.setDefaultColor();
        //graphics.setColor(VisConstants.BORDER_COLOR);

        final int barrierXPos = threadMetaphorWidth / 2;

        // Leading arrow
        graphics.fillRect(X_OFFSET_LEFT, TOP_OFFSET + lineHeight / 2, barrierXPos - 1, 1);
        graphics.drawLine(X_OFFSET_LEFT + barrierXPos - 4, TOP_OFFSET + lineHeight / 2 - 3, X_OFFSET_LEFT + barrierXPos - 1, TOP_OFFSET + lineHeight / 2);
        graphics.drawLine(X_OFFSET_LEFT + barrierXPos - 4, TOP_OFFSET + lineHeight / 2 + 3, X_OFFSET_LEFT + barrierXPos - 1, TOP_OFFSET + lineHeight / 2);

        // Vertical bar or barrier, respectively
        final int barrierWidth = 3;
        graphics.fillRect(X_OFFSET_LEFT + barrierXPos, TOP_OFFSET, barrierWidth, lineHeight);

        final Rectangle threadVisualisationArea = new Rectangle(
                X_OFFSET_LEFT + threadMetaphorWidth, TOP_OFFSET, barChartWidth - 1, lineHeight - 1);
        graphics.drawRectangle(threadVisualisationArea);

        // Draw the clusters
        final int threadSquareEdgeLength = 3;//(lineHeight - 6) / threadsPerColumn;
        final int clusterBarMaxWidth = 20;
        int threadSquareYPos = lineHeight - threadSquareEdgeLength - 2;
        final int threadSquareOffset = threadSquareEdgeLength + 1;

        // If there is no thread which is selected, i.e. all threads executing this artifact are filtered
        boolean createDisabledViz = threadArtifacts.stream().allMatch(AThreadArtifact::isFiltered);

        final double threadFilteredTotalMetricValueOfArtifact = ThreadVisualizationUtil.getMetricValueSumOfSelectedThreads(artifact,
                primaryMetricIdentifier, createDisabledViz);

//        final ThreadArtifactClustering threadClusters =
//                artifact.getSortedConstraintKMeansWithAMaximumOfThreeClustersThreadArtifactClustering(primaryMetricIdentifier);

        ThreadArtifactClustering threadClusters =
                artifact.getThreadArtifactClustering(SmileKernelDensityClustering.getInstance(primaryMetricIdentifier));

//        final List<ThreadArtifactCluster> threadClusters = artifact.getThreadArtifactClustering(new ApacheKMeans(primaryMetricIdentifier, 3));

//        final BestSilhouetteKClustering bestSilhouetteKClustering = new BestSilhouetteKClustering(new ApacheKMeans(primaryMetricIdentifier), 6);
//        final ThreadArtifactClustering threadClusters = artifact.getThreadArtifactClustering(bestSilhouetteKClustering);

//        final ThreadArtifactClustering threadClusters = artifact.getThreadArtifactClustering(new WekaKMeans(primaryMetricIdentifier, 3));

//        final ThreadArtifactClustering threadClusters =
//                artifact.getThreadArtifactClustering(new KernelDensityThreadClusteringStrategy(primaryMetricIdentifier));
        final int numberOfThreadClusters = threadClusters.size();
        if (numberOfThreadClusters > 3)
        {
            final KThreadArtifactClusteringStrategy apacheKMeans = ApacheKMeans.getInstance(primaryMetricIdentifier, 3);
            threadClusters = artifact.getThreadArtifactClustering(apacheKMeans);
        }

        int clusterNum = 0;
        final VisualThreadClusterPropertiesManager clusterPropertiesManager = VisualThreadClusterPropertiesManager.getInstance();
        final Map<ThreadArtifactCluster, Boolean> clusterPropertiesPresent = new HashMap<>(threadClusters.size()); // Do not replace threadClusters.size()

        for (final ThreadArtifactCluster threadCluster : threadClusters)
        {
            JBColor clusterColor = ThreadColor.getNextColor(clusterNum, createDisabledViz);
            final VisualThreadClusterProperties properties = clusterPropertiesManager.getProperties(threadCluster);
            if (properties != null)
            {
                clusterPropertiesPresent.put(threadCluster, true);
                final JBColor color = properties.getColor();
                if (color != null)
                {
                    clusterColor = color;
                }
            }

            /*
             * Draw the metric value sum bar
             */
            final Color backgroundMetricColor = VisualizationUtil.getBackgroundMetricColor(clusterColor, .35f);
            final JBColor clusterMetricValueSumColor = new JBColor(backgroundMetricColor, backgroundMetricColor);

            graphics.setColor(clusterMetricValueSumColor);

            double percent = ThreadVisualizationUtil.getMetricValueSumOfSelectedThreadsOfTheClusterRelativeToTotal(
                    primaryMetricIdentifier
                    , threadArtifacts
                    , threadCluster
                    , threadFilteredTotalMetricValueOfArtifact
                    , createDisabledViz);

            int clusterWidth = ThreadVisualizationUtil.getDiscreteTenValuedScaleWidth(percent, clusterBarMaxWidth);

            graphics.fillRect(X_OFFSET_LEFT + threadMetaphorWidth + 2, TOP_OFFSET + threadSquareYPos, clusterWidth, threadSquareEdgeLength);

            if (clusterWidth > 0)
            {
                // Arrows after barrier
                graphics.fillRect(X_OFFSET_LEFT + barrierXPos + barrierWidth, TOP_OFFSET + threadSquareYPos + 1, barrierXPos - 1, 1);
            }

            /*
             * Draw the metric value avg bar
             */
            graphics.setColor(clusterColor);
            percent = ThreadVisualizationUtil.getMetricValueAverageOfSelectedThreadsOfTheClusterRelativeToTotal(primaryMetricIdentifier, threadArtifacts,
                    threadCluster, threadFilteredTotalMetricValueOfArtifact, createDisabledViz);

            clusterWidth = ThreadVisualizationUtil.getDiscreteTenValuedScaleWidth(percent, clusterBarMaxWidth);
            graphics.fillRect(X_OFFSET_LEFT + threadMetaphorWidth + 2, TOP_OFFSET + threadSquareYPos, clusterWidth, threadSquareEdgeLength);

            if (clusterWidth > 0)
            {
                // Arrows after barrier
                graphics.fillRect(X_OFFSET_LEFT + barrierXPos + barrierWidth, TOP_OFFSET + threadSquareYPos + 1, barrierXPos - 1, 1);
            }

            // Save the position and color to the properties such that they can be reused in the neighbor artifact visualization
            if (!clusterPropertiesPresent.getOrDefault(threadCluster, false))
            {
                final VisualThreadClusterProperties visualThreadClusterProperties =
                        new VisualThreadClusterPropertiesBuilder(threadCluster)
                                .setColor(clusterColor)
                                .setPosition(clusterNum)
                                .get();
                clusterPropertiesManager.registerProperties(visualThreadClusterProperties);
            }

            /*
             * -------------------------------------------
             */

            clusterNum += 1;

            threadSquareYPos -= threadSquareOffset;
        }

        if (numberOfThreadClusters > 3) // TODO: When ready, change this to 3
        { // The 'plus' symbol indicating that there are more than three thread clusters!
            graphics.setColor(VisConstants.BORDER_COLOR);
//            final int plusSymbolXOffset = X_OFFSET_LEFT + threadMetaphorWidth - 2;
//            graphics.drawLine(plusSymbolXOffset, 0, plusSymbolXOffset, 4);
//            graphics.drawLine(plusSymbolXOffset - 2, 2, plusSymbolXOffset + 2, 2);
            final int plusSymbolXOffset = X_OFFSET_LEFT + threadMetaphorWidth + barChartWidth - 1;
            graphics.drawLine(plusSymbolXOffset, 0, plusSymbolXOffset, 4);
            graphics.drawLine(plusSymbolXOffset - 2, 2, plusSymbolXOffset + 2, 2);
        }

        // Creation of the label
        final JLabel jLabel = makeLabel(graphics);
        jLabel.addMouseListener(new ThreadForkMouseListener(jLabel, artifact, primaryMetricIdentifier, threadArtifactsDisplayData));
        return jLabel;
    }
}
