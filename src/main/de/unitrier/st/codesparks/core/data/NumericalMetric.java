package de.unitrier.st.codesparks.core.data;

import de.unitrier.st.codesparks.core.CoreUtil;

public class NumericalMetric extends Metric
{
    public NumericalMetric(final String identifier)
    {
        super(identifier);
        this.value = 0D;
    }

    void increaseNumericalValue(final double toIncrease)
    {
        this.value = (Double) value + toIncrease;
    }

    double getNumericValue()
    {
        return (Double) value;
    }

    @Override
    public String getMetricValueString()
    {
        return String.format("%s => METRIC-VALUE: %s", name, CoreUtil.formatPercentage((Double) value));
    }

    public void decreaseNumericalMetricValue(double toDecrease)
    {
        this.value = (Double) value - toDecrease;
    }
}
