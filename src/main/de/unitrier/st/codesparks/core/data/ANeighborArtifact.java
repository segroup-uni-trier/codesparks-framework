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

import de.unitrier.st.codesparks.core.CoreUtil;

/**
 * Created by Oliver Moseler on 22.09.2014.
 */
public abstract class ANeighborArtifact extends AArtifact
{
    protected ANeighborArtifact(
            final String identifier
            , final String name
            , final Class<? extends AThreadArtifact> threadArtifactClass
            , final int lineNumber
    )
    {
        super(identifier, name, threadArtifactClass);
        this.lineNumber = lineNumber;
    }

    public double getNumericalMetricValueRelativeTo(final AArtifact artifact, final AMetricIdentifier metricIdentifier)
    {
        if (!metricIdentifier.isNumerical())
        {
            return Double.NaN;
        }
        final double metricValue = getNumericalMetricValue(metricIdentifier);
        final double artifactNumericalMetricValue = artifact.getNumericalMetricValue(metricIdentifier);
        return metricValue / artifactNumericalMetricValue;
    }

    public String getDisplayStringRelativeTo(final AArtifact artifact, final AMetricIdentifier metricIdentifier, final int maxLen)
    {
        return CoreUtil.reduceToLength(getDisplayStringRelativeTo(artifact, metricIdentifier), maxLen);
    }

    public String getDisplayStringRelativeTo(final AArtifact artifact, final AMetricIdentifier metricIdentifier)
    {
        String metricValueString;
        if (metricIdentifier.isNumerical())
        {
            final double valueRelativeTo = getNumericalMetricValueRelativeTo(artifact, metricIdentifier);
            metricValueString = CoreUtil.formatPercentage(valueRelativeTo);
        } else
        {
            metricValueString = getMetricValue(metricIdentifier).toString();
        }
        return name + " - " + metricIdentifier.getDisplayString() + ": " + metricValueString;
    }
}
