package de.unitrier.st.insituprofiling.core.visualization.thread;

import de.unitrier.st.insituprofiling.core.data.AProfilingArtifact;
import de.unitrier.st.insituprofiling.core.data.ThreadArtifact;
import de.unitrier.st.insituprofiling.core.data.ThreadArtifactCluster;

import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

public final class ThreadVisualizationUtil
{
    private ThreadVisualizationUtil() { }

    public static double calculateFilteredAvgRuntimeRatio(ThreadArtifactCluster cluster, boolean ignoreFilter)
    {
        double sum = 0;
        int threads = 0;
        for (ThreadArtifact aCluster : cluster)
        {
            if (aCluster.isFiltered() && !ignoreFilter)
                continue;

            sum += aCluster.getMetricValue();
            threads++;
        }
        return (threads == 0) ? 0 : sum / threads;
    }

    public static double calculateFilteredAvgRuntimeRatioForZoomVisualization(
            ThreadArtifactCluster cluster
            , Set<ThreadArtifact> selectedThreadArtifacts
            , boolean ignoreFilter
    )
    {
        if (!ignoreFilter)
        {
            final int size = cluster.size();
            if (size == 0) return 0;
            return cluster.stream().map(ThreadArtifact::getMetricValue).reduce(Double::sum).get() / size;
        }
        selectedThreadArtifacts.retainAll(cluster);
        final int size = selectedThreadArtifacts.size();
        if (size == 0)
        {
            return 0;
        }
        double sum = 0;
        for (ThreadArtifact ta : selectedThreadArtifacts)
        {
            sum += ta.getMetricValue();
        }
        return sum / size;
    }

    public static double calculateFilteredSumRuntimeRatio(ThreadArtifactCluster cluster, boolean ignoreFilter)
    {
        if (ignoreFilter)
        {
            final Optional<Double> reduce = cluster.stream().map(ThreadArtifact::getMetricValue).reduce(Double::sum);
            if (reduce.isPresent())
            {
                return reduce.get();
            }
            return 0;
        }
        double sum = 0;
        for (ThreadArtifact aCluster : cluster)
        {
            if (aCluster.isFiltered())
            {
                continue;
            }
            sum += aCluster.getMetricValue();
        }
        return sum;
    }

    public static double calculateFilteredSumRuntimeRatioForZoomVisualisation(ThreadArtifactCluster cluster,
                                                                              Set<ThreadArtifact> selectedThreadArtifacts,
                                                                              boolean ignoreFilter)
    {
        double sum = 0;
        for (ThreadArtifact ta : selectedThreadArtifacts)
        {
            if (!cluster.contains(ta) && !ignoreFilter)
                continue;

            sum += ta.getMetricValue();
        }
        return sum;
    }

    public static double getStartAngle(int radius, int labelRadius)
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

    public static int metricToDiscreteMetric(double metric, int circleDiameter)
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

    public static int getNumberOfThreadTypesInSet(AProfilingArtifact artifact, Set<ThreadArtifact> threadArtifactsSet)
    {
        if (threadArtifactsSet == null)
        {
            return 0;
        }
        Set<String> collect = artifact.getThreadTypeLists().entrySet()
                .stream()
                .filter(stringListEntry -> stringListEntry.getValue()
                        .stream()
                        .anyMatch(threadArtifactsSet::contains)).map(Map.Entry::getKey).collect(Collectors.toSet());
        return collect.size();
    }

    public static int getNumberOfFilteredThreadTypes(AProfilingArtifact artifact, Set<ThreadArtifact> filteredThreadArtifacts)
    {
        if (filteredThreadArtifacts == null)
        {
            filteredThreadArtifacts =
                    artifact.getThreadArtifacts()
                            .stream()
                            .filter(ThreadArtifact::isFiltered)
                            .collect(Collectors.toSet());
        }
        return getNumberOfThreadTypesInSet(artifact, filteredThreadArtifacts);
    }

    public static int getNumberOfSelectedThreadTypes(AProfilingArtifact artifact, Set<ThreadArtifact> selectedThreadArtifacts)
    {
        if (selectedThreadArtifacts == null)
        {
            selectedThreadArtifacts =
                    artifact.getThreadArtifacts()
                            .stream()
                            .filter(threadArtifact -> !threadArtifact.isFiltered())
                            .collect(Collectors.toSet());
        }
        return getNumberOfThreadTypesInSet(artifact, selectedThreadArtifacts);
    }
}
