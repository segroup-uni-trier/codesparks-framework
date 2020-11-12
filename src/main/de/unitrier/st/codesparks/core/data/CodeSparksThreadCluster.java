package de.unitrier.st.codesparks.core.data;

import java.util.ArrayList;

public class CodeSparksThreadCluster extends ArrayList<ACodeSparksThread>
{
    private static int clusterId = 0;

    private static synchronized int getNextId()
    {
        return clusterId++;
    }

    private final int id;

    CodeSparksThreadCluster()
    {
        id = getNextId();
    }

    public final int getId()
    {
        return id;
    }

    @Override
    public String toString()
    {
        StringBuilder strb = new StringBuilder();
        for (ACodeSparksThread codeSparksThread : this)
        {
            strb.append(codeSparksThread.getIdentifier()).append(",");
        }
        strb.deleteCharAt(strb.length() - 1);
        return strb.toString();
    }
}
