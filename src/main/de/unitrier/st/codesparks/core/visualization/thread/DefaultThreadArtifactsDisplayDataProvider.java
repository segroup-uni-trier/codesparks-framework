/*
 * Copyright (c) 2021. Oliver Moseler
 */
package de.unitrier.st.codesparks.core.visualization.thread;

import de.unitrier.st.codesparks.core.data.AArtifact;
import de.unitrier.st.codesparks.core.data.AThreadArtifact;
import de.unitrier.st.codesparks.core.data.AMetricIdentifier;

import java.util.Set;

public class DefaultThreadArtifactsDisplayDataProvider implements IThreadArtifactsDisplayDataProvider
{
    private final AMetricIdentifier metricIdentifier;

    public DefaultThreadArtifactsDisplayDataProvider(final AMetricIdentifier metricIdentifier)
    {
        this.metricIdentifier = metricIdentifier;
    }

    @Override
    public ThreadArtifactDisplayData getDisplayDataOfSelectedThreads(final AArtifact artifact, final Set<AThreadArtifact> selectedThreadArtifacts)
    {
        final ThreadArtifactDisplayData threadArtifactDisplayData = new ThreadArtifactDisplayData();

        double sum = 0;
        for (final AThreadArtifact threadArtifact : selectedThreadArtifacts)
        {
            sum += threadArtifact.getNumericalMetricValue(metricIdentifier);
        }

        threadArtifactDisplayData.setMetricValueSum(sum);
        threadArtifactDisplayData.setMetricValueAvg(sum / selectedThreadArtifacts.size());
        threadArtifactDisplayData.setNumberOfThreads(selectedThreadArtifacts.size());
        final int numberOfSelectedThreadTypesWithNumericMetricValueInSelection =
                ThreadVisualizationUtil.getNumberOfSelectedThreadTypesWithNumericMetricValueInSelection(artifact, metricIdentifier, selectedThreadArtifacts);
        threadArtifactDisplayData.setNumberOfThreadTypes(numberOfSelectedThreadTypesWithNumericMetricValueInSelection);

        return threadArtifactDisplayData;
    }

    @Override
    public ThreadArtifactDisplayData getDisplayDataOfHoveredThreads(final AArtifact artifact, final Set<AThreadArtifact> hoveredThreadArtifacts)
    {
        final ThreadArtifactDisplayData threadArtifactDisplayData = new ThreadArtifactDisplayData();

        double sum = 0;
        for (final AThreadArtifact threadArtifact : hoveredThreadArtifacts)
        {
            sum += threadArtifact.getNumericalMetricValue(metricIdentifier);
        }

        threadArtifactDisplayData.setMetricValueSum(sum);
        threadArtifactDisplayData.setMetricValueAvg(sum / hoveredThreadArtifacts.size());
        threadArtifactDisplayData.setNumberOfThreads(hoveredThreadArtifacts.size());
        final int numberOfSelectedThreadTypesWithNumericMetricValueInSelection =
                ThreadVisualizationUtil.getNumberOfSelectedThreadTypesWithNumericMetricValueInSelection(
                        artifact
                        , metricIdentifier
                        , hoveredThreadArtifacts
                        , true
                );
        threadArtifactDisplayData.setNumberOfThreadTypes(numberOfSelectedThreadTypesWithNumericMetricValueInSelection);

        return threadArtifactDisplayData;
    }
}