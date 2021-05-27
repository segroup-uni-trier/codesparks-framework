/*
 * Copyright (c) 2021. Oliver Moseler
 */

package de.unitrier.st.codesparks.core.data;

import java.util.Objects;

public abstract class KThreadArtifactClusteringStrategy extends AThreadArtifactClusteringStrategy
{
    protected int k;

    protected KThreadArtifactClusteringStrategy(final AMetricIdentifier metricIdentifier)
    {
        super(metricIdentifier);
    }

    protected KThreadArtifactClusteringStrategy(final AMetricIdentifier metricIdentifier, final int k)
    {
        super(metricIdentifier);
        this.k = k;
    }

    protected int getK()
    {
        return k;
    }

    protected void setK(final int k)
    {
        this.k = k;
    }

    @Override
    public boolean equals(final Object o)
    {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;
        final KThreadArtifactClusteringStrategy that = (KThreadArtifactClusteringStrategy) o;
        return k == that.k;
    }

    @Override
    public int hashCode()
    {
        return Objects.hash(super.hashCode(), k);
    }
}
