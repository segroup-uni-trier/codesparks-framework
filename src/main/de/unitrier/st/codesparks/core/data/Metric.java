package de.unitrier.st.codesparks.core.data;

public class Metric
{
    private final String identifier;
    String name;
    //    T value;
    Object value;

    protected Metric(final String identifier)
    {
        this.identifier = identifier;
    }

    public String getIdentifier()
    {
        return identifier;
    }

    public String getName()
    {
        return name;
    }

    public void setName(String name)
    {
        this.name = name;
    }

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
        return String.format("%s: %s\n", identifier, value.toString());
    }

    public String getMetricValueString()
    {
        return value.toString();
    }
}
