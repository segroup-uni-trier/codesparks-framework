/*
 * Copyright (c) 2021. Oliver Moseler
 */

package de.unitrier.st.codesparks.core.data;

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
}
