package de.unitrier.st.codesparks.core.data;

import java.util.Comparator;

public class ArtifactMetricValueSelfComparator implements Comparator<AArtifact>
{
    public int compare(AArtifact a, AArtifact b)
    {
        return Double.compare(b.getMetricValueSelf(), a.getMetricValueSelf());
    }
}
