package de.unitrier.st.codesparks.core.data;

import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;
import java.util.function.ToDoubleFunction;

public class CodeSparksThreadClusterComparator implements Comparator<CodeSparksThreadCluster>
{
    private final static Map<IMetricIdentifier, Comparator<CodeSparksThreadCluster>> comparators = new HashMap<>();

    public static Comparator<CodeSparksThreadCluster> getInstance(final IMetricIdentifier metricIdentifier)
    {
        synchronized (CodeSparksThreadClusterComparator.class)
        {
            Comparator<CodeSparksThreadCluster> codeSparksThreadClusterComparator = comparators.get(metricIdentifier);
            if (codeSparksThreadClusterComparator == null)
            {
                codeSparksThreadClusterComparator = new CodeSparksThreadClusterComparator(metricIdentifier);
                comparators.put(metricIdentifier, codeSparksThreadClusterComparator);
            }
            return codeSparksThreadClusterComparator;
        }
    }

    private final IMetricIdentifier metricIdentifier;

    private CodeSparksThreadClusterComparator(final IMetricIdentifier metricIdentifier)
    {
        this.metricIdentifier = metricIdentifier;
    }

    @Override
    public int compare(CodeSparksThreadCluster o1, CodeSparksThreadCluster o2)
    {
        final ToDoubleFunction<ACodeSparksThread> f = thread -> thread.getNumericalMetricValue(metricIdentifier);
        double sum1 = o1.stream().mapToDouble(f).sum() / o1.size();
        double sum2 = o2.stream().mapToDouble(f).sum() / o2.size();
        return Double.compare(sum1, sum2) * -1;
    }
}
