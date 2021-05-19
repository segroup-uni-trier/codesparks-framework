/*
 * Copyright (c) 2021. Oliver Moseler
 */

package de.unitrier.st.codesparks.core.data;

import java.util.Collection;

public class SumMetricValueProvider extends AMetricValueProvider
{
    public SumMetricValueProvider(final Collection<AMetricIdentifier> metricIdentifiers)
    {
        super(metricIdentifiers);
    }

    @Override
    public double metricValueFor(final AArtifact artifact)
    {
        if (artifact == null)
        {
            return 0;
        }
        double sum = 0;
        for (final AMetricIdentifier metricIdentifier : metricIdentifiers)
        {
            final double numericalMetricValue = artifact.getNumericalMetricValue(metricIdentifier);
            sum += numericalMetricValue;
        }
        return sum;
    }
}
