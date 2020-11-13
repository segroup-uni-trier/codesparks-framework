package de.unitrier.st.codesparks.core.data;

public class Metric
{
    private String name;
    private Object value;

    public Metric(final String name, final Object value)
    {
        this.name = name;
        this.value = value;
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
}
