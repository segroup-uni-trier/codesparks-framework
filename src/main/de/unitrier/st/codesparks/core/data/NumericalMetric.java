package de.unitrier.st.codesparks.core.data;

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

    public void decreaseNumericalMetricValue(double toDecrease)
    {
        this.value = (Double) value - toDecrease;
    }
}
