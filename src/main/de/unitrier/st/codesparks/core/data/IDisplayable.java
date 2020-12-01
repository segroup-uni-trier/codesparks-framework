package de.unitrier.st.codesparks.core.data;

/*
 * Copyright (c), Oliver Moseler, 2020
 */
public interface IDisplayable
{
    String getDisplayString(final IMetricIdentifier metricIdentifier, final int maxLen);

    String getDisplayString(final IMetricIdentifier metricIdentifier);
}
