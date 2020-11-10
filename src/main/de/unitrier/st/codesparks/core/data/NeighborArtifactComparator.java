package de.unitrier.st.codesparks.core.data;

import java.util.Comparator;

public class NeighborArtifactComparator implements Comparator<ANeighborArtifact>
{
    public int compare(ANeighborArtifact a, ANeighborArtifact b)
    {
        return Double.compare(b.getMetricValue(), a.getMetricValue());
    }
}
