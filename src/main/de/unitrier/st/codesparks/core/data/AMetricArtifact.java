package de.unitrier.st.codesparks.core.data;

import java.util.*;

public abstract class AMetricArtifact
{
    protected final String name;

    public String getName()
    {
        return name;
    }

    protected final String identifier;

    public String getIdentifier()
    {
        return identifier;
    }

    protected AMetricArtifact(final String name, final String identifier)
    {
        this.name = name == null ? "" : name;
        this.identifier = identifier == null ? "" : identifier;
        metrics = new HashMap<>();
    }

    private final Map<IMetricIdentifier, Object> metrics;

    private final Object metricsLock = new Object();

    public Collection<Metric> getMetrics()
    {
        Set<Map.Entry<IMetricIdentifier, Object>> entries;
        synchronized (metricsLock)
        {
            entries = metrics.entrySet();
        }
        Collection<Metric> ret = new ArrayList<>(entries.size());

        for (final Map.Entry<IMetricIdentifier, Object> entry : entries)
        {
            final String name = entry.getKey().getDisplayString();
            final Object value = entry.getValue();
            final Metric metric = new Metric(name, value);
            ret.add(metric);
        }

        return ret;
    }

    public Metric getMetric(final IMetricIdentifier metricIdentifier)
    {
        final String name = metricIdentifier.getDisplayString();
        Metric m = new Metric(name);
        Object value;
        synchronized (metricsLock)
        {
            value = metrics.get(metricIdentifier);
        }
        m.setValue(value);
        return m;
    }

    public Object getMetricValue(IMetricIdentifier metricIdentifier)
    {
        Object value;
        synchronized (metricsLock)
        {
            value = metrics.get(metricIdentifier);
        }
        return value;
    }

    public void setMetricValue(final IMetricIdentifier metricIdentifier, final Object value)
    {
        if (metricIdentifier == null || value == null)
        {
            return;
        }
        synchronized (metricsLock)
        {
            metrics.put(metricIdentifier, value);
        }
    }

    public void increaseNumericalMetricValue(final IMetricIdentifier metricIdentifier, final double toIncrease)
    {
        if (metricIdentifier.isNumerical())
        {
            synchronized (metricsLock)
            {
                Double val = (Double) metrics.get(metricIdentifier);
                if (val == null)
                {
                    val = 0d;
                }
                val += toIncrease;
                metrics.put(metricIdentifier, val);
            }
        }
    }

    public void decreaseNumericalMetricValue(final IMetricIdentifier metricIdentifier, final double toDecrease)
    {
        increaseNumericalMetricValue(metricIdentifier, (-1) * toDecrease);
    }

    public double getNumericalMetricValue(final IMetricIdentifier metricIdentifier)
    {
        if (!metricIdentifier.isNumerical())
        {
            return Double.NaN;
        }
        Double val;
        synchronized (metricsLock)
        {
            val = (Double) metrics.get(metricIdentifier);
        }
        if (val == null)
        {
            return 0D;
        }
        return val;
    }

    public void setNumericalMetricValue(final IMetricIdentifier metricIdentifier, final double value)
    {
        if (metricIdentifier.isNumerical())
        {
            synchronized (metricsLock)
            {
                metrics.put(metricIdentifier, value);
            }
        }
    }
}
