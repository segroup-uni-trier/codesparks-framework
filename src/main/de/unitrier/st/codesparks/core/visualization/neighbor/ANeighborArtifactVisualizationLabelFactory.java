package de.unitrier.st.codesparks.core.visualization.neighbor;

import de.unitrier.st.codesparks.core.data.AMetricIdentifier;
import de.unitrier.st.codesparks.core.visualization.AVisualizationSequence;

/*
 * Copyright (c), Oliver Moseler, 2020
 */
public abstract class ANeighborArtifactVisualizationLabelFactory extends AVisualizationSequence
        implements INeighborArtifactVisualizationLabelFactory
{
    protected final AMetricIdentifier primaryMetricIdentifier;

    protected ANeighborArtifactVisualizationLabelFactory(final AMetricIdentifier primaryMetricIdentifier)
    {
        this.primaryMetricIdentifier = primaryMetricIdentifier;
    }

    protected ANeighborArtifactVisualizationLabelFactory(final AMetricIdentifier primaryMetricIdentifier, final int sequence)
    {
        super(sequence);
        this.primaryMetricIdentifier = primaryMetricIdentifier;
    }
}