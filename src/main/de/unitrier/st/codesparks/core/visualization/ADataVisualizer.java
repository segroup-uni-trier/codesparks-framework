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
    protected AArtifactVisualizationLabelFactory[] artifactLabelFactories;
    protected final INeighborArtifactVisualizer neighborArtifactVisualizer;
    protected ANeighborArtifactVisualizationLabelFactory[] neighborLabelFactories;

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
}
