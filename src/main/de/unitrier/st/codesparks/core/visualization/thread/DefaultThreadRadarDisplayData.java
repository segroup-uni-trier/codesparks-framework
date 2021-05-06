package de.unitrier.st.codesparks.core.visualization.thread;

import de.unitrier.st.codesparks.core.data.AArtifact;
import de.unitrier.st.codesparks.core.data.AThreadArtifact;
import de.unitrier.st.codesparks.core.data.AMetricIdentifier;

import java.util.Set;

/*
 * Copyright (c), Oliver Moseler, 2020
 */
public class DefaultThreadRadarDisplayData implements IThreadRadarDisplayData
{
    private final AMetricIdentifier metricIdentifier;

    public DefaultThreadRadarDisplayData(final AMetricIdentifier metricIdentifier)
    {
        this.metricIdentifier = metricIdentifier;
    }

    @Override
    public CodeSparksThreadDisplayData getSelectedThreadData(AArtifact artifact, Set<AThreadArtifact> selectedCodeSparksThreads)
    {
        final CodeSparksThreadDisplayData codeSparksThreadDisplayData = new CodeSparksThreadDisplayData();

        double sum = 0;
        for (AThreadArtifact selectedCodeSparksThread : selectedCodeSparksThreads)
        {
            sum += selectedCodeSparksThread.getNumericalMetricValue(metricIdentifier);
        }

        codeSparksThreadDisplayData.setMetricValueSum(sum);
        codeSparksThreadDisplayData.setMetricValueAvg(sum / selectedCodeSparksThreads.size());
        codeSparksThreadDisplayData.setNumberOfThreads(selectedCodeSparksThreads.size());
        codeSparksThreadDisplayData.setNumberOfThreadTypes(ThreadVisualizationUtil.getNumberOfFilteredThreadTypesInSelection(artifact,
                selectedCodeSparksThreads));

        return codeSparksThreadDisplayData;
    }

    @Override
    public CodeSparksThreadDisplayData getHoveredThreadData(AArtifact artifact, Set<AThreadArtifact> hoveredCodeSparksThreads)
    {
        final CodeSparksThreadDisplayData codeSparksThreadDisplayData = new CodeSparksThreadDisplayData();

        double sum = 0;
        for (AThreadArtifact selectedCodeSparksThread : hoveredCodeSparksThreads)
        {
            sum += selectedCodeSparksThread.getNumericalMetricValue(metricIdentifier);
        }

        codeSparksThreadDisplayData.setMetricValueSum(sum);
        codeSparksThreadDisplayData.setMetricValueAvg(sum / hoveredCodeSparksThreads.size());
        codeSparksThreadDisplayData.setNumberOfThreads(hoveredCodeSparksThreads.size());
        codeSparksThreadDisplayData.setNumberOfThreadTypes(ThreadVisualizationUtil.getNumberOfFilteredThreadTypesInSelection(artifact,
                hoveredCodeSparksThreads));

        return codeSparksThreadDisplayData;
    }
}