package de.unitrier.st.codesparks.core.data;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

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

    Map<String, Metric> metrics; // TODO: OM!

    private final Object metricsLock = new Object();

    public Collection<Metric> getMetrics()
    {
        synchronized (metricsLock)
        {
            return metrics.values();
        }
    }

    public Metric getMetric(final String metricIdentifier)
    {
        synchronized (metricsLock)
        {
            return metrics.get(metricIdentifier);
        }
    }

    public Object getMetricValue(final String identifier)
    {
        synchronized (metricsLock)
        {
            final Metric metric = metrics.get(identifier);
            if (metric == null)
            {
                return null;
            }
            return metric.getValue();
        }
    }

    public void setMetricValue(final String identifier, final Object value)
    {
        synchronized (metricsLock)
        {
            Metric metric = metrics.get(identifier);
            if (metric == null)
            {
                metric = new Metric(identifier);
                metrics.put(identifier, metric);
            }
            metric.setValue(value);
        }
    }

    public void increaseNumericalMetricValue(final String identifier, final double toIncrease)
    {
        synchronized (metricsLock)
        {
            NumericalMetric metric = (NumericalMetric) metrics.get(identifier);
            if (metric == null)
            {
                metric = new NumericalMetric(identifier);
                metrics.put(identifier, metric);
            }
            metric.increaseNumericalValue(toIncrease);
        }
    }

    public void decreaseNumericalMetricValue(final String metricIdentifier, double toDecrease)
    {
        synchronized (metricsLock)
        {
            NumericalMetric metric = (NumericalMetric) metrics.get(identifier);
            if (metric == null)
            {
                metric = new NumericalMetric(identifier);
                metrics.put(identifier, metric);
            }
            metric.decreaseNumericalMetricValue(toDecrease);
        }
    }

    public double getNumericalMetricValue(final String identifier)
    {
        synchronized (metricsLock)
        {
            final NumericalMetric metric = (NumericalMetric) metrics.get(identifier);
            if (metric == null)
            {
                return Double.NaN;
            }
            return metric.getNumericValue();
        }
    }

    public void setNumericalMetricValue(final String identifier, final double value)
    {
        synchronized (metricsLock)
        {
            NumericalMetric metric = (NumericalMetric) metrics.get(identifier);
            if (metric == null)
            {
                metric = new NumericalMetric(identifier);
                metrics.put(identifier, metric);
            }
            metric.setValue(value);
        }
    }

    public String getMetricValueText(final String identifier)
    {
        synchronized (metricsLock)
        {
            final Metric metric = metrics.get(identifier);
            if (metric == null)
            {
                return "n/a";
            }
            return metric.toString();
        }
    }
}
