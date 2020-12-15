package de.unitrier.st.codesparks.core.data;

/*
 * Copyright (c), Oliver Moseler, 2020
 */
public interface IMetricIdentifier
{
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
