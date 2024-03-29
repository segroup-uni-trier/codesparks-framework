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

public class ThreadArtifactComparator implements Comparator<AThreadArtifact>
{
    private static final Map<AMetricIdentifier, Comparator<AThreadArtifact>> instances = new HashMap<>(2);

    public static Comparator<AThreadArtifact> getInstance(final AMetricIdentifier metricIdentifier)
    {
        synchronized (instances)
        {
            Comparator<AThreadArtifact> comparator = instances.get(metricIdentifier);
            if (comparator == null)
            {
                comparator = new ThreadArtifactComparator(metricIdentifier);
                instances.put(metricIdentifier, comparator);
            }
            return comparator;
        }
    }

    private final AMetricIdentifier metricIdentifier;

    private ThreadArtifactComparator(final AMetricIdentifier metricIdentifier)
    {
        this.metricIdentifier = metricIdentifier;
    }

    @Override
    public int compare(AThreadArtifact o1, AThreadArtifact o2)
    {
        return Double.compare(o1.getNumericalMetricValue(metricIdentifier), o2.getNumericalMetricValue(metricIdentifier)) * -1;
    }
}
