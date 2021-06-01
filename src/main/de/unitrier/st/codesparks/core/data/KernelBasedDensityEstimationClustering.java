/*
 * Copyright (c) 2021. Oliver Moseler
 */

package de.unitrier.st.codesparks.core.data;

import smile.stat.distribution.KernelDensity;

import java.util.*;

public class KernelBasedDensityEstimationClustering extends AThreadArtifactClusteringStrategy
{
    private static final Map<AMetricIdentifier, AThreadArtifactClusteringStrategy> instances = new HashMap<>(4);

    public static AThreadArtifactClusteringStrategy getInstance(final AMetricIdentifier metricIdentifier)
    {
        AThreadArtifactClusteringStrategy instance;
        synchronized (instances)
        {
            instance = instances.get(metricIdentifier);
            if (instance == null)
            {
                instance = new KernelBasedDensityEstimationClustering(metricIdentifier);
                instances.put(metricIdentifier, instance);
            }
        }
        return instance;
    }

    private KernelBasedDensityEstimationClustering(final AMetricIdentifier metricIdentifier)
    {
        super(metricIdentifier);
    }

    @Override
    public ThreadArtifactClustering clusterThreadArtifacts(final Collection<AThreadArtifact> threadArtifacts)
    {
        final ThreadArtifactClustering threadArtifactClusters = new ThreadArtifactClustering(this);
        final int size = threadArtifacts.size();
        if (size < 2)
        {
            final ThreadArtifactCluster cluster = new ThreadArtifactCluster();
            cluster.addAll(threadArtifacts);
            threadArtifactClusters.add(cluster);
            return threadArtifactClusters;
        }

        final Map<Double, Collection<AThreadArtifact>> metricMap = new HashMap<>(size);
        final double[] metricValues = new double[size];
        double maxMetricValue = Double.MIN_VALUE;
        double minMetricValue = Double.MAX_VALUE;
        final AMetricIdentifier metricIdentifier = getMetricIdentifier();
        int i = 0;
        for (final AThreadArtifact threadArtifact : threadArtifacts)
        {
            final double metricValue = Math.max(0.001, ((int)(threadArtifact.getNumericalMetricValue(metricIdentifier) * 100)) / 100d);
            maxMetricValue = Math.max(maxMetricValue, metricValue);
            minMetricValue = Math.min(minMetricValue, metricValue);
            metricValues[i++] = metricValue;
            Collection<AThreadArtifact> threads = metricMap.get(metricValue);
            if (threads == null)
            {
                threads = new ArrayList<>(1 << 4);
            }
            threads.add(threadArtifact);
            metricMap.put(metricValue, threads);
        }

        final double bandwidth = 0.01;
        final KernelDensity kernelDensity = new KernelDensity(metricValues, bandwidth);

        final double step = 0.001; // That we don't miss a value. This is the smallest difference we distinguish.
//        final double step = (maxMetricValue - minMetricValue) / (4 * size);

        double splitValue = 0;
        boolean firstRun = true;
        boolean minFound = false;
        double previousProbability = 0d;
        double delta;
        for (double currentValue = minMetricValue; currentValue < maxMetricValue; currentValue += step)
        {
            final double probability = kernelDensity.p(currentValue);
            delta = probability - previousProbability;
            previousProbability = probability;
            if (delta > 0 && !minFound)
            {
                minFound = true;
                if (firstRun)
                {
                    firstRun = false;
                } else
                {
                    final ThreadArtifactCluster cluster = new ThreadArtifactCluster();
                    for (final double metricValue : metricValues)
                    {
                        if (metricValue > splitValue && metricValue <= (currentValue - step)) // We make the cut at the value before
                        {
                            final Collection<AThreadArtifact> aThreadArtifacts = metricMap.get(metricValue);
                            cluster.addAll(aThreadArtifacts);
                            aThreadArtifacts.clear();
                        }
                    }
                    threadArtifactClusters.add(cluster);
                    splitValue = currentValue;
                }
            } else if (delta < 0)
            {
                minFound = false;
            }
        }

        // Put all threads with a higher metric value in the last cluster
        final ThreadArtifactCluster cluster = new ThreadArtifactCluster();
        for (final double metricValue : metricValues)
        {
            if (metricValue > splitValue)
            {
                final Collection<AThreadArtifact> aThreadArtifacts = metricMap.get(metricValue);
                cluster.addAll(aThreadArtifacts);
                aThreadArtifacts.clear();
            }
        }
        if (!cluster.isEmpty())
        {
            threadArtifactClusters.add(cluster);
        }

        return threadArtifactClusters;
    }
}
