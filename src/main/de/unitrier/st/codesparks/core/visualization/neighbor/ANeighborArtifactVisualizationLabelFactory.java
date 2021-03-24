package de.unitrier.st.codesparks.core.visualization.neighbor;

import de.unitrier.st.codesparks.core.data.AMetricIdentifier;
import de.unitrier.st.codesparks.core.visualization.AVisualizationLabelFactory;

/*
 * Copyright (c), Oliver Moseler, 2020
 */
public abstract class ANeighborArtifactVisualizationLabelFactory extends AVisualizationLabelFactory
        implements INeighborArtifactVisualizationLabelFactory
{

    protected ANeighborArtifactVisualizationLabelFactory(final AMetricIdentifier primaryMetricIdentifier)
    {
        super(primaryMetricIdentifier);
    }

    protected ANeighborArtifactVisualizationLabelFactory(final AMetricIdentifier primaryMetricIdentifier, final int sequence)
    {
        super(primaryMetricIdentifier, sequence);
    }

    protected ANeighborArtifactVisualizationLabelFactory(final AMetricIdentifier primaryMetricIdentifier, final int sequence, final int xOffsetLeft)
    {
        super(primaryMetricIdentifier, sequence, xOffsetLeft);
    }
}