/*
 * Copyright (c) 2021. Oliver Moseler
 */

package de.unitrier.st.codesparks.core.data;

import org.apache.commons.math3.ml.clustering.*;
import org.apache.commons.math3.ml.distance.EuclideanDistance;

import java.util.*;

public class ApacheKMeansPlusPlus extends KThreadArtifactClusteringStrategy
{
    private static final Map<AMetricIdentifier, Map<Integer, KThreadArtifactClusteringStrategy>> instances = new HashMap<>(4);

    public static KThreadArtifactClusteringStrategy getInstance(final AMetricIdentifier metricIdentifier, final int k)
    {
        KThreadArtifactClusteringStrategy ret;
        synchronized (instances)
        {
            final Map<Integer, KThreadArtifactClusteringStrategy> strategyMap = instances.computeIfAbsent(metricIdentifier, k1 -> new HashMap<>(4));
            KThreadArtifactClusteringStrategy strategy = strategyMap.get(k);
            if (strategy == null)
            {
                strategy = new ApacheKMeansPlusPlus(metricIdentifier, k);
                strategyMap.put(k, strategy);
            }
            ret = strategy;
        }
        return ret;
    }

    private ApacheKMeansPlusPlus(final AMetricIdentifier metricIdentifier, final int k)
    {
        super(metricIdentifier, k);
    }

    private static class ThreadPoint implements Clusterable
    {
        private final AThreadArtifact threadArtifact;
        private final double[] point;

        ThreadPoint(final AThreadArtifact threadArtifact, final AMetricIdentifier metricIdentifier)
        {
            this.threadArtifact = threadArtifact;
            point = new double[]{threadArtifact.getNumericalMetricValue(metricIdentifier)};
        }

        @Override
        public double[] getPoint()
        {
            return point;
        }
    }

    @Override
    public ThreadArtifactClustering clusterThreadArtifacts(final Collection<AThreadArtifact> threadArtifacts)
    {
        final List<ThreadPoint> threadPoints = new ArrayList<>(threadArtifacts.size());
        for (final AThreadArtifact threadArtifact : threadArtifacts)
        {
            threadPoints.add(new ThreadPoint(threadArtifact, this.getMetricIdentifier()));
        }
        final ThreadArtifactClustering threadArtifactClusters = new ThreadArtifactClustering(this);
        // In case there are too few threads to cluster for a given k, apply a trivial clustering. That is, assign every thread to an own cluster.
        if (threadPoints.size() <= k)
        {
            for (final ThreadPoint threadPoint : threadPoints)
            {
                final ThreadArtifactCluster cluster = new ThreadArtifactCluster();
                cluster.add(threadPoint.threadArtifact);
                threadArtifactClusters.add(cluster);
            }
            return threadArtifactClusters;
        }

        final EuclideanDistance euclideanDistance = new EuclideanDistance();
        final Clusterer<ThreadPoint> clusterer = new KMeansPlusPlusClusterer<>(k, 100, euclideanDistance);
        final List<? extends Cluster<ThreadPoint>> clustering = clusterer.cluster(threadPoints);

        for (final Cluster<ThreadPoint> threadPointCluster : clustering)
        {
            final ThreadArtifactCluster aThreadArtifacts = new ThreadArtifactCluster();
            for (final ThreadPoint point : threadPointCluster.getPoints())
            {
                aThreadArtifacts.add(point.threadArtifact);
            }
            threadArtifactClusters.add(aThreadArtifacts);
        }
        return threadArtifactClusters;
    }
}
