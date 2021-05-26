/*
 * Copyright (c), Oliver Moseler, 2021
 */
package de.unitrier.st.codesparks.core.data;

import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;

public class ThreadArtifactComparator implements Comparator<AThreadArtifact>
{
    private static final Map<AMetricIdentifier, Comparator<AThreadArtifact>> instances = new HashMap<>(2);

    public static Comparator<AThreadArtifact> getInstance(final AMetricIdentifier metricIdentifier)
    {
        synchronized (instances)
        {
            Comparator<AThreadArtifact> comparator = instances.get(metricIdentifier);
            if (comparator == null)
            {
                comparator = new ThreadArtifactComparator(metricIdentifier);
                instances.put(metricIdentifier, comparator);
            }
            return comparator;
        }
    }

    private final AMetricIdentifier metricIdentifier;

    private ThreadArtifactComparator(final AMetricIdentifier metricIdentifier)
    {
        this.metricIdentifier = metricIdentifier;
    }

    @Override
    public int compare(AThreadArtifact o1, AThreadArtifact o2)
    {
        return Double.compare(o1.getNumericalMetricValue(metricIdentifier), o2.getNumericalMetricValue(metricIdentifier)) * -1;
    }
}
