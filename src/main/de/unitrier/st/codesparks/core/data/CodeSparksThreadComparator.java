package de.unitrier.st.codesparks.core.data;

import java.util.Comparator;

/*
 * Copyright (c), Oliver Moseler, 2020
 */
public class CodeSparksThreadComparator implements Comparator<AThreadArtifact>
{
    private final IMetricIdentifier metricIdentifier;

    public CodeSparksThreadComparator(final IMetricIdentifier metricIdentifier)
    {
        this.metricIdentifier = metricIdentifier;
    }

    @Override
    public int compare(AThreadArtifact o1, AThreadArtifact o2)
    {
        return Double.compare(o1.getNumericalMetricValue(metricIdentifier), o2.getNumericalMetricValue(metricIdentifier)) * -1;
    }
}
