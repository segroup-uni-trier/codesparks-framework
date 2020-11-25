package de.unitrier.st.codesparks.core.visualization.neighbor;

import de.unitrier.st.codesparks.core.data.IMetricIdentifier;
import de.unitrier.st.codesparks.core.visualization.AVisualizationSequence;

public abstract class ANeighborArtifactVisualizationLabelFactory extends AVisualizationSequence
        implements INeighborArtifactVisualizationLabelFactory
{
    protected final IMetricIdentifier primaryMetricIdentifier;

    protected ANeighborArtifactVisualizationLabelFactory(final IMetricIdentifier primaryMetricIdentifier)
    {
        this.primaryMetricIdentifier = primaryMetricIdentifier;
    }

    protected ANeighborArtifactVisualizationLabelFactory(final IMetricIdentifier primaryMetricIdentifier, final int sequence)
    {
        super(sequence, false);
        this.primaryMetricIdentifier = primaryMetricIdentifier;
    }
}