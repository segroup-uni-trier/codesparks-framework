/*
 * Copyright (c) 2022. Oliver Moseler
 */

package de.unitrier.st.codesparks.core.visualization;

import java.awt.*;

public interface IMetricValueColorCodingStrategy
{
    Color getMetricValueColor(final Object metricValue);
}
