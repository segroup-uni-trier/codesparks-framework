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

public final class DataUtil
{
    private DataUtil() {}

    //TODO: inline in method below
    private static double getThreadFilteredRelativeNumericMetricValueRatioOfArtifact(
            final AArtifact artifact
            , final AMetricIdentifier metricIdentifier
    )
    {
        if (!metricIdentifier.isNumerical() || !metricIdentifier.isRelative())
        { // The metric value is expected to be a relative numeric value, i.e. element of the closed interval [0,1]
            return Double.NaN;
        }
        double ratio = 0D;
        for (final AThreadArtifact threadArtifact : artifact.getThreadArtifacts())
        {
            if (!threadArtifact.isFiltered())
            {
                final double metricValue = threadArtifact.getNumericalMetricValue(metricIdentifier);
                if (metricValue > 0)
                {
                    ratio += metricValue;
                }
            }
        }
//        final double ratio =
//                artifact.getThreadArtifacts()
//                        .stream()
//                        .filter(threadArtifact ->
//                                !threadArtifact.isFiltered() && (threadArtifact.getNumericalMetricValue(metricIdentifier) > 0)
//                        )
//                        .mapToDouble(threadArtifact -> threadArtifact.getNumericalMetricValue(metricIdentifier))
//                        .reduce(0d, Double::sum);
        assert !Double.isNaN(ratio) : "Ratio is NaN!";
        return ratio;
    }

    public static double getThreadFilteredRelativeNumericMetricValueOf(
            final AArtifact artifact
            , final AMetricIdentifier metricIdentifier
    )
    {
        if (artifact == null || metricIdentifier == null)
        {
            return Double.NaN;
        }
        double metricValue = artifact.getNumericalMetricValue(metricIdentifier);
        double ratio = 1d;
        if (artifact.hasThreads())
        {
            ratio = DataUtil.getThreadFilteredRelativeNumericMetricValueRatioOfArtifact(artifact, metricIdentifier);
        }
//        if (Double.isNaN(ratio))
//        {
//            ratio = 1d;
//        }
        //noinspection UnnecessaryLocalVariable : Not inlined because of debugging reasons
        final double threadFilteredMetricValue = metricValue * ratio;
        return threadFilteredMetricValue;
    }
}
