package de.unitrier.st.codesparks.core.data;

import java.util.ArrayList;

/*
 * Copyright (c), Oliver Moseler, 2021
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

    /**
     * Summed distance from thr to all other threads in this cluster concerning the metric denoted by metricIdentifier.
     *
     * @param thr              The thread of which to compute the summed distance to all other threads to.
     * @param metricIdentifier The identifier if the metric which to account
     * @return The summed distance from thr to all other threads in this cluster.
     */
    public double dist(final AThreadArtifact thr, final AMetricIdentifier metricIdentifier)
    {
        int sub = 0;
        if (contains(thr))
        {
            sub = 1;
        }
        final int n = size() - sub;
        if (n == 0) // this cluster contains thr and thr is the only element in it
        {
            return 0D;
        }
        double sum = 0D;
        for (AThreadArtifact t : this)
        {
            if (!t.equals(thr))
            {
                sum += thr.dist(t, metricIdentifier);
            }
        }
        //noinspection UnnecessaryLocalVariable
        final double dist = 1D / n * sum;
        return dist;
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
