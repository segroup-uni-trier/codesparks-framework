/*
 * Copyright (c) 2021. Oliver Moseler
 */
package de.unitrier.st.codesparks.core.data;

import java.util.Comparator;

public class NeighborArtifactComparator implements Comparator<ANeighborArtifact>
{
    private final AMetricIdentifier metricIdentifier;

    public NeighborArtifactComparator(final AMetricIdentifier metricIdentifier)
    {
        this.metricIdentifier = metricIdentifier;
    }

    public int compare(ANeighborArtifact a, ANeighborArtifact b)
    {
        return Double.compare(b.getNumericalMetricValue(metricIdentifier), a.getNumericalMetricValue(metricIdentifier));
    }
}
