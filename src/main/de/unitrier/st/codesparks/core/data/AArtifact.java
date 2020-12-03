package de.unitrier.st.codesparks.core.data;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.*;

/*
 * Copyright (c), Oliver Moseler, 2020
 */
public abstract class AArtifact implements IDisplayable
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

    protected AArtifact(final String name, final String identifier)
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
        if (metricIdentifier == null)
        {
            return null;
        }
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

    public Object getMetricValue(final IMetricIdentifier metricIdentifier)
    {
        if (metricIdentifier == null)
        {
            return null;
        }
        Object value;
        synchronized (metricsLock)
        {
            value = metrics.get(metricIdentifier);
        }
        return value;
    }

    /**
     * A thread safe method to get or create a metric value in case it might not have been initialised yet. If the value is non null, no new value will be
     * instantiated.
     *
     * @param metricIdentifier The metric identifier.
     * @return The value (as object) associated with the metric identifier.
     */
    public final Object getOrCreateMetricValue(final IMetricIdentifier metricIdentifier, final Constructor<?> constructor, final Object... initArgs)
    {
        if (metricIdentifier == null)
        {
            return null;
        }
        Object metricValue = metrics.get(metricIdentifier);
        if (metricValue == null)
        {
            synchronized (metricsLock)
            { // Double checked locking!
                metricValue = metrics.get(metricIdentifier);
                if (metricValue == null)
                {
                    try
                    {
                        metricValue = constructor.newInstance(initArgs);
                        setMetricValue(metricIdentifier, metricValue);
                    } catch (InstantiationException | IllegalAccessException | InvocationTargetException e)
                    {
                        e.printStackTrace();
                    }
                }
            }
        }
        return metricValue;
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
        if (metricIdentifier == null || !metricIdentifier.isNumerical())
        {
            return;
        }
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

    public void decreaseNumericalMetricValue(final IMetricIdentifier metricIdentifier, final double toDecrease)
    {
        increaseNumericalMetricValue(metricIdentifier, (-1) * toDecrease);
    }

    public double getNumericalMetricValue(final IMetricIdentifier metricIdentifier)
    {
        if (metricIdentifier == null || !metricIdentifier.isNumerical())
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
        if (metricIdentifier == null || !metricIdentifier.isNumerical())
        {
            return;
        }
        synchronized (metricsLock)
        {
            metrics.put(metricIdentifier, value);
        }
    }
}
