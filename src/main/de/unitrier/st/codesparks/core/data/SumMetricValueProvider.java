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

import java.util.Collection;

public class SumMetricValueProvider extends AMetricValueProvider
{
    public SumMetricValueProvider(final Collection<AMetricIdentifier> metricIdentifiers)
    {
        super(metricIdentifiers);
    }

    @Override
    public double metricValueFor(final AArtifact artifact)
    {
        if (artifact == null)
        {
            return 0;
        }
        double sum = 0;
        for (final AMetricIdentifier metricIdentifier : metricIdentifiers)
        {
            final double numericalMetricValue = artifact.getNumericalMetricValue(metricIdentifier);
            sum += numericalMetricValue;
        }
        return sum;
    }
}
