package de.unitrier.st.codesparks.core.data;

public interface IDisplayable
{
    String getDisplayString(final String metricIdentifier, final int maxLen);

    String getDisplayString(final String metricIdentifier);
}
