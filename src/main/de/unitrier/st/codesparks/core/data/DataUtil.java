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

    public static double getThreadFilteredMetricValue(ABaseArtifact artifact)
    {
        final double metricValue = artifact.getMetricValue();
        double ratio = 1d;
        if (artifact.hasThreads())
        {
//            final double threadMetricValueRatio = DataUtil.getThreadMetricValueRatio(artifact, CodeSparksThread::getMetricValue);
            ratio = DataUtil.getThreadMetricValueRatio(artifact, ACodeSparksThread::getMetricValue);
        }
//        final double threadFilteredMetricValue = metricValue * threadMetricValueRatio;
        final double threadFilteredMetricValue = metricValue * ratio;
        return threadFilteredMetricValue;
    }


    public static double getThreadFilteredMetricValueSelf(ABaseArtifact artifact)
    {
        final double metricValue = artifact.getMetricValue();
        final double threadMetricValueRatio = DataUtil.getThreadMetricValueRatio(artifact, ACodeSparksThread::getMetricValueSelf);
        final double threadFilteredMetricValueSelf = metricValue * threadMetricValueRatio;
        return threadFilteredMetricValueSelf;
    }

}
