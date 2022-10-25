/*
 * Copyright (c) 2021. Oliver Moseler
 */

package de.unitrier.st.codesparks.core.data;

import de.unitrier.st.codesparks.core.CoreUtil;

public abstract class ARelativeNumericMetricIdentifier extends AMetricIdentifier
{
    @Override
    public final boolean isRelative()
    {
        return true;
    }

    @Override
    public Class<Double> getMetricValueType()
    {
        return Double.class;
    }

    @Override
    public String getValueDisplayString(final Object metricValue)
    {
        if (metricValue != null)
        {
            //final Class<Double> metricValueType = getMetricValueType();
            return CoreUtil.formatPercentage((Double) metricValue);
        }
        return "N/A";
    }
}
