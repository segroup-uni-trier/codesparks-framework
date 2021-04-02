/*
 * Copyright (c) 2021. Oliver Moseler
 */

package de.unitrier.st.codesparks.core.overview;

import de.unitrier.st.codesparks.core.data.AArtifact;
import de.unitrier.st.codesparks.core.data.AMetricIdentifier;
import de.unitrier.st.codesparks.core.data.DataUtil;

import java.util.Comparator;
import java.util.function.ToDoubleFunction;

public class ArtifactMetricComparator implements Comparator<AArtifact>
{
//    private static final Map<AMetricIdentifier, ArtifactMetricComparator> instances = new HashMap<>(8);
//
//    public static ArtifactMetricComparator getInstance(final AMetricIdentifier metricIdentifier)
//    {
//        synchronized (instances)
//        {
//            ArtifactMetricComparator artifactMetricComparator = instances.get(metricIdentifier);
//            if (artifactMetricComparator == null)
//            {
//                artifactMetricComparator = new ArtifactMetricComparator(metricIdentifier);
//                instances.put(metricIdentifier, artifactMetricComparator);
//            }
//            return artifactMetricComparator;
//        }
//    }

    protected ToDoubleFunction<? super AArtifact> toDoubleFunction;
    private final AMetricIdentifier metricIdentifier;
    private boolean enabled;

    public ArtifactMetricComparator(final AMetricIdentifier metricIdentifier, final boolean enabled)
    {
        this.metricIdentifier = metricIdentifier;
        if (metricIdentifier.isNumerical() && metricIdentifier.isRelative())
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

    @Override
    public int compare(final AArtifact o1, final AArtifact o2)
    {
        return Comparator.comparingDouble(toDoubleFunction).reversed().compare(o1, o2);
    }

    @Override
    public String toString()
    {
        return metricIdentifier.toString();
    }
}
