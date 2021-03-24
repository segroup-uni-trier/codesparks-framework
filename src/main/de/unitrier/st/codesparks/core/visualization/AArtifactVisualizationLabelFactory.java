package de.unitrier.st.codesparks.core.visualization;

import de.unitrier.st.codesparks.core.data.AMetricIdentifier;

/*
 * Copyright (C) 2021, Oliver Moseler
 */
public abstract class AArtifactVisualizationLabelFactory extends AVisualizationLabelFactory implements IArtifactVisualizationLabelFactory
{
    protected AArtifactVisualizationLabelFactory(final AMetricIdentifier primaryMetricIdentifier)
    {
        super(primaryMetricIdentifier);
    }

    protected AArtifactVisualizationLabelFactory(final AMetricIdentifier primaryMetricIdentifier, final int sequence)
    {
        super(primaryMetricIdentifier, sequence);
    }

    protected AArtifactVisualizationLabelFactory(final AMetricIdentifier primaryMetricIdentifier, final int sequence, final int xOffsetLeft)
    {
        super(primaryMetricIdentifier, sequence, xOffsetLeft);
    }
}