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

import java.util.Objects;

public abstract class KThreadArtifactClusteringStrategy extends AThreadArtifactClusteringStrategy
{
    protected int k;

    protected KThreadArtifactClusteringStrategy(final AMetricIdentifier metricIdentifier)
    {
        super(metricIdentifier);
    }

    protected KThreadArtifactClusteringStrategy(final AMetricIdentifier metricIdentifier, final int k)
    {
        super(metricIdentifier);
        this.k = k;
    }

    public int getK()
    {
        return k;
    }

    public void setK(final int k)
    {
        this.k = k;
    }

    @Override
    public boolean equals(final Object o)
    {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;
        final KThreadArtifactClusteringStrategy that = (KThreadArtifactClusteringStrategy) o;
        return k == that.k;
    }

    @Override
    public int hashCode()
    {
        return Objects.hash(super.hashCode(), k);
    }
}
