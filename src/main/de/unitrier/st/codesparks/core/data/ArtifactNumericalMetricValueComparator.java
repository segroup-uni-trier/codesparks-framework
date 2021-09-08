/*
 * Copyright (c) 2021. Oliver Moseler
 */
package de.unitrier.st.codesparks.core.data;

import java.util.Comparator;

public class ArtifactNumericalMetricValueComparator implements Comparator<AArtifact>
{
    private final AMetricIdentifier metricIdentifier;

    public ArtifactNumericalMetricValueComparator(final AMetricIdentifier metricIdentifier)
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
