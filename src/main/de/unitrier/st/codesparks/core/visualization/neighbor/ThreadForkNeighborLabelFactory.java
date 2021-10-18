/*
 * Copyright (c) 2021. Oliver Moseler
 */
package de.unitrier.st.codesparks.core.visualization.neighbor;

import com.intellij.ui.JBColor;
import de.unitrier.st.codesparks.core.data.*;
import de.unitrier.st.codesparks.core.visualization.CodeSparksGraphics;
import de.unitrier.st.codesparks.core.visualization.VisConstants;
import de.unitrier.st.codesparks.core.visualization.VisualizationUtil;
import de.unitrier.st.codesparks.core.visualization.thread.ThreadVisualizationUtil;
import de.unitrier.st.codesparks.core.visualization.thread.VisualThreadClusterProperties;
import de.unitrier.st.codesparks.core.visualization.thread.VisualThreadClusterPropertiesManager;

import javax.swing.*;
import java.awt.*;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

public class ThreadForkNeighborLabelFactory extends ANeighborArtifactVisualizationLabelFactory
{
    @SuppressWarnings("unused")
    public ThreadForkNeighborLabelFactory(final AMetricIdentifier primaryMetricIdentifier)
    {
        this(primaryMetricIdentifier, 0);
    }

    public ThreadForkNeighborLabelFactory(final AMetricIdentifier primaryMetricIdentifier, int sequence)
    {
        super(primaryMetricIdentifier, sequence);
    }

    // TODO: Move JavaDoc to super class!

    /**
     * Computes the visualization for the given line of code with respect to the given artifact.
     *
     * @param artifact                              The artifact for which to create the visualization, e.g. a class or method etc.
     * @param threadFilteredNeighborArtifactsOfLine The list of neighbor artifacts which are executed by the threads which are currently selected, i.e.
     *                                              for each neighbor artifact n element of the list exists a thread thr executing n where 'thr.isFiltered()'
     *                                              yields false.
     * @return The JLabel holding the visualization.
     */
    @Override
    public JLabel createNeighborArtifactLabel(
            final AArtifact artifact
            , List<ANeighborArtifact> threadFilteredNeighborArtifactsOfLine
    )
    {
        threadFilteredNeighborArtifactsOfLine =
                threadFilteredNeighborArtifactsOfLine
                        .stream()
                        .filter(neighbor -> neighbor.getNumericalMetricValue(primaryMetricIdentifier) > 0)
                        .collect(Collectors.toList());
        if (threadFilteredNeighborArtifactsOfLine.isEmpty())
        {
            return emptyLabel();
        }
        final int threadsPerColumn = 3;
        final int lineHeight = VisualizationUtil.getLineHeightFloor(VisConstants.getLineHeight(), threadsPerColumn);

        final int X_OFFSET_LEFT = 2;
        final int X_OFFSET_RIGHT = 0;

        final int threadMetaphorWidth = 24;
        final int barChartWidth = 24;

        final int clusterBarMaxWidth = 20;

        final int totalWidth = X_OFFSET_LEFT + threadMetaphorWidth + barChartWidth + X_OFFSET_RIGHT;

        final int threadSquareEdgeLength = 3;

        final int initialThreadSquareYPos = lineHeight - threadSquareEdgeLength - 2;
        final int threadSquareOffset = threadSquareEdgeLength + 1;

        final CodeSparksGraphics graphics = getGraphics(totalWidth, lineHeight);

        // Thread metaphor
        graphics.setColor(VisConstants.BORDER_COLOR);

        final int barrierXPos = threadMetaphorWidth / 2;

        // Leading arrow
        graphics.fillRect(X_OFFSET_LEFT, lineHeight / 2, barrierXPos - 1, 1);
        graphics.drawLine(X_OFFSET_LEFT + barrierXPos - 4, lineHeight / 2 - 3, X_OFFSET_LEFT + barrierXPos - 1, lineHeight / 2);
        graphics.drawLine(X_OFFSET_LEFT + barrierXPos - 4, lineHeight / 2 + 3, X_OFFSET_LEFT + barrierXPos - 1, lineHeight / 2);

        // Vertical bar or barrier, respectively
        final int barrierWidth = 3;
        graphics.fillRect(X_OFFSET_LEFT + barrierXPos, 0, barrierWidth, lineHeight);

        final Rectangle threadVisualisationArea = new Rectangle(X_OFFSET_LEFT + threadMetaphorWidth, 0, barChartWidth - 1, lineHeight - 1);

        graphics.drawRectangle(threadVisualisationArea);

        final double totalThreadFilteredMetricValueOfAllNeighborsOfLine =
                getTotalThreadFilteredMetricValueOfAllNeighborsOfLine(threadFilteredNeighborArtifactsOfLine);

        final AThreadArtifactClusteringStrategy kbdeClusteringStrategy = KernelBasedDensityEstimationClustering.getInstance(primaryMetricIdentifier);
        ThreadArtifactClustering selectedClustering = artifact.getSelectedClusteringOrApplyAndSelect(kbdeClusteringStrategy);

        final long numberOfNonEmptyThreadClusters = selectedClustering
                .stream()
                .filter(cl -> cl.stream()
                        .anyMatch(AThreadArtifact::isSelected))
                .count();
        if (numberOfNonEmptyThreadClusters > 3)
        {
            selectedClustering = artifact.clusterThreadArtifacts(ApacheKMeansPlusPlus.getInstance(primaryMetricIdentifier, 3));
        }

        final VisualThreadClusterPropertiesManager clusterPropertiesManager = VisualThreadClusterPropertiesManager.getInstance(selectedClustering);
        final Map<ThreadArtifactCluster, Integer> drawPositions = ThreadVisualizationUtil.getDrawPositions(selectedClustering, clusterPropertiesManager);

        int clusterNum = 0;
        for (final ThreadArtifactCluster threadCluster : selectedClustering)
        {
            if (threadCluster.stream().noneMatch(AThreadArtifact::isSelected))
            {
                clusterNum += 1;
                continue;
            }
            final VisualThreadClusterProperties properties = clusterPropertiesManager.getOrDefault(threadCluster, clusterNum);
            final JBColor clusterColor = properties.getColor();

            final Color backgroundMetricColor = VisualizationUtil.getBackgroundMetricColor(clusterColor, .35f);
            final JBColor clusterMetricValueSumColor = new JBColor(backgroundMetricColor, backgroundMetricColor);

            graphics.setColor(clusterMetricValueSumColor);

            // Draw the sum metric value
            double percent = getThreadFilteredClusterMetricValueOfLineRelativeToTotal(
                    threadFilteredNeighborArtifactsOfLine
                    , threadCluster
                    , totalThreadFilteredMetricValueOfAllNeighborsOfLine
                    , false
            );
            percent = Math.min(1., percent); // Possible floating point arithmetic rounding errors!
            int clusterWidth = ThreadVisualizationUtil.getDiscreteTenValuedScaleWidth(percent, clusterBarMaxWidth);
            final int positionToDrawCluster = drawPositions.get(threadCluster);
            final int yPositionToDraw = initialThreadSquareYPos - positionToDrawCluster * threadSquareOffset;
            graphics.fillRect(X_OFFSET_LEFT + threadMetaphorWidth + 2, yPositionToDraw, clusterWidth, threadSquareEdgeLength);

            // Draw the average metric value
            percent = getThreadFilteredClusterMetricValueOfLineRelativeToTotal(
                    threadFilteredNeighborArtifactsOfLine
                    , threadCluster
                    , totalThreadFilteredMetricValueOfAllNeighborsOfLine
                    , true
            );
            clusterWidth = ThreadVisualizationUtil.getDiscreteTenValuedScaleWidth(percent, clusterBarMaxWidth);
            graphics.setColor(clusterColor);
            graphics.fillRect(X_OFFSET_LEFT + threadMetaphorWidth + 2, yPositionToDraw, clusterWidth, threadSquareEdgeLength);

            // --
            if (clusterWidth > 0)
            {
                // Arrows after barrier
                graphics.fillRect(X_OFFSET_LEFT + barrierXPos + barrierWidth, yPositionToDraw + 1, barrierXPos - 1, 1);
            }

            clusterNum = clusterNum + 1;
        }

        return makeLabel(graphics);
    }

    private double getTotalThreadFilteredMetricValueOfAllNeighborsOfLine(final List<ANeighborArtifact> threadFilteredNeighborArtifactsOfLine)
    {
        //noinspection UnnecessaryLocalVariable
        final double totalThreadFilteredMetricValueOfAllNeighborsOfLine =
                threadFilteredNeighborArtifactsOfLine.stream().mapToDouble(neighbor -> {
                            final List<AThreadArtifact> neighborNonFilteredThreadArtifacts =
                                    neighbor.getThreadArtifacts().stream().filter(AThreadArtifact::isSelected).collect(Collectors.toList());
                            final double neighborNumericalMetricValue = neighbor.getNumericalMetricValue(primaryMetricIdentifier);
                            double neighborTotal = 0;
                            for (final AThreadArtifact neighborThread : neighborNonFilteredThreadArtifacts)
                            {
                                neighborTotal += neighborThread.getNumericalMetricValue(primaryMetricIdentifier) * neighborNumericalMetricValue;
                            }
                            return neighborTotal;
                        }
                ).sum();
        return totalThreadFilteredMetricValueOfAllNeighborsOfLine;
    }

    private double getThreadFilteredClusterMetricValueOfLineRelativeToTotal(
            final List<ANeighborArtifact> neighborArtifacts
            , final List<AThreadArtifact> threadCluster
            , final double totalRuntimeOfAllNeighborsOfLine
            , final boolean average
    )
    {
        final List<ANeighborArtifact> neighborArtifactsExecutedByThreadsOfTheCluster =
                neighborArtifacts.stream()
                        .filter(neighbor -> neighbor.getThreadArtifacts()
                                .stream()
                                .anyMatch(threadExecutingNeighbor -> threadExecutingNeighbor.isSelected() &&
                                        threadCluster.stream()
                                                .anyMatch(threadOfCluster -> threadOfCluster.isSelected() &&
                                                        threadOfCluster.getIdentifier().equals(threadExecutingNeighbor.getIdentifier()))))
                        .collect(Collectors.toList());

        double clusterRuntimeOfLine = 0;
        final Set<AThreadArtifact> threads = new HashSet<>(1 << 4);

        for (final ANeighborArtifact neighborExecutedByAnyClusterThread : neighborArtifactsExecutedByThreadsOfTheCluster)
        {
            final double neighborRuntime = neighborExecutedByAnyClusterThread.getNumericalMetricValue(primaryMetricIdentifier);
            for (final AThreadArtifact thread : threadCluster.stream().filter(AThreadArtifact::isSelected).collect(Collectors.toList()))
            {
                final AThreadArtifact neighborThread = neighborExecutedByAnyClusterThread.getThreadArtifact(thread.getIdentifier());
                if (neighborThread == null) continue;

                final double neighborThreadRuntimeRatio = neighborThread.getNumericalMetricValue(primaryMetricIdentifier);
                clusterRuntimeOfLine += (neighborRuntime / totalRuntimeOfAllNeighborsOfLine) * neighborThreadRuntimeRatio;
                if (neighborThreadRuntimeRatio > 0)
                { //
                    if (threads.stream().noneMatch(t -> t.getIdentifier().equals(neighborThread.getIdentifier())))
                    { // Maybe the same thread (identifier) executes different callees in a single line, but it must not be counted multiple times!
                        threads.add(neighborThread);
                    }
                }
            }
        }
        if (average)
        {
            return clusterRuntimeOfLine / threads.size();
        }
        return clusterRuntimeOfLine;
    }
}
