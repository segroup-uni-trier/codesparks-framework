package de.unitrier.st.codesparks.core.data;

import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;
import java.util.function.ToDoubleFunction;

/*
 * Copyright (c), Oliver Moseler, 2020
 */
public class ThreadArtifactClusterNumericalMetricSumComparator implements Comparator<ThreadArtifactCluster>
{
    private final static Map<AMetricIdentifier, Comparator<ThreadArtifactCluster>> comparators = new HashMap<>();

    public static Comparator<ThreadArtifactCluster> getInstance(final AMetricIdentifier metricIdentifier)
    {
        synchronized (ThreadArtifactClusterNumericalMetricSumComparator.class)
        {
            Comparator<ThreadArtifactCluster> codeSparksThreadClusterComparator = comparators.get(metricIdentifier);
            if (codeSparksThreadClusterComparator == null)
            {
                codeSparksThreadClusterComparator = new ThreadArtifactClusterNumericalMetricSumComparator(metricIdentifier);
                comparators.put(metricIdentifier, codeSparksThreadClusterComparator);
            }
            return codeSparksThreadClusterComparator;
        }
    }

    private final AMetricIdentifier metricIdentifier;

    private ThreadArtifactClusterNumericalMetricSumComparator(final AMetricIdentifier metricIdentifier)
    {
        this.metricIdentifier = metricIdentifier;
    }

    @Override
    public int compare(ThreadArtifactCluster o1, ThreadArtifactCluster o2)
    {
        final ToDoubleFunction<AThreadArtifact> f = thread -> thread.getNumericalMetricValue(metricIdentifier);
        double sum1 = o1.stream().mapToDouble(f).sum() / o1.size();
        double sum2 = o2.stream().mapToDouble(f).sum() / o2.size();
        return Double.compare(sum1, sum2) * -1;
    }
}
