package de.unitrier.st.codesparks.core.data;

import java.util.Comparator;

public class NeighborArtifactComparator implements Comparator<ANeighborArtifact>
{
    private final IMetricIdentifier metricIdentifier;

    public NeighborArtifactComparator(final IMetricIdentifier metricIdentifier)
    {
        this.metricIdentifier = metricIdentifier;
    }

    public int compare(ANeighborArtifact a, ANeighborArtifact b)
    {
        return Double.compare(b.getNumericalMetricValue(metricIdentifier), a.getNumericalMetricValue(metricIdentifier));
    }
}
