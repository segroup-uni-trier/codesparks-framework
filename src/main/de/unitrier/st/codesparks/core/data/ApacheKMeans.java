/*
 * Copyright (c) 2021. Oliver Moseler
 */

package de.unitrier.st.codesparks.core.data;

import org.apache.commons.math3.ml.clustering.*;
import org.apache.commons.math3.ml.distance.EuclideanDistance;

import java.util.*;

public class ApacheKMeans extends KThreadArtifactClusteringStrategy
{
    private static final Map<AMetricIdentifier, Map<Integer, KThreadArtifactClusteringStrategy>> instances = new HashMap<>(4);

    public static KThreadArtifactClusteringStrategy getInstance(final AMetricIdentifier metricIdentifier, final int k)
    {
        KThreadArtifactClusteringStrategy ret;
        synchronized (instances)
        {
            Map<Integer, KThreadArtifactClusteringStrategy> strategyMap = instances.computeIfAbsent(metricIdentifier, k1 -> new HashMap<>(4));
            KThreadArtifactClusteringStrategy strategy = strategyMap.get(k);
            if (strategy == null)
            {
                strategy = new ApacheKMeans(metricIdentifier, k);
                strategyMap.put(k, strategy);
            }
            ret = strategy;
        }
        return ret;
    }

    private ApacheKMeans(final AMetricIdentifier metricIdentifier, final int k)
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

        AThreadArtifact getThreadArtifact()
        {
            return threadArtifact;
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

        final EuclideanDistance euclideanDistance = new EuclideanDistance();
        Clusterer<ThreadPoint> clusterer;

//        clusterer = new FuzzyKMeansClusterer<>(k, 2d);
        clusterer = new KMeansPlusPlusClusterer<>(k, 100, euclideanDistance);

        final List<? extends Cluster<ThreadPoint>> clustering = clusterer.cluster(threadPoints);

//        int cl = 0;
//        for (final Cluster<ThreadPoint> threadPointCluster : clustering)
//        {
//            System.out.println("Cluster: " + ++cl + " size=" + threadPointCluster.getPoints().size());
//            for (final ThreadPoint point : threadPointCluster.getPoints())
//            {
//                System.out.println(point.getThreadArtifact().identifier + " " + point.getPoint()[0]);
//            }
//        }

        final ThreadArtifactClustering threadArtifactClusters = new ThreadArtifactClustering();

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

    @Override
    public int hashCode()
    {
        return super.hashCode();
    }

    @Override
    public boolean equals(final Object obj)
    {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        return this.getMetricIdentifier().equals(((AThreadArtifactClusteringStrategy) obj).getMetricIdentifier());
        //return super.equals(obj);
    }
}
