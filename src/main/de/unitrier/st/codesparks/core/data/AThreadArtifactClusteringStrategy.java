/*
 * Copyright (c) 2021. Oliver Moseler
 */

package de.unitrier.st.codesparks.core.data;

import java.util.Objects;

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

    @Override
    public boolean equals(final Object o)
    {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        final AThreadArtifactClusteringStrategy that = (AThreadArtifactClusteringStrategy) o;
        return Objects.equals(metricIdentifier, that.metricIdentifier);
    }

    @Override
    public int hashCode()
    {
        return Objects.hash(metricIdentifier);
    }
}
