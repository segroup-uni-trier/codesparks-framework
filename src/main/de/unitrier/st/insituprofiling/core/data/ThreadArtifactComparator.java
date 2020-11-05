package de.unitrier.st.insituprofiling.core.data;

import java.util.Comparator;

public class ThreadArtifactComparator implements Comparator<ThreadArtifact>
{
    @Override
    public int compare(ThreadArtifact o1, ThreadArtifact o2)
    {
        return Double.compare(o1.getMetricValue(), o2.getMetricValue()) * -1;
    }
}
