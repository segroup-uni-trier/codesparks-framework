package de.unitrier.st.codesparks.core.data;

public interface IDisplayable
{
    String getDisplayString(final IMetricIdentifier metricIdentifier, final int maxLen);

    String getDisplayString(final IMetricIdentifier metricIdentifier);
}
