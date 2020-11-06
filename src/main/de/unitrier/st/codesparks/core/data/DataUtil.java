package de.unitrier.st.codesparks.core.data;

import java.util.function.ToDoubleFunction;

public final class DataUtil
{
    private DataUtil() {}

    public static double getThreadMetricValueRatio(ABaseProfilingArtifact artifact,
                                                   ToDoubleFunction<ThreadArtifact> threadArtifactToDoubleFunc)
    {
        return artifact.getThreadArtifacts()
                .stream()
                .filter(threadArtifact -> !threadArtifact.isFiltered())
                .mapToDouble(threadArtifactToDoubleFunc)
                .reduce(0d, Double::sum);
    }

    public static double getThreadFilteredMetricValue(ABaseProfilingArtifact artifact)
    {
        final double metricValue = artifact.getMetricValue();
        final double threadMetricValueRatio = DataUtil.getThreadMetricValueRatio(artifact, ThreadArtifact::getMetricValue);
        final double threadFilteredMetricValue = metricValue * threadMetricValueRatio;
        return threadFilteredMetricValue;
    }


    public static double getThreadFilteredMetricValueSelf(ABaseProfilingArtifact artifact)
    {
        final double metricValue = artifact.getMetricValue();
        final double threadMetricValueRatio = DataUtil.getThreadMetricValueRatio(artifact, ThreadArtifact::getMetricValueSelf);
        final double threadFilteredMetricValueSelf = metricValue * threadMetricValueRatio;
        return threadFilteredMetricValueSelf;
    }

}
