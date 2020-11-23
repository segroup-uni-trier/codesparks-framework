package de.unitrier.st.codesparks.core.data;

/**
 * Attention! Not in use yet. In future, it will replace the metricIdentifier which is solely represented as String currently.
 */
public abstract class AMetricIdentifier implements IMetricIdentifier
{
    private final String metricIdentifier;

    protected AMetricIdentifier(final String metricIdentifier)
    {
        assert metricIdentifier != null;
        this.metricIdentifier = metricIdentifier;
    }

    @Override
    public String toString()
    {
        return metricIdentifier;
    }
}
