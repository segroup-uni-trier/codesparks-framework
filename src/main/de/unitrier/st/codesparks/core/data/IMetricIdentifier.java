/*
 * Copyright (c), Oliver Moseler, 2021
 */
package de.unitrier.st.codesparks.core.data;

import java.io.Serializable;

interface IMetricIdentifier extends Serializable
{
    String getIdentifier();

    String getName();

    String getDisplayString();

    /**
     * Determines if the metric is designed to represent a numerical value, i.e. its data type is a double.
     *
     * @return true if the metric represents a numeric value, false otherwise.
     */
    boolean isNumerical();

    /**
     * Determines if the metric is designed to represent a relative numerical value, i.e. its value is of the closed interval [0,1].
     *
     * @return true if the metric represents a relative numerical value, false otherwise.
     */
    boolean isRelative();
}
