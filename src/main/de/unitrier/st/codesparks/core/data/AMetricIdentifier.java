/*
 * Copyright (c) 2021. Oliver Moseler
 */

package de.unitrier.st.codesparks.core.data;

public abstract class AMetricIdentifier implements IMetricIdentifier
{
    @Override
    public int hashCode()
    {
        return getIdentifier().hashCode();
    }

    @Override
    public boolean equals(final Object obj)
    {
        if (obj == null) return false;
        if (!(obj instanceof IMetricIdentifier)) return false;
        return this.getIdentifier().equals(((IMetricIdentifier) obj).getIdentifier());
    }
}
