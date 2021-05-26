/*
 * Copyright (c) 2021. Oliver Moseler
 */
package de.unitrier.st.codesparks.core.visualization.neighbor;

import com.intellij.ui.JBColor;
import de.unitrier.st.codesparks.core.data.*;
import de.unitrier.st.codesparks.core.visualization.CodeSparksGraphics;
import de.unitrier.st.codesparks.core.visualization.VisConstants;
import de.unitrier.st.codesparks.core.visualization.VisualizationUtil;
import de.unitrier.st.codesparks.core.visualization.popup.ThreadColor;
import de.unitrier.st.codesparks.core.visualization.thread.VisualThreadClusterProperties;
import de.unitrier.st.codesparks.core.visualization.thread.VisualThreadClusterPropertiesManager;

import javax.swing.*;
import java.awt.*;
import java.util.List;
import java.util.stream.Collectors;

public class NeighborThreadForkLabelFactory extends ANeighborArtifactVisualizationLabelFactory
{
    @SuppressWarnings("unused")
    public NeighborThreadForkLabelFactory(final AMetricIdentifier primaryMetricIdentifier)
    {
        this(primaryMetricIdentifier, 0);
    }

    public NeighborThreadForkLabelFactory(final AMetricIdentifier primaryMetricIdentifier, int sequence)
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
        final int X_OFFSET_RIGHT = 1;

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

//        ThreadArtifactClustering clustering = artifact.getConstraintKMeansWithAMaximumOfThreeClustersThreadArtifactClustering(primaryMetricIdentifier);

        final ThreadArtifactClustering clustering =
                artifact.clusterThreadArtifacts(SmileKernelDensityClustering.getInstance(primaryMetricIdentifier));

        final VisualThreadClusterPropertiesManager clusterPropertiesManager = VisualThreadClusterPropertiesManager.getInstance(clustering);
        final boolean[] positionsTaken = new boolean[3];
        int clusterNum = 0;

        for (final ThreadArtifactCluster threadCluster : clustering)
        {
            /*
             * Will be set in the respective thread clustering visualization for
             * the artifact, e.g. ThreadRadarLabelFactory or ThreadForkLabelFactory
             */
            VisualThreadClusterProperties properties = clusterPropertiesManager.getProperties(threadCluster);
            int clusterPosition = -1;
            JBColor color = ThreadColor.getNextColor(clusterNum);
            if (properties != null)
            {
                color = properties.getOrSetColor(color);
                clusterPosition = properties.getOrSetPosition(clusterNum);
            }

            final int positionIndex = findPositionToDraw(positionsTaken, clusterPosition, clusterNum);
            clusterNum = clusterNum + 1;

            if (positionIndex < 0)
            { // No more clusterPosition to draw available. Only happens when the number of clusters is set up to be greater than k=3
                break;
            }

            graphics.setColor(color);

            int clusterWidth;

            double percent = getThreadFilteredClusterMetricValueOfLineRelativeToTotal(threadFilteredNeighborArtifactsOfLine, threadCluster,
                    totalThreadFilteredMetricValueOfAllNeighborsOfLine);

            percent = Math.min(1., percent); // Possible floating point arithmetic rounding errors!

            if (percent > 0D)
            {
                int discrete = (int) (percent * 100 / 10 + 0.9999);
                clusterWidth = clusterBarMaxWidth / 10 * discrete;
            } else
            {
                clusterWidth = 0;
            }

            final int yPositionToDraw = initialThreadSquareYPos - positionIndex * threadSquareOffset;

            graphics.fillRect(X_OFFSET_LEFT + threadMetaphorWidth + 2, yPositionToDraw, clusterWidth, threadSquareEdgeLength);
            if (clusterWidth > 0)
            {
                // Arrows after barrier
                graphics.fillRect(X_OFFSET_LEFT + barrierXPos + barrierWidth, yPositionToDraw + 1, barrierXPos - 1, 1);
            }
        }

        return makeLabel(graphics);
    }

    private double getTotalThreadFilteredMetricValueOfAllNeighborsOfLine(final List<ANeighborArtifact> threadFilteredNeighborArtifactsOfLine)
    {
        //noinspection UnnecessaryLocalVariable
        final double totalThreadFilteredMetricValueOfAllNeighborsOfLine =
                threadFilteredNeighborArtifactsOfLine.stream().mapToDouble(neighbor -> {
                            final List<AThreadArtifact> neighborNonFilteredThreadArtifacts =
                                    neighbor.getThreadArtifacts().stream().filter(t -> !t.isFiltered()).collect(Collectors.toList());
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
            , final double totalRuntimeOfAllNeighborsOfLine)
    {
        final List<ANeighborArtifact> neighborArtifactsExecutedByThreadsOfTheCluster =
                neighborArtifacts.stream()
                        .filter(neighbor -> neighbor.getThreadArtifacts()
                                .stream()
                                .anyMatch(threadExecutingNeighbor -> !threadExecutingNeighbor.isFiltered() &&
                                        threadCluster.stream()
                                                .anyMatch(threadOfCluster -> !threadOfCluster.isFiltered() &&
                                                        threadOfCluster.getIdentifier().equals(threadExecutingNeighbor.getIdentifier()))))
                        .collect(Collectors.toList());

        double clusterRuntimeOfLine = 0;

        for (final ANeighborArtifact neighborExecutedByAnyClusterThread : neighborArtifactsExecutedByThreadsOfTheCluster)
        {
            final double neighborRuntime = neighborExecutedByAnyClusterThread.getNumericalMetricValue(primaryMetricIdentifier);
            for (final AThreadArtifact thread : threadCluster.stream().filter(thr -> !thr.isFiltered()).collect(Collectors.toList()))
            {
                final AThreadArtifact neighborThread = neighborExecutedByAnyClusterThread.getThreadArtifact(thread.getIdentifier());
                if (neighborThread == null) continue;
                final double neighborThreadRuntimeRatio = neighborThread.getNumericalMetricValue(primaryMetricIdentifier);
                clusterRuntimeOfLine += (neighborRuntime / totalRuntimeOfAllNeighborsOfLine) * neighborThreadRuntimeRatio;
            }
        }

        return clusterRuntimeOfLine;
    }

    private int findPositionToDraw(final boolean[] positionsTaken, final int currentPos, final int clusterNum)
    {
        if (currentPos < 0 || currentPos > positionsTaken.length - 1)
        {
            return getNextFreePos(positionsTaken, clusterNum);
        }
        if (!positionsTaken[currentPos])
        {
            positionsTaken[currentPos] = true;
            return currentPos;
        } else
        {
            return getNextFreePos(positionsTaken, currentPos);
        }
    }

    private int getNextFreePos(final boolean[] positionsTaken, final int currentPos)
    {
        for (int i = 0; i < positionsTaken.length; i++)
        {
            int nextPos = (currentPos + i) % positionsTaken.length;
            if (!positionsTaken[nextPos])
            {
                positionsTaken[nextPos] = true;
                return nextPos;
            }
        }
        return -1;
    }
}
