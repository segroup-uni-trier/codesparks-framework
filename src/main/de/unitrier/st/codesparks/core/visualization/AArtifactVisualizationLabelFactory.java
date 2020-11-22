package de.unitrier.st.codesparks.core.visualization;

/*
 * Copyright (C) 2020, Oliver Moseler
 */
public abstract class AArtifactVisualizationLabelFactory extends AVisualizationSequence
        implements IArtifactVisualizationLabelFactory
{
    protected final String primaryMetricIdentifier;

    protected AArtifactVisualizationLabelFactory(final String primaryMetricIdentifier)
    {
        this.primaryMetricIdentifier = primaryMetricIdentifier;
    }

    protected AArtifactVisualizationLabelFactory(final int sequence, final String primaryMetricIdentifier)
    {
        super(sequence, false);
        this.primaryMetricIdentifier = primaryMetricIdentifier;
    }

    protected AArtifactVisualizationLabelFactory(final int sequence, final boolean isDefault, final String primaryMetricIdentifier)
    {
        super(sequence, isDefault);
        this.primaryMetricIdentifier = primaryMetricIdentifier;
    }
}