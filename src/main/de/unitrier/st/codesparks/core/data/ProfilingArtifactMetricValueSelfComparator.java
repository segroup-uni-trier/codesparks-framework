package de.unitrier.st.codesparks.core.data;

import java.util.Comparator;

public class ProfilingArtifactMetricValueSelfComparator implements Comparator<AProfilingArtifact>
{
    public int compare(AProfilingArtifact a, AProfilingArtifact b)
    {
        return Double.compare(b.getMetricValueSelf(), a.getMetricValueSelf());
    }
}
