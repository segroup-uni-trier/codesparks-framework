package de.unitrier.st.codesparks.core.visualization.thread;

import de.unitrier.st.codesparks.core.data.AArtifact;
import de.unitrier.st.codesparks.core.data.ACodeSparksThread;
import de.unitrier.st.codesparks.core.data.CodeSparksThreadCluster;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

public final class ThreadVisualizationUtil
{
    private ThreadVisualizationUtil() { }

    public static double calculateFilteredAvgRuntimeRatio(CodeSparksThreadCluster cluster, boolean ignoreFilter)
    {
        double sum = 0;
        int threads = 0;
        for (ACodeSparksThread aCluster : cluster)
        {
            if (aCluster.isFiltered() && !ignoreFilter)
                continue;

            sum += aCluster.getMetricValue();
            threads++;
        }
        return (threads == 0) ? 0 : sum / threads;
    }

    public static double calculateFilteredAvgRuntimeRatioForZoomVisualization(
            CodeSparksThreadCluster cluster
            , Set<ACodeSparksThread> selectedCodeSparksThreads
            , boolean ignoreFilter
    )
    {
        if (!ignoreFilter)
        {
            final int size = cluster.size();
            if (size == 0) return 0;
            return cluster.stream().map(ACodeSparksThread::getMetricValue).reduce(Double::sum).get() / size;
        }
        selectedCodeSparksThreads.retainAll(cluster);
        final int size = selectedCodeSparksThreads.size();
        if (size == 0)
        {
            return 0;
        }
        double sum = 0;
        for (ACodeSparksThread ta : selectedCodeSparksThreads)
        {
            sum += ta.getMetricValue();
        }
        return sum / size;
    }

    public static double calculateFilteredSumRuntimeRatio(CodeSparksThreadCluster cluster, boolean ignoreFilter)
    {
        if (ignoreFilter)
        {
            final Optional<Double> reduce = cluster.stream().map(ACodeSparksThread::getMetricValue).reduce(Double::sum);
            if (reduce.isPresent())
            {
                return reduce.get();
            }
            return 0;
        }
        double sum = 0;
        for (ACodeSparksThread aCluster : cluster)
        {
            if (aCluster.isFiltered())
            {
                continue;
            }
            sum += aCluster.getMetricValue();
        }
        return sum;
    }

    public static double calculateFilteredSumRuntimeRatioForZoomVisualisation(CodeSparksThreadCluster cluster,
                                                                              Set<ACodeSparksThread> selectedCodeSparksThreads,
                                                                              boolean ignoreFilter)
    {
        double sum = 0;
        for (ACodeSparksThread ta : selectedCodeSparksThreads)
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

    public static int getNumberOfThreadTypesInSet(AArtifact artifact, Set<ACodeSparksThread> codeSparksThreadArtifactsSet)
    {
        if (codeSparksThreadArtifactsSet == null)
        {
            return 0;
        }
        Map<String, List<ACodeSparksThread>> threadTypeLists = artifact.getThreadTypeLists();
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

    public static int getNumberOfFilteredThreadTypes(AArtifact artifact, Set<ACodeSparksThread> filteredCodeSparksThreads)
    {
        if (filteredCodeSparksThreads == null)
        {
            filteredCodeSparksThreads =
                    artifact.getThreadArtifacts()
                            .stream()
                            .filter(ACodeSparksThread::isFiltered)
                            .collect(Collectors.toSet());
        }
        return getNumberOfThreadTypesInSet(artifact, filteredCodeSparksThreads);
    }

    public static int getNumberOfSelectedThreadTypes(AArtifact artifact, Set<ACodeSparksThread> selectedCodeSparksThreads)
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
