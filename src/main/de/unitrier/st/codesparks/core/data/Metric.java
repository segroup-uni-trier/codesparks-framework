package de.unitrier.st.codesparks.core.data;

public class Metric
{
    private final String identifier;
    private IMetricIdentifier metricIdentifier;
    //    T value;
    Object value;

    protected Metric(final String identifier)
    {
        this.identifier = identifier;
    }

//    protected Metric(final IMetricIdentifier metricIdentifier)
//    {
//        this.metricIdentifier = metricIdentifier;
//    }

//    public String getIdentifier()
//    {
//        return identifier;
//    }

    public Object getValue()
    {
        return value;
    }

    public void setValue(Object value)
    {
        this.value = value;
    }

    @Override
    public String toString()
    {
        return identifier + ": " + value.toString();
    }

//    @Override
//    public String toString()
//    {
//        return metricIdentifier.toString() + ": " + value.toString();
//    }
}
