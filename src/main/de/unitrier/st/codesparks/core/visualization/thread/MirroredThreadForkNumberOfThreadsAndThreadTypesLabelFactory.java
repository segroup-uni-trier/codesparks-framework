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
import java.util.ArrayList;
import java.util.List;

public final class MirroredThreadForkNumberOfThreadsAndThreadTypesLabelFactory extends AArtifactVisualizationLabelFactory
{
    @SuppressWarnings("unused")
    public MirroredThreadForkNumberOfThreadsAndThreadTypesLabelFactory(final AMetricIdentifier primaryMetricIdentifier)
    {
        super(primaryMetricIdentifier);
    }

    public MirroredThreadForkNumberOfThreadsAndThreadTypesLabelFactory(final AMetricIdentifier primaryMetricIdentifier, final int sequence)
    {
        super(primaryMetricIdentifier, sequence);
    }

    @Override
    public JLabel createArtifactLabel(final AArtifact artifact)
    {
        final List<AThreadArtifact> threadArtifacts = new ArrayList<>(artifact.getThreadArtifacts());

        if (threadArtifacts.isEmpty())
        {
            return emptyLabel();
        }

        final int X_OFFSET_LEFT = -1; // We don't need the left vertical line of the rectangle when used in conjunction with the original ThreadFork
        final int threadsPerColumn = 3;
        final int threadMetaphorWidth = 24;
        final int barChartWidth = 24;
        final int X_OFFSET_RIGHT = 1;
        final int TOP_OFFSET = 6;

        final int lineHeight = VisualizationUtil.getLineHeightFloor(VisConstants.getLineHeight(), threadsPerColumn);

        final CodeSparksGraphics graphics = getGraphics(X_OFFSET_LEFT + threadMetaphorWidth + barChartWidth + X_OFFSET_RIGHT,
                lineHeight + TOP_OFFSET);

        // The rectangle for the bars
        graphics.setColor(VisConstants.BORDER_COLOR);

        final Rectangle threadVisualisationArea = new Rectangle(
                X_OFFSET_LEFT, TOP_OFFSET, barChartWidth - 1, lineHeight - 1);

        graphics.drawRectangle(threadVisualisationArea);

        // Thread metaphor
        final int barrierXOffset = 9;
        final int barrierXPos = barChartWidth + barrierXOffset;//threadMetaphorWidth / 2;
        // Vertical bar or barrier, respectively
        final int barrierWidth = 3;
        graphics.fillRect(X_OFFSET_LEFT + barrierXPos, TOP_OFFSET, barrierWidth, lineHeight);

        // Subsequent arrow
        final int arrowLength = threadMetaphorWidth / 2;
        final int arrowStartX = X_OFFSET_LEFT + barrierXPos + barrierWidth;
        graphics.fillRect(arrowStartX, TOP_OFFSET + lineHeight / 2, arrowLength, 1);
        graphics.drawLine(arrowStartX + 3, TOP_OFFSET + lineHeight / 2 - 3, arrowStartX, TOP_OFFSET + lineHeight / 2);
        graphics.drawLine(arrowStartX + 3, TOP_OFFSET + lineHeight / 2 + 3, arrowStartX, TOP_OFFSET + lineHeight / 2);

        // Draw the clusters
        final int threadSquareEdgeLength = 3;//(lineHeight - 6) / threadsPerColumn;
        final int clusterBarMaxWidth = 20;
        int threadSquareYPos = lineHeight - threadSquareEdgeLength - 2;
        final int threadSquareOffset = threadSquareEdgeLength + 1;

        int clusterNum = 0;
        boolean createDisabledViz = threadArtifacts.stream().allMatch(AThreadArtifact::isFiltered);

        final double totalNumberOfFilteredThreads =
                (double) artifact.getThreadArtifacts().stream().filter(threadExecutingArtifact -> (createDisabledViz || !threadExecutingArtifact.isFiltered()))
                        .count();

//        final List<ThreadArtifactCluster> threadClusters =
//                artifact.getSortedConstraintKMeansWithAMaximumOfThreeClustersThreadArtifactClustering(primaryMetricIdentifier);

        final ThreadArtifactClustering threadClusters =
                artifact.getThreadArtifactClustering(SmileKernelDensityClustering.getInstance(primaryMetricIdentifier));

//        final ThreadArtifactClustering threadClusters = artifact.getThreadArtifactClustering(new ApacheKMeans(primaryMetricIdentifier, 3));

//        final BestSilhouetteKClustering bestSilhouetteKClustering = new BestSilhouetteKClustering(new ApacheKMeans(primaryMetricIdentifier), 6);
//
//        final ThreadArtifactClustering threadClusters = artifact.getThreadArtifactClustering(bestSilhouetteKClustering);

        final VisualThreadClusterPropertiesManager propertiesManager = VisualThreadClusterPropertiesManager.getInstance();

        for (final ThreadArtifactCluster threadCluster : threadClusters)
        {
            JBColor clusterColor = ThreadColor.getNextColor(clusterNum, createDisabledViz);

            final VisualThreadClusterProperties properties = propertiesManager.getProperties(threadCluster);
            if (properties != null)
            {
                final JBColor color = properties.getColor();
                if (color != null)
                {
                    clusterColor = color;
                }
            }

            final long numberOfThreadsOfCluster = threadCluster.stream().filter(clusterThread -> (createDisabledViz || !clusterThread.isFiltered())).count();

            double percent = numberOfThreadsOfCluster / totalNumberOfFilteredThreads;

            final int totalNumberOfThreadsWidth = ThreadVisualizationUtil.getDiscreteTenValuedScaleWidth(percent, clusterBarMaxWidth);

            final Color backgroundMetricColor = VisualizationUtil.getBackgroundMetricColor(clusterColor, .35f);
            graphics.setColor(backgroundMetricColor);
            graphics.fillRect(X_OFFSET_LEFT + barChartWidth - totalNumberOfThreadsWidth - 2, TOP_OFFSET + threadSquareYPos, totalNumberOfThreadsWidth,
                    threadSquareEdgeLength);

            // The thread-types bar
            final int numberOfFilteredThreadTypesOfCluster = ThreadVisualizationUtil.getNumberOfFilteredThreadTypesOfCluster(artifact, threadCluster);
            percent = numberOfFilteredThreadTypesOfCluster / totalNumberOfFilteredThreads;

            final int totalNumberOfThreadTypesWidth = ThreadVisualizationUtil.getDiscreteTenValuedScaleWidth(percent, clusterBarMaxWidth);
            graphics.setColor(clusterColor);
            graphics.fillRect(X_OFFSET_LEFT + barChartWidth - totalNumberOfThreadTypesWidth - 2, TOP_OFFSET + threadSquareYPos
                    , totalNumberOfThreadTypesWidth, threadSquareEdgeLength);

            if (totalNumberOfThreadsWidth > 0)
            {
                // Arrows after barrier
                graphics.fillRect(X_OFFSET_LEFT + barChartWidth - 2, TOP_OFFSET + threadSquareYPos + 1, arrowLength - 1, 1);
            }
            clusterNum += 1;

            threadSquareYPos -= threadSquareOffset;
        }

        if (threadClusters.size() > 0) // TODO: When ready, change this to 3
        { // The 'plus' symbol indicating that there are more than three thread clusters!
            graphics.setColor(VisConstants.BORDER_COLOR);
//            final int plusSymbolXOffset = X_OFFSET_LEFT + threadMetaphorWidth - 2;
//            graphics.drawLine(plusSymbolXOffset, 0, plusSymbolXOffset, 4);
//            graphics.drawLine(plusSymbolXOffset - 2, 2, plusSymbolXOffset + 2, 2);
            final int plusSymbolXOffset = -1;
            graphics.drawLine(plusSymbolXOffset, 0, plusSymbolXOffset, 4);
            graphics.drawLine(plusSymbolXOffset - 2, 2, plusSymbolXOffset + 2, 2);
        }

        // Creation of the label
        final JLabel jLabel = makeLabel(graphics);
        jLabel.addMouseListener(new DefaultThreadVisualizationMouseListener(jLabel, artifact, primaryMetricIdentifier));
        return jLabel;
    }
}
