/*
 * Copyright (c) 2021. Oliver Moseler
 */

package de.unitrier.st.codesparks.core.data;

import java.util.Collection;

public abstract class AMetricValueProvider
{
    protected final Collection<AMetricIdentifier> metricIdentifiers;

    protected AMetricValueProvider(final Collection<AMetricIdentifier> metricIdentifiers)
    {
        this.metricIdentifiers = metricIdentifiers;
    }

    public abstract double metricValueFor(AArtifact artifact);
}
