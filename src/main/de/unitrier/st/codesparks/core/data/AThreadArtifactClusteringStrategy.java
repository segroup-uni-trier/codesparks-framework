/*
 * Copyright (c) 2021. Oliver Moseler
 */

package de.unitrier.st.codesparks.core.data;

public abstract class AThreadArtifactClusteringStrategy implements IThreadArtifactClusteringStrategy
{
    private final AMetricIdentifier metricIdentifier;

    protected AThreadArtifactClusteringStrategy(final AMetricIdentifier metricIdentifier)
    {
        this.metricIdentifier = metricIdentifier;
    }

    public AMetricIdentifier getMetricIdentifier()
    {
        return metricIdentifier;
    }
}
