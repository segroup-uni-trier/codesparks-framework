/*
 * Copyright (c) 2021. Oliver Moseler
 */
package de.unitrier.st.codesparks.core.data;

public interface IDisplayable
{
    String getDisplayString(final AMetricIdentifier metricIdentifier, final int maxLen);

    String getDisplayString(final AMetricIdentifier metricIdentifier);

    String getDisplayString();
}
