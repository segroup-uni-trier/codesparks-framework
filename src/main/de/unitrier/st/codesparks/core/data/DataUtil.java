package de.unitrier.st.codesparks.core.data;

import java.util.function.ToDoubleFunction;

public final class DataUtil
{
    private DataUtil() {}

    public static double getThreadMetricValueRatio(ABaseArtifact artifact,
                                                   ToDoubleFunction<CodeSparksThread> threadArtifactToDoubleFunc)
    {
        return artifact.getThreadArtifacts()
                .stream()
                .filter(threadArtifact -> !threadArtifact.isFiltered())
                .mapToDouble(threadArtifactToDoubleFunc)
                .reduce(0d, Double::sum);
    }

    public static double getThreadFilteredMetricValue(ABaseArtifact artifact)
    {
        final double metricValue = artifact.getMetricValue();
        final double threadMetricValueRatio = DataUtil.getThreadMetricValueRatio(artifact, CodeSparksThread::getMetricValue);
        final double threadFilteredMetricValue = metricValue * threadMetricValueRatio;
        return threadFilteredMetricValue;
    }


    public static double getThreadFilteredMetricValueSelf(ABaseArtifact artifact)
    {
        final double metricValue = artifact.getMetricValue();
        final double threadMetricValueRatio = DataUtil.getThreadMetricValueRatio(artifact, CodeSparksThread::getMetricValueSelf);
        final double threadFilteredMetricValueSelf = metricValue * threadMetricValueRatio;
        return threadFilteredMetricValueSelf;
    }

}
