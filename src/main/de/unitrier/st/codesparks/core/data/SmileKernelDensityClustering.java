/*
 * Copyright (c) 2021. Oliver Moseler
 */

package de.unitrier.st.codesparks.core.data;

import smile.stat.distribution.KernelDensity;

import java.util.*;

public class SmileKernelDensityClustering extends AThreadArtifactClusteringStrategy
{
    public SmileKernelDensityClustering(final AMetricIdentifier metricIdentifier)
    {
        super(metricIdentifier);
    }

    @Override
    public ThreadArtifactClustering clusterThreadArtifacts(final Collection<AThreadArtifact> threadArtifacts)
    {
        final ThreadArtifactClustering threadArtifactClusters = new ThreadArtifactClustering();

        final Map<Double, Collection<AThreadArtifact>> metricMap = new HashMap<>(threadArtifacts.size());
        final double[] metricValues = new double[threadArtifacts.size()];
        double maxMetricValue = Double.MIN_VALUE;
        double minMetricValue = Double.MAX_VALUE;
        final AMetricIdentifier metricIdentifier = getMetricIdentifier();
        int i = 0;
        for (final AThreadArtifact threadArtifact : threadArtifacts)
        {
            final double metricValue = threadArtifact.getNumericalMetricValue(metricIdentifier);
            maxMetricValue = Math.max(maxMetricValue, metricValue);
            minMetricValue = Math.min(minMetricValue, metricValue);
            metricValues[i++] = metricValue;
            Collection<AThreadArtifact> threads = metricMap.get(metricValue);
            if (threads == null)
            {
                threads = new ArrayList<>(4);
            }
            threads.add(threadArtifact);
            metricMap.put(metricValue, threads);
        }
        // TODO: epsilon dependent of number of threads and max value
        final double bandwidth = 0.01;

        final KernelDensity kernelDensity = new KernelDensity(metricValues, bandwidth);

        // TODO: a step size dependent of the number of threads or the concrete metric values?
//        final double step = 0.001;
        final double step = (maxMetricValue - minMetricValue) / (4 * threadArtifacts.size());

        double lastM = 0;
        boolean firstRun = true;
        boolean minFound = false;
        double previousProbability = 0d;
        double delta;
        for (double m = minMetricValue; m < maxMetricValue; m += step)
        {
            final double probability = kernelDensity.p(m);
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
                        if (metricValue > lastM && metricValue <= (m - step)) // We make the cut at the value before
                        {
                            final Collection<AThreadArtifact> aThreadArtifacts = metricMap.get(metricValue);
                            cluster.addAll(aThreadArtifacts);
                            aThreadArtifacts.clear();
                        }
                    }
                    threadArtifactClusters.add(cluster);
                    lastM = m;
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
            if (metricValue > lastM)
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
