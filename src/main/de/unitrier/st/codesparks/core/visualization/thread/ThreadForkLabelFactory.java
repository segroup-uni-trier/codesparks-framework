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
import java.util.Collection;
import java.util.OptionalDouble;

public final class ThreadForkLabelFactory extends AArtifactVisualizationLabelFactory
{
    @SuppressWarnings("unused")
    public ThreadForkLabelFactory(final AMetricIdentifier primaryMetricIdentifier)
    {
        super(primaryMetricIdentifier, -1);
    }

    public ThreadForkLabelFactory(final AMetricIdentifier primaryMetricIdentifier, final int sequence)
    {
        super(primaryMetricIdentifier, sequence);
    }

    @SuppressWarnings("unused")
    public ThreadForkLabelFactory(final AMetricIdentifier primaryMetricIdentifier, final int sequence, final int xOffsetLeft)
    {
        super(primaryMetricIdentifier, sequence, xOffsetLeft);
    }

    @Override
    public JLabel createArtifactLabel(final AArtifact artifact)
    {
        final Collection<AThreadArtifact> threadArtifacts = artifact.getThreadArtifacts();
        if (threadArtifacts.isEmpty())
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

        int clusterNum = 0;
        final VisualThreadClusterPropertiesManager clusterPropertiesManager = VisualThreadClusterPropertiesManager.getInstance();

        // If there is no thread which is selected, i.e. all threads executing this artifact are filtered
        boolean createDisabledViz = threadArtifacts.stream().allMatch(AThreadArtifact::isFiltered);

        final double threadFilteredTotalMetricValueOfArtifact = artifact.getThreadFilteredTotalNumericalMetricValue(primaryMetricIdentifier, createDisabledViz);

//        final ThreadArtifactClustering threadClusters =
//                artifact.getSortedConstraintKMeansWithAMaximumOfThreeClustersThreadArtifactClustering(primaryMetricIdentifier);

        final ThreadArtifactClustering threadClusters =
                artifact.getThreadArtifactClustering(SmileKernelDensityClustering.getInstance(primaryMetricIdentifier));

//        final List<ThreadArtifactCluster> threadClusters = artifact.getThreadArtifactClustering(new ApacheKMeans(primaryMetricIdentifier, 3));

//        final BestSilhouetteKClustering bestSilhouetteKClustering = new BestSilhouetteKClustering(new ApacheKMeans(primaryMetricIdentifier), 6);
//        final ThreadArtifactClustering threadClusters = artifact.getThreadArtifactClustering(bestSilhouetteKClustering);

//        final ThreadArtifactClustering threadClusters = artifact.getThreadArtifactClustering(new WekaKMeans(primaryMetricIdentifier, 3));

//        final ThreadArtifactClustering threadClusters =
//                artifact.getThreadArtifactClustering(new KernelDensityThreadClusteringStrategy(primaryMetricIdentifier));

        for (final ThreadArtifactCluster threadCluster : threadClusters)
        {
            final JBColor clusterColor = ThreadColor.getNextColor(clusterNum, createDisabledViz);

            /*
             * Draw the metric value sum bar
             */
            final Color backgroundMetricColor = VisualizationUtil.getBackgroundMetricColor(clusterColor, .35f);
            final JBColor clusterMetricValueSumColor = new JBColor(backgroundMetricColor, backgroundMetricColor);

            graphics.setColor(clusterMetricValueSumColor);

            double percent = getThreadFilteredArtifactMetricValueSumOfClusterRelativeToTotal(threadArtifacts, threadCluster,
                    threadFilteredTotalMetricValueOfArtifact, createDisabledViz);

            int clusterWidth = ThreadVisualizationUtil.getDiscreteTenValuedScaleWidth(percent, clusterBarMaxWidth);

            graphics.fillRect(X_OFFSET_LEFT + threadMetaphorWidth + 2, TOP_OFFSET + threadSquareYPos, clusterWidth, threadSquareEdgeLength);

            if (clusterWidth > 0)
            {
                // Arrows after barrier
                graphics.fillRect(X_OFFSET_LEFT + barrierXPos + barrierWidth, TOP_OFFSET + threadSquareYPos + 1, barrierXPos - 1, 1);
            }

            // Save the position and color to the properties such that they can be reused in the neighbor artifact visualization
            final VisualThreadClusterProperties visualThreadClusterProperties =
                    new VisualThreadClusterPropertiesBuilder(threadCluster)
                            .setColor(clusterColor)
                            .setPosition(clusterNum)
                            .get();
            clusterPropertiesManager.registerProperties(visualThreadClusterProperties);

            /*
             * Draw the metric value avg bar
             */
            graphics.setColor(clusterColor);
            percent = getThreadFilteredArtifactMetricValueAverageOfClusterRelativeToTotal(threadArtifacts, threadCluster,
                    threadFilteredTotalMetricValueOfArtifact, createDisabledViz);

            clusterWidth = ThreadVisualizationUtil.getDiscreteTenValuedScaleWidth(percent, clusterBarMaxWidth);
            graphics.fillRect(X_OFFSET_LEFT + threadMetaphorWidth + 2, TOP_OFFSET + threadSquareYPos, clusterWidth, threadSquareEdgeLength);

            if (clusterWidth > 0)
            {
                // Arrows after barrier
                graphics.fillRect(X_OFFSET_LEFT + barrierXPos + barrierWidth, TOP_OFFSET + threadSquareYPos + 1, barrierXPos - 1, 1);
            }

            /*
             * -------------------------------------------
             */

            clusterNum += 1;

            threadSquareYPos -= threadSquareOffset;
        }

        if (threadClusters.size() > 0) // TODO: When ready, change this to 3
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
        jLabel.addMouseListener(new ThreadForkVisualizationMouseListener(jLabel, artifact, primaryMetricIdentifier));
        return jLabel;
    }

    /**
     * @deprecated Use {@link ThreadVisualizationUtil#getThreadFilteredArtifactMetricValueSumOfClusterRelativeToTotal(AMetricIdentifier, Collection, ThreadArtifactCluster, double, boolean)} } instead
     */
    @Deprecated
    private double getThreadFilteredArtifactMetricValueSumOfClusterRelativeToTotal(final Collection<AThreadArtifact> threadsOfArtifact,
                                                                                   final ThreadArtifactCluster threadArtifactCluster,
                                                                                   final double total,
                                                                                   final boolean createDisabledViz)
    {
        final double sum =
                threadsOfArtifact.stream().filter(threadExecutingArtifact -> (createDisabledViz || !threadExecutingArtifact.isFiltered()) && threadArtifactCluster.stream().anyMatch(
                        clusterThread -> (createDisabledViz || !clusterThread.isFiltered()) && clusterThread.getIdentifier().equals(threadExecutingArtifact.getIdentifier())
                )).mapToDouble(threadExecutingArtifact -> threadExecutingArtifact.getNumericalMetricValue(primaryMetricIdentifier)).sum();
        //noinspection UnnecessaryLocalVariable
        final double ratio = sum / total;
        return ratio;
    }

    /**
     * @deprecated Use {@link ThreadVisualizationUtil#getThreadFilteredArtifactMetricValueAverageOfClusterRelativeToTotal(AMetricIdentifier, Collection, ThreadArtifactCluster, double, boolean)} } instead
     */
    @Deprecated
    private double getThreadFilteredArtifactMetricValueAverageOfClusterRelativeToTotal(final Collection<AThreadArtifact> threadsOfArtifact,
                                                                                       final ThreadArtifactCluster threadArtifactCluster,
                                                                                       final double total,
                                                                                       final boolean createDisabledViz)
    {
        final OptionalDouble average =
                threadsOfArtifact.stream().filter(threadExecutingArtifact -> (createDisabledViz || !threadExecutingArtifact.isFiltered()) && threadArtifactCluster.stream().anyMatch(
                        clusterThread -> (createDisabledViz || !clusterThread.isFiltered()) && clusterThread.getIdentifier().equals(threadExecutingArtifact.getIdentifier())
                )).mapToDouble(threadExecutingArtifact -> threadExecutingArtifact.getNumericalMetricValue(primaryMetricIdentifier)).average();
        if (average.isPresent())
        {
            //noinspection UnnecessaryLocalVariable
            final double ratio = average.getAsDouble() / total;
            return ratio;
        }
        return Double.NaN;
    }

}
