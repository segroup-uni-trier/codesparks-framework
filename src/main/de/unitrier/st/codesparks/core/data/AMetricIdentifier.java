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

public abstract class AMetricIdentifier implements IMetricIdentifier
{
    @Override
    public int hashCode()
    {
        return getIdentifier().hashCode();
    }

    @Override
    public boolean equals(final Object obj)
    {
        if (obj == null) return false;
        if (!(obj instanceof IMetricIdentifier)) return false;
        return this.getIdentifier().equals(((IMetricIdentifier) obj).getIdentifier());
    }

    @Override
    public String getValueDisplayString(final Object metricValue)
    {
        if (metricValue != null)
        {
            return metricValue.toString();
        }
        return "N/A";
    }

    @Override
    public String toString()
    {
        return getDisplayString();
    }

    @Override
    public final boolean isNumerical()
    {
        final Class<?> metricValueType = getMetricValueType();
        return Double.class.equals(metricValueType);
    }
}
