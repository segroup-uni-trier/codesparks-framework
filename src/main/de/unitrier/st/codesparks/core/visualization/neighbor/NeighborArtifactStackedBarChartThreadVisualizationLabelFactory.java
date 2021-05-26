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
import java.util.*;
import java.util.stream.Collectors;

@SuppressWarnings("unused")
public class NeighborArtifactStackedBarChartThreadVisualizationLabelFactory extends ANeighborArtifactVisualizationLabelFactory
{
    public NeighborArtifactStackedBarChartThreadVisualizationLabelFactory(final AMetricIdentifier primaryMetricIdentifier)
    {
        super(primaryMetricIdentifier);
    }

    public NeighborArtifactStackedBarChartThreadVisualizationLabelFactory(final AMetricIdentifier primaryMetricIdentifier, final int sequence)
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

        final double totalThreadFilteredCalleeTime = summedThreadMetricValuesOfNeighbors(threadFilteredNeighborArtifactsOfLine);

        final Comparator<ThreadArtifactCluster> threadArtifactClusterComparator =
                ThreadArtifactClusterNumericalMetricSumComparator.getInstance(primaryMetricIdentifier);

        final ThreadArtifactClustering clustering =
                artifact.clusterThreadArtifacts(ConstraintKMeansWithAMaximumOfThreeClusters.getInstance(primaryMetricIdentifier), true);

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
            for (final AThreadArtifact neighborThreadArtifact :
                    neighborArtifact.getThreadArtifacts()
                            .stream()
                            .filter(threadArtifact -> !threadArtifact.isFiltered())
                            .collect(Collectors.toList()))
            {
                final String threadArtifactIdentifier = neighborThreadArtifact.getIdentifier();
                for (final Map.Entry<ThreadArtifactCluster, Set<String>> artifactClusterSetEntry : artifactClusterSets.entrySet())
                {
                    if (artifactClusterSetEntry.getValue().contains(threadArtifactIdentifier))
                    {
                        neighborClusterSets.get(artifactClusterSetEntry.getKey()).add(neighborThreadArtifact);
                    }
                }
            }
        }

        final int lineHeight = VisualizationUtil.getLineHeightCeil(VisConstants.getLineHeight(), 3);

//        int totalThreads = totalThreads(neighborClusterSets);
        final int X_OFFSET_LEFT = 1;
        final int X_OFFSET_RIGHT = 1;

        final int visWidth = 5;

        final int totalWidth = X_OFFSET_LEFT + visWidth + X_OFFSET_RIGHT;

        final CodeSparksGraphics graphics = getGraphics(totalWidth, lineHeight);

        int yPos = lineHeight;

        int clusterNum = 0;
        final VisualThreadClusterPropertiesManager clusterPropertiesManager = VisualThreadClusterPropertiesManager.getInstance(clustering);
        for (final Map.Entry<ThreadArtifactCluster, Set<AThreadArtifact>> threadArtifactClusterSetEntry : neighborClusterSets.entrySet())
        {
            final ThreadArtifactCluster cluster = threadArtifactClusterSetEntry.getKey();
            final VisualThreadClusterProperties clusterProperties = clusterPropertiesManager.getOrDefault(cluster, clusterNum);
            final JBColor clusterColor = clusterProperties.getColor();

            double clusterThreadArtifactMetric = summedThreadMetricValues(threadArtifactClusterSetEntry.getValue());

            graphics.setColor(clusterColor);

            int clusterHeight = (int) (Math.ceil(clusterThreadArtifactMetric / totalThreadFilteredCalleeTime * lineHeight));

            graphics.fillRect(X_OFFSET_LEFT, yPos - clusterHeight, visWidth, clusterHeight);

            yPos = yPos - clusterHeight;

//            int size = threadArtifactClusterSetEntry.getValue().size();
//
//            graphics.setColor(color);
//
//            int clusterHeight = (int) ((size / (double) totalThreads) * lineHeight);
//
//            graphics.fillRect(xPos, yPos - clusterHeight, width, clusterHeight);
//
//            yPos = yPos - clusterHeight;

        }

        return makeLabel(graphics);
    }

    private int totalThreads(Map<ThreadArtifactCluster, Set<AThreadArtifact>> neighborClusterSets)
    {
        return neighborClusterSets.values().stream().mapToInt(Set::size).sum();
    }

    private double summedThreadMetricValues(Collection<AThreadArtifact> codeSparksThreads)
    {
        return codeSparksThreads
                .stream()
                .filter(codeSparksThread -> !codeSparksThread.isFiltered())
                .map(codeSparksThread -> codeSparksThread.getNumericalMetricValue(primaryMetricIdentifier)).reduce(0d, Double::sum);
    }

    private double summedThreadMetricValuesOfNeighbors(Collection<ANeighborArtifact> neighborProfilingArtifacts)
    {
        return neighborProfilingArtifacts.stream().map(aNeighborProfilingArtifact ->
                summedThreadMetricValues(aNeighborProfilingArtifact.getThreadArtifacts())
        ).reduce(0d, Double::sum);
    }
}
