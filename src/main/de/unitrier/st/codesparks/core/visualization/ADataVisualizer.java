package de.unitrier.st.codesparks.core.visualization;

import de.unitrier.st.codesparks.core.IDataVisualizer;
import de.unitrier.st.codesparks.core.visualization.neighbor.ANeighborArtifactVisualizationLabelFactory;
import de.unitrier.st.codesparks.core.visualization.neighbor.INeighborArtifactVisualizer;

import java.util.Arrays;
import java.util.Comparator;
import java.util.Optional;

/*
 * Copyright (c), Oliver Moseler, 2020
 */
public abstract class ADataVisualizer implements IDataVisualizer
{
    protected final IArtifactVisualizer artifactVisualizer;
    protected final AArtifactVisualizationLabelFactory[] artifactLabelFactories;
    protected final INeighborArtifactVisualizer neighborArtifactVisualizer;
    protected final ANeighborArtifactVisualizationLabelFactory[] neighborLabelFactories;

    ADataVisualizer(
            final IArtifactVisualizer artifactVisualizer
            , final INeighborArtifactVisualizer neighborArtifactVisualizer
            , final AArtifactVisualizationLabelFactory[] artifactLabelFactories
            , final ANeighborArtifactVisualizationLabelFactory[] neighborLabelFactories
    )
    {
        this.artifactVisualizer = artifactVisualizer;
        this.neighborArtifactVisualizer = neighborArtifactVisualizer;
        this.artifactLabelFactories = artifactLabelFactories;
        this.neighborLabelFactories = neighborLabelFactories;
    }

    public AArtifactVisualizationLabelFactory getFirstArtifactVisualizationLabelFactory()
    {
        if (artifactLabelFactories == null)
        {
            return null;
        }
        final Optional<AArtifactVisualizationLabelFactory> min =
                Arrays.stream(artifactLabelFactories).min(Comparator.comparing(AVisualizationSequence::getSequence));
        if (min.isEmpty())
        {
            return null;
        }
        return min.get();
    }
}
