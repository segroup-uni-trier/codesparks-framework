package de.unitrier.st.codesparks.core.data;

import java.util.Comparator;

public class CodeSparksThreadComparator implements Comparator<ACodeSparksThread>
{
    private final String metricIdentifier;

    public CodeSparksThreadComparator(final String metricIdentifier)
    {
        this.metricIdentifier = metricIdentifier;
    }

    @Override
    public int compare(ACodeSparksThread o1, ACodeSparksThread o2)
    {
        return Double.compare(o1.getNumericalMetricValue(metricIdentifier), o2.getNumericalMetricValue(metricIdentifier)) * -1;
    }
}
