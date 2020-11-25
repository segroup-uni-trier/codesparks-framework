package de.unitrier.st.codesparks.core.data;

public class Metric
{
    private final String name;
    Object value;

    protected Metric(final String name)
    {
        this.name = name;
    }

    protected Metric(final String name, final Object value)
    {
        this.name = name;
        this.value = value;
    }

    public Object getValue()
    {
        return value;
    }

    public String getName()
    {
        return name;
    }

    public void setValue(Object value)
    {
        this.value = value;
    }

    @Override
    public String toString()
    {
        return name + ": " + value.toString();
    }
}
