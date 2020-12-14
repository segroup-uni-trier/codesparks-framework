package de.unitrier.st.codesparks.core.data;

import java.util.function.ToDoubleFunction;

/*
 * Copyright (c), Oliver Moseler, 2020
 */
public final class DataUtil
{
    private DataUtil() {}

    public static double getThreadMetricValueRatio(
            final AArtifact artifact
            , final ToDoubleFunction<AThreadArtifact> threadArtifactToDoubleFunc
    )
    {
        return artifact.getThreadArtifacts()
                .stream()
                .filter(threadArtifact -> !threadArtifact.isFiltered())
                .mapToDouble(threadArtifactToDoubleFunc)
                .reduce(0d, Double::sum);
    }

    public static double getThreadFilteredMetricValue(
            final AArtifact artifact
            , final IMetricIdentifier metricIdentifier
    )
    {
        double metricValue = artifact.getNumericalMetricValue(metricIdentifier);
        double ratio = 1d;
        if (artifact.hasThreads())
        {
            ratio = DataUtil.getThreadMetricValueRatio(artifact, thread -> thread.getNumericalMetricValue(metricIdentifier));
        }
        //noinspection UnnecessaryLocalVariable : Not inlined because of debugging reasons
        final double threadFilteredMetricValue = metricValue * ratio;
        return threadFilteredMetricValue;
    }
}
