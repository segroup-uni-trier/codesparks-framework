/*
 * Copyright (c) 2021. Oliver Moseler
 */
package de.unitrier.st.codesparks.core.visualization;

import de.unitrier.st.codesparks.core.IDataVisualizer;
import de.unitrier.st.codesparks.core.visualization.neighbor.ANeighborArtifactVisualizationLabelFactory;
import de.unitrier.st.codesparks.core.visualization.neighbor.INeighborArtifactVisualizer;

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
}
