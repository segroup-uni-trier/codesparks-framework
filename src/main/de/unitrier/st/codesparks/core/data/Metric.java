package de.unitrier.st.codesparks.core.data;

public abstract class Metric<T>
{
    String name;
    T value;

    public String getName()
    {
        return name;
    }

    public void setName(String name)
    {
        this.name = name;
    }

    public T getValue()
    {
        return value;
    }

    public void setValue(T value)
    {
        this.value = value;
    }

    @Override
    public String toString()
    {
        return value.toString();
    }
}
