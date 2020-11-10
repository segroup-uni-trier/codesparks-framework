package de.unitrier.st.codesparks.core.data;

import java.util.Comparator;

public class CodeSparksThreadComparator implements Comparator<CodeSparksThread>
{
    @Override
    public int compare(CodeSparksThread o1, CodeSparksThread o2)
    {
        return Double.compare(o1.getMetricValue(), o2.getMetricValue()) * -1;
    }
}
