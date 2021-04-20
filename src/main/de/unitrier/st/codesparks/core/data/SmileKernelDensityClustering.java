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
        int i = 0;
        for (final AThreadArtifact threadArtifact : threadArtifacts)
        {
            final double metricValue = threadArtifact.getNumericalMetricValue(getMetricIdentifier());
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
        final double bandwidth = 0.01; // TODO: epsilon dependent of number of threads and max value

        final KernelDensity kernelDensity = new KernelDensity(metricValues, bandwidth);

        final double step = (maxMetricValue - minMetricValue) / threadArtifacts.size();

        double lastM = 0;
        boolean firstRun = true;
        boolean minFound = false;
        double prevP = 0d;
        double deltaP;
        for (double m = minMetricValue; m < maxMetricValue; m += 0.01)
        {
            final double p = kernelDensity.p(m);
            deltaP = p - prevP;
            prevP = p;
            if (deltaP > 0 && !minFound)
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
                        if (metricValue > lastM && metricValue <= m)
                        {
                            final Collection<AThreadArtifact> aThreadArtifacts = metricMap.get(metricValue);
                            cluster.addAll(aThreadArtifacts);
                            aThreadArtifacts.clear();
                        }
                    }
                    threadArtifactClusters.add(cluster);
                    lastM = m;
                }
            } else if (deltaP < 0)
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
