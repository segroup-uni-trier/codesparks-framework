/*
 * Copyright (c) 2021. Oliver Moseler
 */

package de.unitrier.st.codesparks.core.overview;

import de.unitrier.st.codesparks.core.data.AArtifact;
import de.unitrier.st.codesparks.core.data.AMetricIdentifier;
import de.unitrier.st.codesparks.core.data.DataUtil;

import java.util.Comparator;
import java.util.Objects;
import java.util.function.ToDoubleFunction;

public class ArtifactMetricComparator implements Comparator<AArtifact>
{
    protected ToDoubleFunction<? super AArtifact> toDoubleFunction;
    private final AMetricIdentifier metricIdentifier;
    private boolean enabled;

    public ArtifactMetricComparator(final AMetricIdentifier metricIdentifier, final boolean enabled)
    {
        if (metricIdentifier == null)
        {
            throw new IllegalArgumentException("The argument 'metricIdentifier' must not be null!");
        }
        this.metricIdentifier = metricIdentifier;
        if (metricIdentifier.isNumerical() )
        {
            if (metricIdentifier.isRelative())
            {
                this.toDoubleFunction = artifact -> {
                    if (artifact != null)
                    {
                        return DataUtil.getThreadFilteredRelativeNumericMetricValueOf(artifact, metricIdentifier);
                    } else
                    {
                        return 0d;
                    }
                };
            } else {
                this.toDoubleFunction = artifact ->
                {
                    if (artifact != null)
                    {
                        return artifact.getNumericalMetricValue(metricIdentifier);
                    } else
                    {
                        return 0d;
                    }
                };
            }
        }
        this.enabled = enabled;
    }

    public ArtifactMetricComparator(final AMetricIdentifier metricIdentifier)
    {
        this(metricIdentifier, false);
    }

    boolean isEnabled()
    {
        return enabled;
    }

    void setEnabled(final boolean enabled)
    {
        this.enabled = enabled;
    }

    AMetricIdentifier getMetricIdentifier()
    {
        return metricIdentifier;
    }

    @Override
    public int compare(final AArtifact o1, final AArtifact o2)
    {
        return Comparator.comparingDouble(toDoubleFunction).reversed().compare(o1, o2);
    }

    @Override
    public boolean equals(final Object o)
    {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        final ArtifactMetricComparator that = (ArtifactMetricComparator) o;
        return enabled == that.enabled && Objects.equals(toDoubleFunction, that.toDoubleFunction) && Objects.equals(metricIdentifier, that.metricIdentifier);
    }

    @Override
    public int hashCode()
    {
        return Objects.hash(toDoubleFunction, metricIdentifier, enabled);
    }

    @Override
    public String toString()
    {
        return metricIdentifier.toString();
    }
}
