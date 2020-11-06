package de.unitrier.st.codesparks.core.data;

import java.util.ArrayList;

public class ThreadArtifactCluster extends ArrayList<ThreadArtifact>
{
    private static int clusterId = 0;

    private static synchronized int getNextId()
    {
        return clusterId++;
    }

    private final int id;

    ThreadArtifactCluster()
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
        for (ThreadArtifact threadArtifact : this)
        {
            strb.append(threadArtifact.getIdentifier()).append(",");
        }
        strb.deleteCharAt(strb.length() - 1);
        return strb.toString();
    }
}
