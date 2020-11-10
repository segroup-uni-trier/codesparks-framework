package de.unitrier.st.codesparks.core.data;

import java.util.Comparator;

public class CodeSparksThreadClusterComparator implements Comparator<CodeSparksThreadCluster>
{
    private static Comparator<CodeSparksThreadCluster> instance;

    public static Comparator<CodeSparksThreadCluster> getInstance(){
        if (instance == null)
        {
            synchronized (CodeSparksThreadClusterComparator.class)
            {
                if (instance == null)
                {
                    instance = new CodeSparksThreadClusterComparator();
                }
            }
        }
        return instance;
    }

    private CodeSparksThreadClusterComparator(){}

    @Override
    public int compare(CodeSparksThreadCluster o1, CodeSparksThreadCluster o2)
    {
        double sum1 = o1.stream().mapToDouble(CodeSparksThread::getMetricValue).sum() / o1.size();
        double sum2 = o2.stream().mapToDouble(CodeSparksThread::getMetricValue).sum() / o2.size();
        return Double.compare(sum1, sum2) * -1;
    }
}
