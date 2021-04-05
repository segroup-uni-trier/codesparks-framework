package de.unitrier.st.codesparks.core.visualization.thread;

import de.unitrier.st.codesparks.core.data.*;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

/*
 * Copyright (c), Oliver Moseler, 2020
 */
public final class ThreadVisualizationUtil
{
    private ThreadVisualizationUtil() { }

    public static double calculateFilteredAvgNumericalMetricRatio(
            final ThreadArtifactCluster cluster
            , final AMetricIdentifier metricIdentifier
            , final boolean ignoreFilter
    )
    {
        double sum = 0;
        int threads = 0;
        for (AThreadArtifact codeSparksThread : cluster)
        {
            if (codeSparksThread.isFiltered() && !ignoreFilter)
                continue;

            sum += codeSparksThread.getNumericalMetricValue(metricIdentifier);
            threads++;
        }
        return (threads == 0) ? 0 : sum / threads;
    }

    public static double calculateFilteredAvgNumericalMetricRatioForZoomVisualization(
            final ThreadArtifactCluster cluster
            , final Set<AThreadArtifact> selectedCodeSparksThreads
            , final AMetricIdentifier metricIdentifier
            , final boolean ignoreFilter
    )
    {
        if (!ignoreFilter)
        {
            final int size = cluster.size();
            if (size == 0) return 0;
            return cluster.stream().map(thread -> thread.getNumericalMetricValue(metricIdentifier)).reduce(Double::sum).get() / size;
        }
        selectedCodeSparksThreads.retainAll(cluster);
        final int size = selectedCodeSparksThreads.size();
        if (size == 0)
        {
            return 0;
        }
        double sum = 0;
        for (AThreadArtifact codeSparksThread : selectedCodeSparksThreads)
        {
            sum += codeSparksThread.getNumericalMetricValue(metricIdentifier);
        }
        return sum / size;
    }

    public static double calculateFilteredSumNumericalMetricRatio(
            final ThreadArtifactCluster cluster
            , final AMetricIdentifier metricIdentifier
            , final boolean ignoreFilter
    )
    {
        if (ignoreFilter)
        {
            final Optional<Double> reduce = cluster.stream().map(thread -> thread.getNumericalMetricValue(metricIdentifier)).reduce(Double::sum);
            if (reduce.isPresent())
            {
                return reduce.get();
            }
            return 0;
        }
        double sum = 0;
        for (AThreadArtifact codeSparksThread : cluster)
        {
            if (codeSparksThread.isFiltered())
            {
                continue;
            }
            sum += codeSparksThread.getNumericalMetricValue(metricIdentifier);
        }
        return sum;
    }

    public static double calculateFilteredSumNumericalMetricRatioForZoomVisualisation(final ThreadArtifactCluster cluster,
                                                                                      final AMetricIdentifier metricIdentifier,
                                                                                      final Set<AThreadArtifact> selectedThreadArtifacts,
                                                                                      final boolean ignoreFilter)
    {
        double sum = 0;
        for (AThreadArtifact codeSparksThread : selectedThreadArtifacts)
        {
            if (!cluster.contains(codeSparksThread) && !ignoreFilter)
                continue;

            sum += codeSparksThread.getNumericalMetricValue(metricIdentifier);
        }
        return sum;
    }

    public static double getStartAngle(final int radius, final int labelRadius)
    {
        double x = (double) radius / (double) labelRadius;
        double radian = Math.PI + Math.asin(x) + 2 * Math.PI;
        double degree = Math.toDegrees(radian);
        double degreeMod = degree % 360;
        double d1 = 360 - degreeMod;
        double d2 = 360 - d1;
        double d3 = 270 - d2;
        return (d1 - 2 * d3);
    }

    public static int metricToDiscreteMetric(final double metric, final int circleDiameter)
    {
        int discreteMetric;
        if (metric <= 0.33)
        {
            discreteMetric = (int) (circleDiameter * 0.33);
        } else if (metric > 0.33 && metric <= 0.66)
        {
            discreteMetric = (int) (circleDiameter * 0.66);
        } else
        {
            discreteMetric = (int) (circleDiameter * 0.97f);
        }

        return discreteMetric;
    }

    public static int getNumberOfThreadTypesInSet(final AArtifact artifact, Set<AThreadArtifact> codeSparksThreadArtifactsSet)
    {
        if (codeSparksThreadArtifactsSet == null)
        {
            return 0;
        }
        Map<String, List<AThreadArtifact>> threadTypeLists = artifact.getThreadTypeLists();
        if (threadTypeLists == null)
        {
            return 0;
        }
        Set<String> collect = threadTypeLists.entrySet()
                .stream()
                .filter(stringListEntry -> stringListEntry.getValue()
                        .stream()
                        .anyMatch(codeSparksThreadArtifactsSet::contains)).map(Map.Entry::getKey).collect(Collectors.toSet());
        return collect.size();
    }

    public static int getNumberOfFilteredThreadTypes(final AArtifact artifact, Set<AThreadArtifact> filteredCodeSparksThreads)
    {
        if (filteredCodeSparksThreads == null)
        {
            filteredCodeSparksThreads =
                    artifact.getThreadArtifacts()
                            .stream()
                            .filter(AThreadArtifact::isFiltered)
                            .collect(Collectors.toSet());
        }
        return getNumberOfThreadTypesInSet(artifact, filteredCodeSparksThreads);
    }

    public static int getNumberOfSelectedThreadTypes(final AArtifact artifact, Set<AThreadArtifact> selectedCodeSparksThreads)
    {
        if (selectedCodeSparksThreads == null)
        {
            selectedCodeSparksThreads =
                    artifact.getThreadArtifacts()
                            .stream()
                            .filter(threadArtifact -> !threadArtifact.isFiltered())
                            .collect(Collectors.toSet());
        }
        return getNumberOfThreadTypesInSet(artifact, selectedCodeSparksThreads);
    }
}
