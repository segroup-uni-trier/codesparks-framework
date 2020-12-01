package de.unitrier.st.codesparks.core.data;

import java.util.Comparator;

/*
 * Copyright (c), Oliver Moseler, 2020
 */
public class CodeSparksThreadComparator implements Comparator<ACodeSparksThread>
{
    private final IMetricIdentifier metricIdentifier;

    public CodeSparksThreadComparator(final IMetricIdentifier metricIdentifier)
    {
        this.metricIdentifier = metricIdentifier;
    }

    @Override
    public int compare(ACodeSparksThread o1, ACodeSparksThread o2)
    {
        return Double.compare(o1.getNumericalMetricValue(metricIdentifier), o2.getNumericalMetricValue(metricIdentifier)) * -1;
    }
}
