/*
 * Copyright (c) 2021. Oliver Moseler
 */
package de.unitrier.st.codesparks.core.visualization.neighbor;

import com.intellij.ui.JBColor;
import de.unitrier.st.codesparks.core.data.*;
import de.unitrier.st.codesparks.core.visualization.CodeSparksGraphics;
import de.unitrier.st.codesparks.core.visualization.VisConstants;
import de.unitrier.st.codesparks.core.visualization.VisualizationUtil;
import de.unitrier.st.codesparks.core.visualization.thread.VisualThreadClusterProperties;
import de.unitrier.st.codesparks.core.visualization.thread.VisualThreadClusterPropertiesManager;

import javax.swing.*;
import java.awt.*;
import java.util.List;
import java.util.*;
import java.util.stream.Collectors;

@SuppressWarnings("unused")
public class NeighborArtifactThreadDotVisualizationLabelFactory extends ANeighborArtifactVisualizationLabelFactory
{
    public NeighborArtifactThreadDotVisualizationLabelFactory(final AMetricIdentifier primaryMetricIdentifier)
    {
        super(primaryMetricIdentifier);
    }

    public NeighborArtifactThreadDotVisualizationLabelFactory(final AMetricIdentifier primaryMetricIdentifier, final int sequence)
    {
        super(primaryMetricIdentifier, sequence);
    }

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

        final Comparator<ThreadArtifactCluster> threadArtifactClusterComparator =
                ThreadArtifactClusterNumericalMetricSumComparator.getInstance(primaryMetricIdentifier);

        final ThreadArtifactClustering clustering =
                artifact.clusterThreadArtifacts(ConstraintKMeansWithAMaximumOfThreeClusters.getInstance(primaryMetricIdentifier), true);
//                artifact.getConstraintKMeansWithAMaximumOfThreeClustersThreadArtifactClustering(primaryMetricIdentifier);
//                        .stream()
//                        .sorted(threadArtifactClusterComparator)
//                        .filter(cluster -> !cluster.isEmpty())
//                        .collect(Collectors.toList());

        final SortedMap<ThreadArtifactCluster, Set<String>> artifactClusterSets =
                new TreeMap<>(threadArtifactClusterComparator);
        final SortedMap<ThreadArtifactCluster, Set<AThreadArtifact>> neighborClusterSets =
                new TreeMap<>(threadArtifactClusterComparator);

        for (final ThreadArtifactCluster threadCluster : clustering)
        {
            artifactClusterSets.put(threadCluster,
                    new HashSet<>(threadCluster.stream().map(AThreadArtifact::getIdentifier).collect(Collectors.toList())));
            neighborClusterSets.put(threadCluster, new HashSet<>());
        }

        for (final ANeighborArtifact neighborArtifact : threadFilteredNeighborArtifactsOfLine)
        {
            for (final AThreadArtifact neighborArtifactThread :
                    neighborArtifact.getThreadArtifacts()
                            .stream()
                            .filter(threadArtifact -> !threadArtifact.isFiltered())
                            .collect(Collectors.toList()))
            {
                final String threadArtifactIdentifier = neighborArtifactThread.getIdentifier();
                for (final Map.Entry<ThreadArtifactCluster, Set<String>> artifactClusterSetEntry : artifactClusterSets.entrySet())
                {
                    if (artifactClusterSetEntry.getValue().contains(threadArtifactIdentifier))
                    {
                        neighborClusterSets.get(artifactClusterSetEntry.getKey()).add(neighborArtifactThread);
                    }
                }
            }
        }

        final int X_OFFSET_LEFT = 1;
        final int X_OFFSET_RIGHT = 2;

        final int visWidth = 7;

        final int threadsPerColumn = 3;
        final int lineHeight = VisualizationUtil.getLineHeightCeil(VisConstants.getLineHeight(), threadsPerColumn);

        final int threadSquareEdgeLength = (lineHeight - 6) / threadsPerColumn;

        final int BORDER_RECTANGLE_STROKE_WIDTH = 1;
        final int THREAD_DOT_X_OFFSET = X_OFFSET_LEFT + BORDER_RECTANGLE_STROKE_WIDTH + 1;

        int threadSquareYPos = lineHeight - threadSquareEdgeLength - 2;
        final int threadSquareOffset = threadSquareEdgeLength + 1;

        int totalWidth = X_OFFSET_LEFT + visWidth + X_OFFSET_RIGHT;

        final CodeSparksGraphics graphics = getGraphics(totalWidth, lineHeight);

        final Rectangle threadVisualisationArea = new Rectangle(
                X_OFFSET_LEFT, 0, visWidth, lineHeight - 1);

        graphics.drawRectangle(threadVisualisationArea, VisConstants.BORDER_COLOR);

        final VisualThreadClusterPropertiesManager clusterPropertiesManager = VisualThreadClusterPropertiesManager.getInstance(clustering);

        int clusterNum = 0;
        for (final Map.Entry<ThreadArtifactCluster, Set<AThreadArtifact>> threadArtifactClusterSetEntry : neighborClusterSets.entrySet())
        {
            final ThreadArtifactCluster cluster = threadArtifactClusterSetEntry.getKey();
            final VisualThreadClusterProperties properties = clusterPropertiesManager.getOrDefault(cluster, clusterNum);
            final JBColor color = properties.getColor();

            if (threadArtifactClusterSetEntry.getValue().size() > 0)
            {
                graphics.setColor(color);
                graphics.fillRect(THREAD_DOT_X_OFFSET, threadSquareYPos,
                        threadSquareEdgeLength, threadSquareEdgeLength);
            }
            threadSquareYPos -= threadSquareOffset;
        }

        return makeLabel(graphics);
    }
}
