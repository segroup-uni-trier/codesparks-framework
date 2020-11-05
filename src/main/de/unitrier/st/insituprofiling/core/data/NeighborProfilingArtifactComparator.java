package de.unitrier.st.insituprofiling.core.data;

import java.util.Comparator;

public class NeighborProfilingArtifactComparator implements Comparator<ANeighborProfilingArtifact>
{
    public int compare(ANeighborProfilingArtifact a, ANeighborProfilingArtifact b)
    {
        return Double.compare(b.getMetricValue(), a.getMetricValue());
    }
}
