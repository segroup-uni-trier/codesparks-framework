package de.unitrier.st.insituprofiling.core.data;

import java.util.Comparator;

public class ThreadArtifactClusterComparator implements Comparator<ThreadArtifactCluster>
{
    private static Comparator<ThreadArtifactCluster> instance;

    public static Comparator<ThreadArtifactCluster> getInstance(){
        if (instance == null)
        {
            synchronized (ThreadArtifactClusterComparator.class)
            {
                if (instance == null)
                {
                    instance = new ThreadArtifactClusterComparator();
                }
            }
        }
        return instance;
    }

    private ThreadArtifactClusterComparator(){}

    @Override
    public int compare(ThreadArtifactCluster o1, ThreadArtifactCluster o2)
    {
        double sum1 = o1.stream().mapToDouble(ThreadArtifact::getMetricValue).sum() / o1.size();
        double sum2 = o2.stream().mapToDouble(ThreadArtifact::getMetricValue).sum() / o2.size();
        return Double.compare(sum1, sum2) * -1;
    }
}
