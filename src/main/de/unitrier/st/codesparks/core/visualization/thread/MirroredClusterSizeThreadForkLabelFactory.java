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

public final class MirroredClusterSizeThreadForkLabelFactory extends AArtifactVisualizationLabelFactory
{
    @SuppressWarnings("unused")
    public MirroredClusterSizeThreadForkLabelFactory(final AMetricIdentifier primaryMetricIdentifier)
    {
        super(primaryMetricIdentifier);
    }

    public MirroredClusterSizeThreadForkLabelFactory(final AMetricIdentifier primaryMetricIdentifier, final int sequence)
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

        final int lineHeight = VisualizationUtil.getLineHeightFloor(VisConstants.getLineHeight(), threadsPerColumn);

        final CodeSparksGraphics graphics = getGraphics(X_OFFSET_LEFT + threadMetaphorWidth + barChartWidth + X_OFFSET_RIGHT, lineHeight);

        // The rectangle for the bars
        graphics.setColor(VisConstants.BORDER_COLOR);

        final Rectangle threadVisualisationArea = new Rectangle(
                X_OFFSET_LEFT, 0, barChartWidth - 1, lineHeight - 1);

        graphics.drawRectangle(threadVisualisationArea);

        // Thread metaphor
        final int barrierXOffset = 9;
        final int barrierXPos = barChartWidth + barrierXOffset;//threadMetaphorWidth / 2;
        // Vertical bar or barrier, respectively
        final int barrierWidth = 3;
        graphics.fillRect(X_OFFSET_LEFT + barrierXPos, 0, barrierWidth, lineHeight);

        // Subsequent arrow
        final int arrowLength = threadMetaphorWidth / 2;
        final int arrowStartX = X_OFFSET_LEFT + barrierXPos + barrierWidth;
        graphics.fillRect(arrowStartX, lineHeight / 2, arrowLength, 1);
        graphics.drawLine(arrowStartX + 3, lineHeight / 2 - 3, arrowStartX, lineHeight / 2);
        graphics.drawLine(arrowStartX + 3, lineHeight / 2 + 3, arrowStartX, lineHeight / 2);

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

        final List<ThreadArtifactCluster> threadClusters =
                artifact.getSortedConstraintKMeansWithAMaximumOfThreeClustersThreadArtifactClustering(primaryMetricIdentifier);

//        final ThreadArtifactClustering threadClusters = artifact.getThreadArtifactClustering(new ApacheKMeans(primaryMetricIdentifier, 3));

//        final BestSilhouetteKClustering bestSilhouetteKClustering = new BestSilhouetteKClustering(new ApacheKMeans(primaryMetricIdentifier), 6);
//
//        final ThreadArtifactClustering threadClusters = artifact.getThreadArtifactClustering(bestSilhouetteKClustering);


        for (final ThreadArtifactCluster threadCluster : threadClusters)
        {
            JBColor clusterColor = ThreadColor.getNextColor(clusterNum, createDisabledViz);
            graphics.setColor(clusterColor);

            final long numberOfThreadsOfCluster = threadCluster.stream().filter(clusterThread -> (createDisabledViz || !clusterThread.isFiltered())).count();

            double percent = numberOfThreadsOfCluster / totalNumberOfFilteredThreads;

            int clusterWidth;
            if (percent > 0D)
            {
                int discrete = (int) (percent * 100 / 10 + 0.9999);
                clusterWidth = clusterBarMaxWidth / 10 * discrete;
            } else
            {
                clusterWidth = 0;
            }
            graphics.fillRect(X_OFFSET_LEFT + barChartWidth - clusterWidth - 2, threadSquareYPos, clusterWidth, threadSquareEdgeLength);

            if (clusterWidth > 0)
            {
                // Arrows after barrier
                graphics.fillRect(X_OFFSET_LEFT + barChartWidth - 2, threadSquareYPos + 1, arrowLength - 1, 1);
            }
            clusterNum += 1;

            threadSquareYPos -= threadSquareOffset;

        }
        // Creation of the label

        final JLabel jLabel = makeLabel(graphics);
        jLabel.addMouseListener(new DefaultThreadVisualizationMouseListener(jLabel, artifact, primaryMetricIdentifier));
        return jLabel;
    }
}
