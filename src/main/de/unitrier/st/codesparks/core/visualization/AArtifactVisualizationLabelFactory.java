package de.unitrier.st.codesparks.core.visualization;

import de.unitrier.st.codesparks.core.data.IMetricIdentifier;

/*
 * Copyright (C) 2020, Oliver Moseler
 */
public abstract class AArtifactVisualizationLabelFactory extends AVisualizationSequence
        implements IArtifactVisualizationLabelFactory
{
    protected final IMetricIdentifier primaryMetricIdentifier;

    public IMetricIdentifier getPrimaryMetricIdentifier()
    {
        return primaryMetricIdentifier;
    }

    protected AArtifactVisualizationLabelFactory(final IMetricIdentifier primaryMetricIdentifier)
    {
        this.primaryMetricIdentifier = primaryMetricIdentifier;
    }

    protected AArtifactVisualizationLabelFactory(final IMetricIdentifier primaryMetricIdentifier, final int sequence)
    {
        super(sequence, false);
        this.primaryMetricIdentifier = primaryMetricIdentifier;
    }

    protected AArtifactVisualizationLabelFactory(final IMetricIdentifier primaryMetricIdentifier, final int sequence, final boolean isDefault)
    {
        super(sequence, isDefault);
        this.primaryMetricIdentifier = primaryMetricIdentifier;
    }
}