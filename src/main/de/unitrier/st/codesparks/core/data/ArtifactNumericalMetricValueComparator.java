package de.unitrier.st.codesparks.core.data;

import java.util.Comparator;

/*
 * Copyright (c), Oliver Moseler, 2020
 */
public class ArtifactNumericalMetricValueComparator implements Comparator<AArtifact>
{
    private final IMetricIdentifier metricIdentifier;

    public ArtifactNumericalMetricValueComparator(final IMetricIdentifier metricIdentifier)
    {
        this.metricIdentifier = metricIdentifier;
    }

    public int compare(AArtifact a, AArtifact b)
    {
        double numericalMetricValue = a.getNumericalMetricValue(metricIdentifier);

        double numericalMetricValue1 = b.getNumericalMetricValue(metricIdentifier);

        return Double.compare(numericalMetricValue1, numericalMetricValue);
    }
}
