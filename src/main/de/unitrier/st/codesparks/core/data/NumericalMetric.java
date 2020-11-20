package de.unitrier.st.codesparks.core.data;

public class NumericalMetric extends Metric<Double>
{
    public NumericalMetric()
    {
        this.value = 0D;
    }

    void increaseNumericalValue(final double toIncrease)
    {
        this.value += toIncrease;
    }

}
