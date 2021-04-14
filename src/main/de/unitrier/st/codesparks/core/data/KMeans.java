/*
 * Copyright (c) 2021. Oliver Moseler
 */

package de.unitrier.st.codesparks.core.data;

//import org.deeplearning4j.clustering.algorithm.Distance;
//import org.deeplearning4j.clustering.cluster.Cluster;
//import org.deeplearning4j.clustering.cluster.ClusterSet;
//import org.deeplearning4j.clustering.cluster.Point;
//import org.deeplearning4j.clustering.kmeans.KMeansClustering;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class KMeans extends AThreadArtifactClusteringStrategy
{
    public KMeans(final AMetricIdentifier metricIdentifier)
    {
        super(metricIdentifier);
    }

//    private static class ThreadPoint extends Point
//    {
//        private final AThreadArtifact threadArtifact;
//
//        ThreadPoint(final AThreadArtifact threadArtifact, final AMetricIdentifier metricIdentifier)
//        {
//            super(threadArtifact.getIdentifier(), threadArtifact.getName(), new double[]{threadArtifact.getNumericalMetricValue(metricIdentifier)});
//            this.threadArtifact = threadArtifact;
//        }
//
//        AThreadArtifact getThreadArtifact()
//        {
//            return threadArtifact;
//        }
//    }

    @Override
    public ThreadArtifactClustering clusterThreadArtifacts(final Collection<AThreadArtifact> threadArtifacts)
    {
//        List<Point> threadPoints = new ArrayList<>();
//        for (final AThreadArtifact threadArtifact : threadArtifacts)
//        {
//            threadPoints.add(new ThreadPoint(threadArtifact, this.getMetricIdentifier()));
//        }
//
//        final KMeansClustering kMeansClustering = KMeansClustering.setup(3, 100, Distance.EUCLIDEAN, false, false);
//
//        final ClusterSet clusterSet = kMeansClustering.applyTo(threadPoints);
//
//        final List<Cluster> clusters = clusterSet.getClusters();


        return null;
    }
}
