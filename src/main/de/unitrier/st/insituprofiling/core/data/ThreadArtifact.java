package de.unitrier.st.insituprofiling.core.data;

public abstract class ThreadArtifact implements IDisplayable
{
    private final String identifier;
    private double metricValue;
    private double metricValueSelf;
    private String callSite;
    private boolean filtered;

    public ThreadArtifact(String identifier)
    {
        this.identifier = identifier;
        filtered = false;
    }

    public ThreadArtifact(String identifier, double metricValue)
    {
        this.identifier = identifier;
        this.metricValue = metricValue;
        filtered = false;
    }

    public void increaseMetricValue(double toIncrease)
    {
        this.metricValue += toIncrease;
    }

    public void increaseMetricValueSelf(double toIncrease)
    {
        this.metricValueSelf += toIncrease;
    }

    public double getMetricValue()
    {
        return metricValue;
    }

    public void setMetricValue(double threadMetricValue)
    {
        this.metricValue = threadMetricValue;
    }

    public String getIdentifier()
    {
        return identifier;
    }

    public String getCallSite()
    {
        if (callSite == null)
        {
            int i = identifier.indexOf(':');
            if (i < 0)
            {
                return identifier;
            }
            return identifier.substring(0, i);
        }
        return callSite;
    }

    public void setCallSite(String callSite)
    {
        this.callSite = callSite;
    }

    public double getMetricValueSelf()
    {
        return metricValueSelf;
    }

    public void setMetricValueSelf(double metricValueSelf)
    {
        this.metricValueSelf = metricValueSelf;
    }

    public boolean isFiltered()
    {
        return filtered;
    }

    public void setFiltered(boolean filtered)
    {
        this.filtered = filtered;
    }
}
