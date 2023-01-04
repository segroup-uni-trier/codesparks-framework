/*
 *  Copyright 2023 Oliver Moseler
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */
package de.unitrier.st.codesparks.core.data;

import java.util.ArrayList;

public class ThreadArtifactCluster extends ArrayList<AThreadArtifact>
{
    private static long clusterId = 0;

    private static synchronized long getNextId()
    {
        return clusterId++;
    }

    private final long id;

    public ThreadArtifactCluster()
    {
        id = getNextId();
    }

    public final long getId()
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
        final StringBuilder strb = new StringBuilder();
        for (final AThreadArtifact threadArtifact : this)
        {
            strb.append(threadArtifact.getIdentifier()).append(",");
        }
        strb.deleteCharAt(strb.length() - 1);
        return strb.toString();
    }
}
