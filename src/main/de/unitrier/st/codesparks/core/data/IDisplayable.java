package de.unitrier.st.codesparks.core.data;

/*
 * Copyright (c), Oliver Moseler, 2020
 */
public interface IDisplayable
{
    String getDisplayString(final AMetricIdentifier metricIdentifier, final int maxLen);

    String getDisplayString(final AMetricIdentifier metricIdentifier);
}
