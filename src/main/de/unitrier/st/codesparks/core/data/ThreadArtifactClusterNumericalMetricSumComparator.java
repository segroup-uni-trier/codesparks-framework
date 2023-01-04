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

import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;
import java.util.function.ToDoubleFunction;

public class ThreadArtifactClusterNumericalMetricSumComparator implements Comparator<ThreadArtifactCluster>
{
    private final static Map<AMetricIdentifier, Comparator<ThreadArtifactCluster>> comparators = new HashMap<>();

    public static Comparator<ThreadArtifactCluster> getInstance(final AMetricIdentifier metricIdentifier)
    {
        synchronized (ThreadArtifactClusterNumericalMetricSumComparator.class)
        {
            Comparator<ThreadArtifactCluster> codeSparksThreadClusterComparator = comparators.get(metricIdentifier);
            if (codeSparksThreadClusterComparator == null)
            {
                codeSparksThreadClusterComparator = new ThreadArtifactClusterNumericalMetricSumComparator(metricIdentifier);
                comparators.put(metricIdentifier, codeSparksThreadClusterComparator);
            }
            return codeSparksThreadClusterComparator;
        }
    }

    private final AMetricIdentifier metricIdentifier;

    private ThreadArtifactClusterNumericalMetricSumComparator(final AMetricIdentifier metricIdentifier)
    {
        this.metricIdentifier = metricIdentifier;
    }

    @Override
    public int compare(ThreadArtifactCluster o1, ThreadArtifactCluster o2)
    {
        final ToDoubleFunction<AThreadArtifact> f = thread -> thread.getNumericalMetricValue(metricIdentifier);
        double sum1 = o1.stream().mapToDouble(f).sum() / o1.size();
        double sum2 = o2.stream().mapToDouble(f).sum() / o2.size();
        return Double.compare(sum1, sum2) * -1;
    }
}
