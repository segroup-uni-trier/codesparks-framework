package de.unitrier.st.codesparks.core.data;

import java.util.Comparator;

public class CodeSparksThreadComparator implements Comparator<ACodeSparksThread>
{
    @Override
    public int compare(ACodeSparksThread o1, ACodeSparksThread o2)
    {
        return Double.compare(o1.getMetricValue(), o2.getMetricValue()) * -1;
    }
}
