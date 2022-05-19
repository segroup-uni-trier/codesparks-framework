/*
 * Copyright (c) 2021. Oliver Moseler
 */
package de.unitrier.st.codesparks.core.visualization.thread;

import de.unitrier.st.codesparks.core.data.*;

import java.util.*;
import java.util.stream.Collectors;

public final class ThreadVisualizationUtil
{
    private ThreadVisualizationUtil() {}

    public static Map<ThreadArtifactCluster, Integer> getDrawPositions(
            final ThreadArtifactClustering clustering
            , final VisualThreadClusterPropertiesManager propertiesManager)
    {
        int size = clustering.size();
        final Map<ThreadArtifactCluster, Integer> map = new HashMap<>(size);
        final Map<ThreadArtifactCluster, Integer> retMap = new HashMap<>(size);

        for (int i = 0; i < size; i++)
        {
            ThreadArtifactCluster cluster = clustering.get(i);
            if (cluster.stream().anyMatch(AThreadArtifact::isSelected))
            {
                final VisualThreadClusterProperties properties = propertiesManager.getOrDefault(cluster, i);
                map.put(cluster, properties.getPosition());
            }
        }


        int mapSize = map.size();
        if (mapSize > 3)
        {
            throw new IllegalArgumentException("No more tha three clusters are meant to contain selected thread artifacts!");
        }
        if (mapSize == 0)
        { // all threads are filtered
            assert clustering.size() <= 3;
            int clusterNum = 0;
            for (final ThreadArtifactCluster cluster : clustering)
            {
                final VisualThreadClusterProperties properties = propertiesManager.getOrDefault(cluster, clusterNum);
                retMap.put(cluster, properties.getPosition());
                clusterNum += 1;
            }
            return retMap;
        }
        final ArrayList<Map.Entry<ThreadArtifactCluster, Integer>> list = new ArrayList<>(map.entrySet());
        final Comparator<Map.Entry<ThreadArtifactCluster, Integer>> comparator = Comparator.comparingInt(Map.Entry::getValue);
        list.sort(comparator);
        //final int size = list.size();
        if (mapSize == 3)
        {
            for (int i = 0; i < mapSize; i++)
            {
                final Map.Entry<ThreadArtifactCluster, Integer> entry = list.get(i);
                retMap.put(entry.getKey(), i);
            }
        } else if (mapSize == 2)
        {
            // Only case that there are two!
            Optional<Map.Entry<ThreadArtifactCluster, Integer>> maxGreaterOrEqualTwo =
                    map.entrySet().stream().filter(entry -> entry.getValue() >= 2).max(comparator);

            if (maxGreaterOrEqualTwo.isPresent())
            {
                Map.Entry<ThreadArtifactCluster, Integer> entry = maxGreaterOrEqualTwo.get();
                retMap.put(entry.getKey(), 2);
                map.remove(entry.getKey());

                //
                Optional<Map.Entry<ThreadArtifactCluster, Integer>> greaterOrEqualOne =
                        map.entrySet().stream().filter(entry1 -> entry1.getValue() >= 1).findFirst();
                if (greaterOrEqualOne.isPresent())
                {
                    Map.Entry<ThreadArtifactCluster, Integer> entry1 = greaterOrEqualOne.get();
                    retMap.put(entry1.getKey(), 1);
                    map.remove(entry1.getKey());
                } else
                {
                    Optional<Map.Entry<ThreadArtifactCluster, Integer>> any = map.entrySet().stream().findAny();
                    assert any.isPresent(); // We know that there are
                    Map.Entry<ThreadArtifactCluster, Integer> entry1 = any.get();
                    retMap.put(entry1.getKey(), 0);
                    map.remove(entry1.getKey());
                }

            } else
            {
                Optional<Map.Entry<ThreadArtifactCluster, Integer>> any =
                        map.entrySet().stream().filter(elem -> elem.getValue() == 1).findAny();
                assert any.isPresent();
                Map.Entry<ThreadArtifactCluster, Integer> entry = any.get();
                retMap.put(entry.getKey(), 1);
                map.remove(entry.getKey());

                Optional<Map.Entry<ThreadArtifactCluster, Integer>> any1 =
                        map.entrySet().stream().filter(elem -> elem.getValue() == 0).findAny();
                assert any1.isPresent();
                Map.Entry<ThreadArtifactCluster, Integer> entry1 = any1.get();
                retMap.put(entry1.getKey(), 0);
                map.remove(entry1.getKey());
            }
        } else
        {
            // size == 1
            final Map.Entry<ThreadArtifactCluster, Integer> entry = list.get(0);
            int pos = Math.max(0, Math.min(2, entry.getValue()));
            retMap.put(entry.getKey(), pos);
        }

//        try
//        {
//           assert map.values().stream().distinct().count() == map.values().size();
//        } catch (AssertionError a)
//        {
//            ObjectOutputStream cOut = null;
//            ObjectOutputStream pOut = null;
//            try
//            {
//                cOut = new ObjectOutputStream(new FileOutputStream("/home/moseler/Desktop/test-clustering.ser"));
//                cOut.writeObject(clustering);
//
//                pOut = new ObjectOutputStream(new FileOutputStream("/home/moseler/Desktop/test-clusterPropertiesManager.ser"));
//                pOut.writeObject(propertiesManager);
//            } catch (IOException e)
//            {
//                e.printStackTrace();
//            } finally
//            {
//                if (cOut != null)
//                {
//                    try
//                    {
//                        cOut.flush();
//                        cOut.close();
//                    } catch (IOException e)
//                    {
//                        // ignored
//                    }
//                }
//                if (pOut != null)
//                {
//                    try
//                    {
//                        pOut.flush();
//                        pOut.close();
//                    } catch (IOException e)
//                    {
//                        // ignored
//                    }
//                }
//            }
//
//            //throw a;
//        }

        return retMap;
    }

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

    public static int getDiscreteXValuedScaleWidth(final int x, final double percent, int maxWidth)
    {
        int discreteWidth = 0;
        if (percent > 0D)
        {
            if (percent >= 1d)
            {
                return maxWidth;
            }
            if (x < 20)
            {
                return (int) (maxWidth * percent);
            }
            maxWidth = (int) (maxWidth * ((x - 1) / (double) x));
            double discreteStep = 100d / x;
            int discrete = (int) ((percent * 100) / discreteStep + 0.9999);
            discreteWidth = (int) (maxWidth * ((double) discrete / x));
        }
        return discreteWidth;
    }

    // Used in ThreadFork
    public static double getMetricValueSumOfSelectedThreads(final AArtifact artifact, final AMetricIdentifier metricIdentifier,
                                                            final boolean ignoreTheFilteredFlagOfThreads)
    {
        //noinspection UnnecessaryLocalVariable
        final double sum =
                artifact.getThreadArtifacts().stream().filter(threadArtifact -> ignoreTheFilteredFlagOfThreads || threadArtifact.isSelected())
                        .mapToDouble(threadArtifact -> threadArtifact.getNumericalMetricValue(metricIdentifier)).sum();
        return sum;
    }

    // Used in ThreadFork
    public static double getMetricValueAverageOfSelectedThreadsOfTheClusterRelativeToTotal(
            final AMetricIdentifier metricIdentifier,
            final Collection<AThreadArtifact> threadsOfArtifact,
            final Collection<AThreadArtifact> threadArtifactsOfCluster,
            final double total,
            final boolean createDisabledViz
    )
    {
        final OptionalDouble average =
                threadsOfArtifact.stream().filter(threadExecutingArtifact -> (createDisabledViz || threadExecutingArtifact.isSelected()) && threadArtifactsOfCluster.stream().anyMatch(
                        clusterThread -> (createDisabledViz || clusterThread.isSelected()) && clusterThread.getIdentifier().equals(threadExecutingArtifact.getIdentifier())
                )).mapToDouble(threadExecutingArtifact -> threadExecutingArtifact.getNumericalMetricValue(metricIdentifier)).average();
        if (average.isPresent())
        {
            //noinspection UnnecessaryLocalVariable
            final double ratio = average.getAsDouble() / total;
            return ratio;
        }
        return Double.NaN;
    }

    // Used in ZoomedThreadFork
    public static double getMetricValueAverageOfCurrentSelectionOfThreadsOfTheClusterRelativeToTotal(
            final AMetricIdentifier metricIdentifier,
            final Collection<AThreadArtifact> threadsOfArtifact,
            final Collection<AThreadArtifact> threadArtifactsOfCluster,
            final double total
            // ,  final boolean createDisabledViz
    )
    {
        final OptionalDouble average =
                threadsOfArtifact
                        .stream()
                        .filter(threadExecutingArtifact -> threadArtifactsOfCluster.stream().anyMatch(
                                clusterThread -> clusterThread.getIdentifier().equals(threadExecutingArtifact.getIdentifier())
                        )).mapToDouble(threadExecutingArtifact -> threadExecutingArtifact.getNumericalMetricValue(metricIdentifier))
                        .average();
        if (average.isPresent())
        {
            //noinspection UnnecessaryLocalVariable
            final double ratio = average.getAsDouble() / total;
            return ratio;
        }
        return Double.NaN;
    }

    // Used in ThreadFork
    public static double getMetricValueSumOfSelectedThreadsOfTheClusterRelativeToTotal(
            final AMetricIdentifier metricIdentifier,
            final Collection<AThreadArtifact> threadsOfArtifact,
            final Collection<AThreadArtifact> threadArtifactsOfCluster,
            final double total,
            final boolean createDisabledViz
    )
    {
        final double sum =
                threadsOfArtifact.stream().filter(threadExecutingArtifact -> (createDisabledViz || threadExecutingArtifact.isSelected()) && threadArtifactsOfCluster.stream().anyMatch(
                        clusterThread -> (createDisabledViz || clusterThread.isSelected()) && clusterThread.getIdentifier().equals(threadExecutingArtifact.getIdentifier())
                )).mapToDouble(threadExecutingArtifact -> threadExecutingArtifact.getNumericalMetricValue(metricIdentifier)).sum();
        //noinspection UnnecessaryLocalVariable
        final double ratio = sum / total;
        return ratio;
    }

    // Used in ZoomedThreadFork -> ClusterButtonFillStrategy
    public static double getMetricValueSumOfCurrentSelectionOfThreadsOfTheClusterRelativeToTotal(
            final AMetricIdentifier metricIdentifier,
            final Collection<AThreadArtifact> selectedThreadArtifacts,
            final Collection<AThreadArtifact> threadArtifactsOfCluster,
            final double total

            // final boolean createDisabledViz
    )
    {
        final double sum =
                selectedThreadArtifacts
                        .stream()
                        .filter(threadExecutingArtifact -> threadArtifactsOfCluster.stream().anyMatch(
                                clusterThread -> clusterThread.getIdentifier().equals(threadExecutingArtifact.getIdentifier())
                        )).mapToDouble(threadExecutingArtifact -> threadExecutingArtifact.getNumericalMetricValue(metricIdentifier))
                        .sum();
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

    /*
     * Thread types
     */

    private static int getNumberOfSelectedThreadTypesWithNumericMetricValueInSet(
            final AArtifact artifact
            , final AMetricIdentifier metricIdentifier
            , final Set<AThreadArtifact> threadArtifactsSet
            , final boolean ignoreFilteredFlagOfThreads
    )
    {
        if (threadArtifactsSet == null)
        {
            return 0;
        }
        Map<String, List<AThreadArtifact>> threadTypeLists;
        if (ignoreFilteredFlagOfThreads)
        {
            threadTypeLists = artifact.getThreadTypeListsOfThreadsWithNumericMetricValue(metricIdentifier);
        } else
        {
            threadTypeLists = artifact.getThreadTypeListsOfSelectedThreadsWithNumericMetricValue(metricIdentifier);
        }
        if (threadTypeLists == null)
        {
            return 0;
        }
        final Set<String> collect = threadTypeLists.entrySet()
                .stream()
                .filter(entry -> entry.getValue().stream()
                        .anyMatch(threadArtifactsSet::contains)).map(Map.Entry::getKey).collect(Collectors.toSet());
        return collect.size();
    }

    public static int getNumberOfSelectedThreadTypesWithNumericMetricValueInSelection(
            final AArtifact artifact
            , final AMetricIdentifier metricIdentifier
            , Set<AThreadArtifact> selectedThreadArtifacts
    )
    {
        if (selectedThreadArtifacts == null)
        {
            selectedThreadArtifacts = new HashSet<>(artifact.getSelectedThreadArtifactsWithNumericMetricValue(metricIdentifier));
        }
        return getNumberOfSelectedThreadTypesWithNumericMetricValueInSet(artifact, metricIdentifier, selectedThreadArtifacts, false);
    }

    public static int getNumberOfSelectedThreadTypesWithNumericMetricValueInSelection(
            final AArtifact artifact
            , final AMetricIdentifier metricIdentifier
            , Set<AThreadArtifact> selectedThreadArtifacts
            , final boolean ignoreFilteredFlagOfThreads
    )
    {
        if (selectedThreadArtifacts == null)
        {
            selectedThreadArtifacts = new HashSet<>(artifact.getSelectedThreadArtifactsWithNumericMetricValue(metricIdentifier));
        }
        return getNumberOfSelectedThreadTypesWithNumericMetricValueInSet(artifact, metricIdentifier, selectedThreadArtifacts,
                ignoreFilteredFlagOfThreads);
    }

    public static int getNumberOfSelectedThreadTypesWithNumericMetricValueInSelection(
            final AArtifact artifact
            , final AMetricIdentifier metricIdentifier
    )
    {
        return getNumberOfSelectedThreadTypesWithNumericMetricValueInSelection(artifact, metricIdentifier, null, false);
    }

    public static int getNumberOfSelectedThreadTypesWithNumericMetricValueInSelection(
            final AArtifact artifact
            , final AMetricIdentifier metricIdentifier
            , final boolean ignoreFilteredFlagOfThreads
    )
    {
        return getNumberOfSelectedThreadTypesWithNumericMetricValueInSelection(artifact, metricIdentifier, null,
                ignoreFilteredFlagOfThreads);
    }

    public static int getNumberOfSelectedThreadTypesWithNumericMetricValueInCluster(
            final AArtifact artifact
            , final AMetricIdentifier metricIdentifier
            , final ThreadArtifactCluster threadArtifactCluster
            , final boolean ignoreTheFilteredFlagOfThreads
    )
    {
        final Set<AThreadArtifact> threadArtifactSet =
                threadArtifactCluster.stream().filter(t -> ignoreTheFilteredFlagOfThreads || t.isSelected())
                        .collect(Collectors.toSet());
        return getNumberOfSelectedThreadTypesWithNumericMetricValueInSet(artifact, metricIdentifier, threadArtifactSet,
                ignoreTheFilteredFlagOfThreads);
    }
}
