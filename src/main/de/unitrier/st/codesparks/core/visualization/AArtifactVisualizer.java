package de.unitrier.st.codesparks.core.visualization;

import de.unitrier.st.codesparks.core.data.ACodeSparksArtifact;

import java.util.Collection;

/*
 * Copyright (c), Oliver Moseler, 2020
 */
public abstract class AArtifactVisualizer
{
    protected final Collection<String> metricIdentifiers;

    protected AArtifactVisualizer(final Collection<String> metricIdentifiers)
    {
        this.metricIdentifiers = metricIdentifiers;
    }

    public Collection<String> getMetricIdentifiers()
    {
        return this.metricIdentifiers;
    }

    public abstract AArtifactVisualization createArtifactVisualization(ACodeSparksArtifact artifact, AArtifactVisualizationLabelFactory... factories);
}
