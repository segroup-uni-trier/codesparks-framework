package de.unitrier.st.codesparks.core.visualization.neighbor;

import com.intellij.ui.JBColor;
import com.intellij.ui.paint.PaintUtil;
import com.intellij.util.ui.UIUtil;
import de.unitrier.st.codesparks.core.data.*;
import de.unitrier.st.codesparks.core.visualization.VisConstants;
import de.unitrier.st.codesparks.core.visualization.VisualizationUtil;
import de.unitrier.st.codesparks.core.visualization.popup.ThreadColor;
import de.unitrier.st.codesparks.core.visualization.thread.VisualThreadClusterProperties;
import de.unitrier.st.codesparks.core.visualization.thread.VisualThreadClusterPropertiesManager;

import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.List;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/*
 * Copyright (c), Oliver Moseler, 2020
 */
public class NeighborArtifactDiscreteBarChartThreadVisualizationLabelFactory extends ANeighborArtifactVisualizationLabelFactory
{
    @SuppressWarnings("unused")
    public NeighborArtifactDiscreteBarChartThreadVisualizationLabelFactory(final IMetricIdentifier primaryMetricIdentifier)
    {
        this(primaryMetricIdentifier, 0);
    }

    public NeighborArtifactDiscreteBarChartThreadVisualizationLabelFactory(final IMetricIdentifier primaryMetricIdentifier, int sequence)
    {
        super(primaryMetricIdentifier, sequence);
    }

    private double getClusterRuntime(final List<ANeighborArtifact> neighborArtifacts, final List<AThreadArtifact> cluster,
                                     double totalRuntimeOfAllNeighborsOfLine)
    {
//        final List<ANeighborArtifact> neighborArtifactsExecutedByCluster =
//                neighborArtifacts
//                        .stream()
//                        .filter(neighbor -> neighbor.getThreadArtifacts()
//                                .stream()
//                                .anyMatch(cluster::contains))
//                        .collect(Collectors.toList());

        final List<ANeighborArtifact> neighborArtifactsExecutedByCluster =
                neighborArtifacts.stream()
                        .filter(n -> n.getThreadArtifacts()
                                .stream()
                                .anyMatch(t -> !t.isFiltered() &&
                                        cluster.stream()
                                                .anyMatch(ct -> !ct.isFiltered() && ct.getIdentifier().equals(t.getIdentifier()))))
                        .collect(Collectors.toList());

        double lineRuntimeClusterThreads = 0;

        for (final ANeighborArtifact neighborExecutedByAnyClusterThread : neighborArtifactsExecutedByCluster)
        {
            final double neighborRuntime = neighborExecutedByAnyClusterThread.getNumericalMetricValue(primaryMetricIdentifier);
            for (final AThreadArtifact thread : cluster.stream().filter(thr -> !thr.isFiltered()).collect(Collectors.toList()))
            {
                final AThreadArtifact neighborThread = neighborExecutedByAnyClusterThread.getThreadArtifact(thread.getIdentifier());
                if (neighborThread == null) continue;
                final double neighborThreadRuntimeRatio = neighborThread.getNumericalMetricValue(primaryMetricIdentifier);
                lineRuntimeClusterThreads += (neighborRuntime / totalRuntimeOfAllNeighborsOfLine) * neighborThreadRuntimeRatio;
            }
        }

        return lineRuntimeClusterThreads;
    }

    @Override
    public JLabel createNeighborArtifactLabel(
            final AArtifact artifact
            , final List<ANeighborArtifact> threadFilteredNeighborArtifactsOfLine
    )
    {
        Comparator<ThreadArtifactCluster> threadClusterComparator = ThreadArtifactClusterComparator.getInstance(primaryMetricIdentifier);

        List<ThreadArtifactCluster> threadClusters =
                artifact.getDefaultThreadArtifactClustering(primaryMetricIdentifier)
                        .stream()
                        .sorted(threadClusterComparator)
                        .filter(cluster -> !cluster.isEmpty()
                                &&
                                cluster.stream().anyMatch(t -> !t.isFiltered()))
                        .collect(Collectors.toList());

        final int threadsPerColumn = 3;
        final int lineHeight = VisualizationUtil.getLineHeightFloor(VisConstants.getLineHeight(), threadsPerColumn);

        final int X_OFFSET_LEFT = 2;
        final int X_OFFSET_RIGHT = 1;

        final int threadMetaphorWidth = 24;
        final int barChartWidth = 24;

        final int clusterBarMaxWidth = 20;

        final int totalWidth = X_OFFSET_LEFT + threadMetaphorWidth + barChartWidth + X_OFFSET_RIGHT;

        final int threadSquareEdgeLength = 3;//(lineHeight - 6) / threadsPerColumn;

        final int initialThreadSquareYPos = lineHeight - threadSquareEdgeLength - 2;
        //int currentThreadSquareYPos = initialThreadSquareYPos;
        final int threadSquareOffset = threadSquareEdgeLength + 1;

        GraphicsConfiguration defaultConfiguration =
                GraphicsEnvironment.getLocalGraphicsEnvironment().getDefaultScreenDevice().getDefaultConfiguration();
        BufferedImage bi = UIUtil.createImage(defaultConfiguration, totalWidth, lineHeight, BufferedImage.TYPE_INT_ARGB,
                PaintUtil.RoundingMode.CEIL);
        Graphics2D graphics = (Graphics2D) bi.getGraphics();

        VisualizationUtil.drawTransparentBackground(graphics, bi);

        // Thread metaphor
        graphics.setColor(VisConstants.BORDER_COLOR);

        //graphics.fillRect(X_OFFSET_LEFT - 1, lineHeight / 2 - 1, 3, 3); // Leading square

        final int barrierXPos = threadMetaphorWidth / 2;

        // Leading arrow
        graphics.fillRect(X_OFFSET_LEFT, lineHeight / 2, barrierXPos - 1, 1);
        graphics.drawLine(X_OFFSET_LEFT + barrierXPos - 3, lineHeight / 2 - 3, X_OFFSET_LEFT + barrierXPos, lineHeight / 2);
        graphics.drawLine(X_OFFSET_LEFT + barrierXPos - 3, lineHeight / 2 + 3, X_OFFSET_LEFT + barrierXPos, lineHeight / 2);

        // Vertical bar or barrier, respectively
        final int barrierWidth = 3;
        graphics.fillRect(X_OFFSET_LEFT + barrierXPos, 0, barrierWidth, lineHeight);


//        graphics.fillRect(X_OFFSET_LEFT + threadMetaphorWidth / 2, 2, 1, lineHeight - 4); // vertical
//        graphics.fillRect(X_OFFSET_LEFT + threadMetaphorWidth / 2, 2, threadMetaphorWidth / 2, 1); // top horizontal
//        graphics.fillRect(X_OFFSET_LEFT + threadMetaphorWidth / 2, lineHeight - 3, threadMetaphorWidth / 2, 1); // bottom horizontal

        //
        Rectangle threadVisualisationArea = new Rectangle(
                X_OFFSET_LEFT + threadMetaphorWidth, 0, barChartWidth - 1, lineHeight - 1);

        VisualizationUtil.drawRectangle(graphics, threadVisualisationArea);
        int clusterCnt = 0;
        VisualThreadClusterPropertiesManager clusterPropertiesManager = VisualThreadClusterPropertiesManager.getInstance();

        final boolean[] positionsTaken = new boolean[3];

        final double totalMetricValueOfAllNeighborsOfLine =
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

        for (ThreadArtifactCluster threadCluster : threadClusters)
        {
            VisualThreadClusterProperties properties = clusterPropertiesManager.getProperties(threadCluster); // Will be set in the respective thread
            // clustering visualization for the artifact, e.g. ThreadRadarLabelFactory or ThreadForkLabelFactory
            int position = -1;
            JBColor color;
            if (properties != null)
            {
                color = properties.getColor();
                position = properties.getPosition();
            } else
            {
                color = ThreadColor.getNextColor(clusterCnt++);
            }


            final int positionIndex = findPositionToDraw(positionsTaken, position, clusterCnt);

            if (positionIndex < 0)
            { // No more position to draw available. Only happens when the number of clusters is set up to be greater than k=3
                break;
            }

            graphics.setColor(color);

            int clusterWidth;

            double percent = getClusterRuntime(threadFilteredNeighborArtifactsOfLine, threadCluster, totalMetricValueOfAllNeighborsOfLine);

            percent = Math.min(1., percent); // Possible floating point arithmetic rounding errors!

            if (percent > 0D)
            {
                int discrete = (int) (percent * 100 / 10 + 0.9999);
                clusterWidth = clusterBarMaxWidth / 10 * discrete;
            } else
            {
                clusterWidth = 0;
            }

            final int positionToDraw = initialThreadSquareYPos - positionIndex * threadSquareOffset;

            graphics.fillRect(X_OFFSET_LEFT + threadMetaphorWidth + 2, positionToDraw, clusterWidth, threadSquareEdgeLength);
            if (clusterWidth > 0)
            {
                // Arrows after barrier
                graphics.fillRect(X_OFFSET_LEFT + barrierXPos + barrierWidth, positionToDraw + 1, barrierXPos - 1, 1);
            }
            //currentThreadSquareYPos -= threadSquareOffset;
        }

        BufferedImage subimage = bi.getSubimage(0, 0, bi.getWidth(), bi.getHeight());
        ImageIcon imageIcon = new ImageIcon(subimage);

        JLabel jLabel = new JLabel();
        jLabel.setIcon(imageIcon);

        jLabel.setSize(imageIcon.getIconWidth(), imageIcon.getIconHeight());
        // jLabel.addMouseListener(??);

        return jLabel;
    }

    private int findPositionToDraw(final boolean[] positionsTaken, final int currentPos, final int clusterCnt)
    {
        if (currentPos < 0)
        {
            return getNextFreePos(positionsTaken, clusterCnt);
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
            int nextPos = currentPos + i % positionsTaken.length;
            if (!positionsTaken[nextPos])
            {
                positionsTaken[nextPos] = true;
                return nextPos;
            }
        }
        return -1;
    }
}
