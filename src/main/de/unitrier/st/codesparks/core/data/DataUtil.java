package de.unitrier.st.codesparks.core.data;

import java.util.function.ToDoubleFunction;

public final class DataUtil
{
    private DataUtil() {}

    public static double getThreadMetricValueRatio(ABaseArtifact artifact,
                                                   ToDoubleFunction<ACodeSparksThread> threadArtifactToDoubleFunc)
    {
        return artifact.getThreadArtifacts()
                .stream()
                .filter(threadArtifact -> !threadArtifact.isFiltered())
                .mapToDouble(threadArtifactToDoubleFunc)
                .reduce(0d, Double::sum);
    }

    public static double getThreadFilteredMetricValue(ABaseArtifact artifact, final String metricIdentifier)
    {
        final double metricValue = artifact.getNumericalMetricValue(metricIdentifier);
        double ratio = 1d;
        if (artifact.hasThreads())
        {
//            final double threadMetricValueRatio = DataUtil.getThreadMetricValueRatio(artifact, CodeSparksThread::getMetricValue);
            ratio = DataUtil.getThreadMetricValueRatio(artifact, thread -> thread.getNumericalMetricValue(metricIdentifier));
        }
//        final double threadFilteredMetricValue = metricValue * threadMetricValueRatio;
        final double threadFilteredMetricValue = metricValue * ratio;
        return threadFilteredMetricValue;
    }
}
