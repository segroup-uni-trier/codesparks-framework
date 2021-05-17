package de.unitrier.st.codesparks.core.visualization.thread;

import de.unitrier.st.codesparks.core.data.AArtifact;
import de.unitrier.st.codesparks.core.data.AThreadArtifact;
import de.unitrier.st.codesparks.core.data.AMetricIdentifier;

import java.util.Set;

/*
 * Copyright (c), Oliver Moseler, 2020
 */
public class DefaultThreadArtifactsDisplayData implements IThreadArtifactsDisplayData
{
    private final AMetricIdentifier metricIdentifier;

    public DefaultThreadArtifactsDisplayData(final AMetricIdentifier metricIdentifier)
    {
        this.metricIdentifier = metricIdentifier;
    }

    @Override
    public ThreadArtifactDisplayData getDisplayDataOfSelectedThreads(AArtifact artifact, Set<AThreadArtifact> selectedCodeSparksThreads)
    {
        final ThreadArtifactDisplayData threadArtifactDisplayData = new ThreadArtifactDisplayData();

        double sum = 0;
        for (AThreadArtifact selectedCodeSparksThread : selectedCodeSparksThreads)
        {
            sum += selectedCodeSparksThread.getNumericalMetricValue(metricIdentifier);
        }

        threadArtifactDisplayData.setMetricValueSum(sum);
        threadArtifactDisplayData.setMetricValueAvg(sum / selectedCodeSparksThreads.size());
        threadArtifactDisplayData.setNumberOfThreads(selectedCodeSparksThreads.size());
        threadArtifactDisplayData.setNumberOfThreadTypes(ThreadVisualizationUtil.getNumberOfFilteredThreadTypesInSelection(artifact,
                selectedCodeSparksThreads));

        return threadArtifactDisplayData;
    }

    @Override
    public ThreadArtifactDisplayData getDisplayDataOfHoveredThreads(AArtifact artifact, Set<AThreadArtifact> hoveredCodeSparksThreads)
    {
        final ThreadArtifactDisplayData threadArtifactDisplayData = new ThreadArtifactDisplayData();

        double sum = 0;
        for (AThreadArtifact selectedCodeSparksThread : hoveredCodeSparksThreads)
        {
            sum += selectedCodeSparksThread.getNumericalMetricValue(metricIdentifier);
        }

        threadArtifactDisplayData.setMetricValueSum(sum);
        threadArtifactDisplayData.setMetricValueAvg(sum / hoveredCodeSparksThreads.size());
        threadArtifactDisplayData.setNumberOfThreads(hoveredCodeSparksThreads.size());
        threadArtifactDisplayData.setNumberOfThreadTypes(ThreadVisualizationUtil.getNumberOfFilteredThreadTypesInSelection(artifact,
                hoveredCodeSparksThreads));

        return threadArtifactDisplayData;
    }
}