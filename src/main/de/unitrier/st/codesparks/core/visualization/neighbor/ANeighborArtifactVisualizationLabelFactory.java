package de.unitrier.st.codesparks.core.visualization.neighbor;

import de.unitrier.st.codesparks.core.visualization.AVisualizationSequence;

public abstract class ANeighborArtifactVisualizationLabelFactory extends AVisualizationSequence
        implements INeighborArtifactVisualizationLabelFactory
{
    protected final String primaryMetricIdentifier;

    protected ANeighborArtifactVisualizationLabelFactory(final String primaryMetricIdentifier)
    {
        this.primaryMetricIdentifier = primaryMetricIdentifier;
    }

    protected ANeighborArtifactVisualizationLabelFactory(final int sequence, final String primaryMetricIdentifier)
    {
        super(sequence, false);
        this.primaryMetricIdentifier = primaryMetricIdentifier;
    }
}