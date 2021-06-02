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
import java.util.Map;

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
        final List<AThreadArtifact> threadArtifacts = new ArrayList<>(artifact.getThreadArtifactsWithNumericMetricValue(primaryMetricIdentifier));
        final int totalNumberOfThreads = threadArtifacts.size();
        if (totalNumberOfThreads < 1)
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

        double totalNumberOfSelectedThreads = (double) threadArtifacts.stream().filter(AThreadArtifact::isSelected).count();
        final boolean createDisabledViz = totalNumberOfSelectedThreads == 0;
        if (createDisabledViz)
        {
            totalNumberOfSelectedThreads = totalNumberOfThreads;
        }

        // At first, get the thread classification based on the kernel based density estimation

        final AThreadArtifactClusteringStrategy kbdeClusteringStrategy = KernelBasedDensityEstimationClustering.getInstance(primaryMetricIdentifier);

        final ThreadArtifactClustering kbdeClustering = artifact.clusterThreadArtifacts(kbdeClusteringStrategy);
        final int numberOfEstimatedClusters = kbdeClustering.size();

        ThreadArtifactClustering selectedClustering = artifact.getSelectedClusteringOrApplyAndSelect(kbdeClusteringStrategy);

        final long numberOfNonEmptyThreadClusters = selectedClustering
                .stream()
                .filter(cl -> cl.stream()
                        .anyMatch(AThreadArtifact::isSelected))
                .count();
        if (numberOfNonEmptyThreadClusters > 3)
        {
            final KThreadArtifactClusteringStrategy apacheKMeansPlusPlus = ApacheKMeansPlusPlus.getInstance(primaryMetricIdentifier, 3);
            selectedClustering = artifact.clusterThreadArtifacts(apacheKMeansPlusPlus);
        }

        final VisualThreadClusterPropertiesManager clusterPropertiesManager = VisualThreadClusterPropertiesManager.getInstance(selectedClustering);
        final Map<ThreadArtifactCluster, Integer> drawPositions = ThreadVisualizationUtil.getDrawPositions(selectedClustering, clusterPropertiesManager);

        int clusterNum = 0;
        for (final ThreadArtifactCluster threadCluster : selectedClustering)
        {
            if (!createDisabledViz && threadCluster.stream().noneMatch(AThreadArtifact::isSelected))
            { // In case the density based classification approach yields more than 3 clusters but only up to three of them contain selected threads. In that
                // case, skip the clusters which only contain filtered threads.
                clusterNum += 1;
                continue;
            }

            final VisualThreadClusterProperties properties = clusterPropertiesManager.getOrDefault(threadCluster, clusterNum);
            JBColor clusterColor = properties.getColor();
            if (createDisabledViz)
            {
                clusterColor = ThreadColor.getDisabledColor(clusterColor);
            }
            final int positionToDrawCluster = drawPositions.get(threadCluster);

            final int clusterYPos = TOP_OFFSET + threadSquareYPos - positionToDrawCluster * threadSquareOffset;

            final long numberOfThreadsOfCluster = threadCluster.stream().filter(clusterThread -> (createDisabledViz || clusterThread.isSelected())).count();

            double percent = numberOfThreadsOfCluster / totalNumberOfSelectedThreads;

            final int totalNumberOfThreadsWidth = ThreadVisualizationUtil.getDiscreteTenValuedScaleWidth(percent, clusterBarMaxWidth);

            // The number of threads bar
            final Color backgroundMetricColor = VisualizationUtil.getBackgroundMetricColor(clusterColor, .35f);
            graphics.setColor(backgroundMetricColor);
            graphics.fillRect(X_OFFSET_LEFT + barChartWidth - totalNumberOfThreadsWidth - 2, clusterYPos, totalNumberOfThreadsWidth,
                    threadSquareEdgeLength);

            // The thread-types bar
            final int numberOfSelectedThreadTypesOfCluster = ThreadVisualizationUtil.getNumberOfSelectedThreadTypesWithNumericMetricValueInCluster(
                    artifact, primaryMetricIdentifier, threadCluster, createDisabledViz);
            percent = numberOfSelectedThreadTypesOfCluster / totalNumberOfSelectedThreads;

            final int totalNumberOfThreadTypesWidth = ThreadVisualizationUtil.getDiscreteTenValuedScaleWidth(percent, clusterBarMaxWidth);
            graphics.setColor(clusterColor);
            graphics.fillRect(X_OFFSET_LEFT + barChartWidth - totalNumberOfThreadTypesWidth - 2, clusterYPos
                    , totalNumberOfThreadTypesWidth, threadSquareEdgeLength);

            if (totalNumberOfThreadsWidth > 0)
            {
                // Arrows after barrier
                graphics.fillRect(X_OFFSET_LEFT + barChartWidth - 2, clusterYPos + 1, arrowLength - 1, 1);
            }

            //------------------

            clusterNum += 1;

            //threadSquareYPos -= threadSquareOffset;
        }

        if (numberOfEstimatedClusters > 3) // TODO: When ready, change this to 3
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
