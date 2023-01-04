/*
 *  Copyright 2023 Oliver Moseler
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */
package de.unitrier.st.codesparks.core.data;

import java.util.*;

public class ThreadArtifactClustering extends ArrayList<ThreadArtifactCluster>
{
    private static long idCounter = 0;

    private static synchronized long getNextId()
    {
        return idCounter++;
    }

    private final long id;

    public final long getId()
    {
        return id;
    }

    private final AThreadArtifactClusteringStrategy strategy;

    public ThreadArtifactClustering(final AThreadArtifactClusteringStrategy strategy)
    {
        this.id = getNextId();
        this.strategy = strategy;
    }

    public AThreadArtifactClusteringStrategy getStrategy()
    {
        return strategy;
    }

    public Set<AThreadArtifact> getThreadArtifactsSet()
    {
        Set<AThreadArtifact> threadArtifacts = new HashSet<>();
        for (final ThreadArtifactCluster aThreadArtifacts : this)
        {
            threadArtifacts.addAll(aThreadArtifacts);
        }
        return threadArtifacts;
    }

    public int sizeAccordingToCurrentThreadSelection()
    {
        final long count = this.stream()
                .filter(cluster -> cluster.stream().anyMatch(AThreadArtifact::isSelected))
                .count();
        return (int) count;
    }

    public int sizeAccordingToCurrentThreadSelection(final IThreadSelectable threadSelectable)
    {
        final long count = this.stream()
                .filter(cluster -> !threadSelectable.getSelectedThreadArtifactsOfCluster(cluster).isEmpty())
                .count();
        return (int) count;
    }

    @Override
    public int size()
    {
        return (int) this.stream().filter(cluster -> !cluster.isEmpty()).count();
    }

    /**
     * The distance ...
     *
     */
    public double distToNearestCluster(final AThreadArtifact thr, final AMetricIdentifier metricIdentifier)
    {
        double min = Double.MAX_VALUE;
        for (ThreadArtifactCluster cluster : this)
        {
            if (!cluster.contains(thr))
            {
                final double dist = cluster.dist(thr, metricIdentifier);
                min = Math.min(min, dist);
            }
        }
        return min;
    }

    /**
     * @return A value of the fixed interval [-1,1]. A value close to 1 means that the data is appropriately clustered.
     */
    public double silhouette(final AThreadArtifact thr, final ThreadArtifactCluster clusterOfThr, final AMetricIdentifier metricIdentifier)
    {
        if (!clusterOfThr.contains(thr))
        {
            throw new IllegalArgumentException("The passed thread thr must be an element of the passed cluster!");
        }
        if (!this.contains(clusterOfThr))
        {
            throw new IllegalArgumentException("The passed cluster must be an element of this clustering!");
        }
        if (clusterOfThr.size() == 1)
        { // The thread thr is the only element in the cluster. We already know that the passed cluster contains thr.
            return 0D;
        }
        final double distA = clusterOfThr.dist(thr, metricIdentifier);
        final double distB = distToNearestCluster(thr, metricIdentifier);

        //noinspection UnnecessaryLocalVariable
        final double silhouette = (distB - distA) / Math.max(distA, distB);
        return silhouette;
    }

    /**
     * The mean silhouette over all points of a cluster is a measure of how tightly grouped all the points in the cluster are.
     *
     */
    public double meanSilhouetteCoefficientOfCluster(final ThreadArtifactCluster cluster, final AMetricIdentifier metricIdentifier)
    {
        if (!this.contains(cluster))
        {
            throw new IllegalArgumentException("The passed cluster must be an element of this clustering!");
        }
        double meanSilhouetteOfTheCluster = 0;
        for (final AThreadArtifact thr : cluster)
        {
            meanSilhouetteOfTheCluster += this.silhouette(thr, cluster, metricIdentifier);
        }
        meanSilhouetteOfTheCluster = meanSilhouetteOfTheCluster / cluster.size();
        return meanSilhouetteOfTheCluster;
    }

    /**
     * Thus the mean silhouette over all data of the entire dataset is a measure of how appropriately the data have been clustered. If
     * there are too many or too few clusters, as may occur when a poor choice of k is used in the clustering algorithm (e.g.: k-means),
     * some of the clusters will typically display much narrower silhouettes than the rest.
     *
     */
    public double silhouetteCoefficientAsMeanOfEachElementSilhouette(final AMetricIdentifier metricIdentifier)
    {
        double meanSilhouette = 0;
        int numberOfThreads = 0;
        for (final ThreadArtifactCluster cluster : this)
        {
            for (final AThreadArtifact thr : cluster)
            {
                numberOfThreads += 1;
                meanSilhouette += this.silhouette(thr, cluster, metricIdentifier);
            }
        }
        meanSilhouette = meanSilhouette / numberOfThreads;
        return meanSilhouette;
    }

    /**
     * This is how it is implemented in the R package.
     *
     */
    public double silhouetteCoefficientAsMeanOfEachClusterSilhouette(final AMetricIdentifier metricIdentifier)
    {
        double meanSilhouette = 0;
        for (final ThreadArtifactCluster cluster : this)
        {
            meanSilhouette += meanSilhouetteCoefficientOfCluster(cluster, metricIdentifier);
        }
        meanSilhouette = meanSilhouette / this.size();
        return meanSilhouette;
    }

    public String toString(final AMetricIdentifier metricIdentifier)
    {
        final StringBuilder strb = new StringBuilder();
//        int cl = 0;
        for (final ThreadArtifactCluster cluster : this)
        {
            final long id = cluster.getId();
//            strb.append("Cluster-").append(++cl).append("\n");
            strb.append("Cluster-").append(id).append("\n");
            for (final AThreadArtifact aThreadArtifact : cluster)
            {
                strb.append(aThreadArtifact.getIdentifier())
                        .append(" : ")
                        .append(aThreadArtifact.getNumericalMetricValue(metricIdentifier)).append("\n");
            }
        }

        return strb.toString();
    }
}
