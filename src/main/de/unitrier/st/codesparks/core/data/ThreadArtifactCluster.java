package de.unitrier.st.codesparks.core.data;

import java.util.ArrayList;

/*
 * Copyright (c), Oliver Moseler, 2020
 */
public class ThreadArtifactCluster extends ArrayList<AThreadArtifact>
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
        for (AThreadArtifact codeSparksThread : this)
        {
            strb.append(codeSparksThread.getIdentifier()).append(",");
        }
        strb.deleteCharAt(strb.length() - 1);
        return strb.toString();
    }
}
