/*
 * Copyright (c) 2021. Oliver Moseler
 */

package de.unitrier.st.codesparks.core.data;

import org.apache.commons.math3.ml.clustering.*;
import org.apache.commons.math3.ml.distance.EuclideanDistance;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class ApacheKMeans extends KThreadArtifactClusteringStrategy
{
    public ApacheKMeans(final AMetricIdentifier metricIdentifier, final int k)
    {
        super(metricIdentifier, k);
    }

    public ApacheKMeans(final AMetricIdentifier metricIdentifier)
    {
        super(metricIdentifier, 3);
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
