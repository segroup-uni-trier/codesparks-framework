package de.unitrier.st.codesparks.core.data;

import java.util.Comparator;

/*
 * Copyright (c), Oliver Moseler, 2020
 */
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
