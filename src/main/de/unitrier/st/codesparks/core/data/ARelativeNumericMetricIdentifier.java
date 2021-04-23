/*
 * Copyright (c) 2021. Oliver Moseler
 */

package de.unitrier.st.codesparks.core.data;

public abstract class ARelativeNumericMetricIdentifier extends AMetricIdentifier
{
    @Override
    public boolean isNumerical()
    {
        return true;
    }

    @Override
    public boolean isRelative()
    {
        return true;
    }
}
