/*
 * Copyright (c) 2021. Oliver Moseler
 */

package de.unitrier.st.codesparks.core.data;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

public class BestSilhouetteKClustering implements IThreadArtifactClusteringStrategy
{
    private final int k;
    private final KThreadArtifactClusteringStrategy strategy;

    public BestSilhouetteKClustering(final KThreadArtifactClusteringStrategy strategy, final int k)
    {
        this.strategy = strategy;
        this.k = k;
    }

    @Override
    public ThreadArtifactClustering clusterThreadArtifacts(final Collection<AThreadArtifact> threadArtifacts)
    {
        final Map<Double, ThreadArtifactClustering> clusterings = new HashMap<>(k - 1);
        double maxSilhouette = Double.MIN_VALUE;
        for (int i = 2; i <= k; i++)
        {
            strategy.setK(i);
            final ThreadArtifactClustering threadClustering = strategy.clusterThreadArtifacts(threadArtifacts);
            final double silhouette = threadClustering.silhouetteCoefficientAsMeanOfEachClusterSilhouette(strategy.getMetricIdentifier());
            clusterings.put(silhouette, threadClustering);
            maxSilhouette = Math.max(maxSilhouette, silhouette);
        }
        final ThreadArtifactClustering threadArtifactClusters = clusterings.get(maxSilhouette);
        clusterings.clear();
        return threadArtifactClusters;
    }
}
