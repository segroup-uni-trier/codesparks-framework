package de.unitrier.st.codesparks.core.data;

/*
 * Copyright (c), Oliver Moseler, 2020
 */
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
        return artifact.getThreadArtifacts()
                .stream()
                .filter(threadArtifact -> !threadArtifact.isFiltered())
                .mapToDouble(threadArtifact -> threadArtifact.getNumericalMetricValue(metricIdentifier))
                .reduce(0d, Double::sum);
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
        //noinspection UnnecessaryLocalVariable : Not inlined because of debugging reasons
        final double threadFilteredMetricValue = metricValue * ratio;
        return threadFilteredMetricValue;
    }
}
