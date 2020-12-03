package de.unitrier.st.codesparks.core.data;

import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;
import java.util.function.ToDoubleFunction;

/*
 * Copyright (c), Oliver Moseler, 2020
 */
public class ThreadArtifactClusterComparator implements Comparator<ThreadArtifactCluster>
{
    private final static Map<IMetricIdentifier, Comparator<ThreadArtifactCluster>> comparators = new HashMap<>();

    public static Comparator<ThreadArtifactCluster> getInstance(final IMetricIdentifier metricIdentifier)
    {
        synchronized (ThreadArtifactClusterComparator.class)
        {
            Comparator<ThreadArtifactCluster> codeSparksThreadClusterComparator = comparators.get(metricIdentifier);
            if (codeSparksThreadClusterComparator == null)
            {
                codeSparksThreadClusterComparator = new ThreadArtifactClusterComparator(metricIdentifier);
                comparators.put(metricIdentifier, codeSparksThreadClusterComparator);
            }
            return codeSparksThreadClusterComparator;
        }
    }

    private final IMetricIdentifier metricIdentifier;

    private ThreadArtifactClusterComparator(final IMetricIdentifier metricIdentifier)
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
