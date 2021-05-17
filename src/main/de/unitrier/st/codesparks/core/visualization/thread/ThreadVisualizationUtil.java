/*
 * Copyright (c), Oliver Moseler, 2021
 */
package de.unitrier.st.codesparks.core.visualization.thread;

import de.unitrier.st.codesparks.core.data.*;

import java.util.*;
import java.util.stream.Collectors;

public final class ThreadVisualizationUtil
{
    private ThreadVisualizationUtil() { }

    // Used in ThreadFork and ZoomedThreadFork
    public static int getDiscreteTenValuedScaleWidth(final double percent, final int maxWidth)
    {
        int discreteWidth = 0;
        if (percent > 0D)
        {
            int discrete = (int) (percent * 100 / 10 + 0.9999);
            discreteWidth = maxWidth / 10 * discrete;
        }
        return discreteWidth;
    }

    // Used in ThreadFork and ZoomedThreadFork
    public static double getThreadFilteredArtifactMetricValueAverageOfClusterRelativeToTotal(
            final AMetricIdentifier metricIdentifier,
            final Collection<AThreadArtifact> threadsOfArtifact,
            final Collection<AThreadArtifact> threadArtifactsOfCluster,
            final double total,
            final boolean createDisabledViz
    )
    {
        final OptionalDouble average =
                threadsOfArtifact.stream().filter(threadExecutingArtifact -> (createDisabledViz || !threadExecutingArtifact.isFiltered()) && threadArtifactsOfCluster.stream().anyMatch(
                        clusterThread -> (createDisabledViz || !clusterThread.isFiltered()) && clusterThread.getIdentifier().equals(threadExecutingArtifact.getIdentifier())
                )).mapToDouble(threadExecutingArtifact -> threadExecutingArtifact.getNumericalMetricValue(metricIdentifier)).average();
        if (average.isPresent())
        {
            //noinspection UnnecessaryLocalVariable
            final double ratio = average.getAsDouble() / total;
            return ratio;
        }
        return Double.NaN;
    }

    // Used in ThreadFork and ZoomedThreadFork
    public static double getThreadFilteredArtifactMetricValueSumOfClusterRelativeToTotal(
            final AMetricIdentifier metricIdentifier,
            final Collection<AThreadArtifact> threadsOfArtifact,
            final Collection<AThreadArtifact> threadArtifactsOfCluster,
            final double total,
            final boolean createDisabledViz
    )
    {
        final double sum =
                threadsOfArtifact.stream().filter(threadExecutingArtifact -> (createDisabledViz || !threadExecutingArtifact.isFiltered()) && threadArtifactsOfCluster.stream().anyMatch(
                        clusterThread -> (createDisabledViz || !clusterThread.isFiltered()) && clusterThread.getIdentifier().equals(threadExecutingArtifact.getIdentifier())
                )).mapToDouble(threadExecutingArtifact -> threadExecutingArtifact.getNumericalMetricValue(metricIdentifier)).sum();
        //noinspection UnnecessaryLocalVariable
        final double ratio = sum / total;
        return ratio;
    }


    // Used in ZoomedThreadRadar
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

    // Used in ZoomedThreadRadar
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

    public static int getNumberOfThreadTypesInSet(final AArtifact artifact, Set<AThreadArtifact> threadArtifactsSet)
    {
        if (threadArtifactsSet == null)
        {
            return -1;
        }
        Map<String, List<AThreadArtifact>> threadTypeLists = artifact.getThreadTypeLists();
        if (threadTypeLists == null)
        {
            return -1;
        }
        Set<String> collect = threadTypeLists.entrySet()
                .stream()
                .filter(stringListEntry -> stringListEntry.getValue()
                        .stream()
                        .anyMatch(threadArtifactsSet::contains)).map(Map.Entry::getKey).collect(Collectors.toSet());
        return collect.size();
    }

    public static int getNumberOfThreadTypesWithNumericMetricValueInSet(
            final AArtifact artifact
            , final AMetricIdentifier metricIdentifier
            , Set<AThreadArtifact> threadArtifactsSet
    )
    {
        if (threadArtifactsSet == null)
        {
            return -1;
        }
        Map<String, List<AThreadArtifact>> threadTypeLists = artifact.getThreadTypeLists();
        if (threadTypeLists == null)
        {
            return -1;
        }
        Set<String> collect = threadTypeLists.entrySet()
                .stream()
                .filter(stringListEntry -> stringListEntry.getValue().stream()
                        .anyMatch(threadArtifact -> threadArtifactsSet.contains(threadArtifact)
                                && threadArtifact.getNumericalMetricValue(metricIdentifier) > 0)).map(Map.Entry::getKey).collect(Collectors.toSet());
        return collect.size();
    }

    public static int getNumberOfFilteredThreadTypesInSelection(final AArtifact artifact, Set<AThreadArtifact> selectedThreadArtifacts)
    {
        if (selectedThreadArtifacts == null)
        {
            selectedThreadArtifacts =
                    artifact.getThreadArtifacts()
                            .stream()
                            .filter(AThreadArtifact::isFiltered)
                            .collect(Collectors.toSet());
        }
        return getNumberOfThreadTypesInSet(artifact, selectedThreadArtifacts);
    }

    public static int getNumberOfFilteredThreadTypesInSelection(final AArtifact artifact)
    {
        return getNumberOfFilteredThreadTypesInSelection(artifact, null);
    }

    public static int getNumberOfFilteredThreadTypesWithNumericMetricValueInSelection(
            final AArtifact artifact
            , final AMetricIdentifier metricIdentifier
            , Set<AThreadArtifact> selectedThreadArtifacts
    )
    {
        if (selectedThreadArtifacts == null)
        {
            selectedThreadArtifacts =
                    artifact.getThreadArtifacts()
                            .stream()
                            .filter(threadArtifact -> threadArtifact.getNumericalMetricValue(metricIdentifier) > 0 && !threadArtifact.isFiltered())
                            .collect(Collectors.toSet());
        }
        return getNumberOfThreadTypesWithNumericMetricValueInSet(artifact, metricIdentifier, selectedThreadArtifacts);
    }

    public static int getNumberOfFilteredThreadTypesWithNumericMetricValueInSelection(final AArtifact artifact, final AMetricIdentifier metricIdentifier)
    {
        return getNumberOfFilteredThreadTypesWithNumericMetricValueInSelection(artifact, metricIdentifier, null);
    }

    public static int getNumberOfFilteredThreadTypesOfCluster(final AArtifact artifact, final ThreadArtifactCluster threadArtifactCluster)
    {
        final Set<AThreadArtifact> threadArtifactSet = threadArtifactCluster.stream().filter(threadArtifact -> !threadArtifact.isFiltered())
                .collect(Collectors.toSet());
        return getNumberOfThreadTypesInSet(artifact, threadArtifactSet);
    }

    public static int getNumberOfFilteredThreadTypesWithNumericMetricValueOfCluster(
            final AArtifact artifact
            , final AMetricIdentifier metricIdentifier
            , final ThreadArtifactCluster threadArtifactCluster
    )
    {
        final Set<AThreadArtifact> threadArtifactSet = threadArtifactCluster.stream().filter(threadArtifact -> !threadArtifact.isFiltered())
                .collect(Collectors.toSet());
        return getNumberOfThreadTypesWithNumericMetricValueInSet(artifact, metricIdentifier, threadArtifactSet);
    }
}
