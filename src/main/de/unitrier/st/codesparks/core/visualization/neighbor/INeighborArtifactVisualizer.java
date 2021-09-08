/*
 * Copyright (c) 2021. Oliver Moseler
 */
package de.unitrier.st.codesparks.core.visualization.neighbor;

import de.unitrier.st.codesparks.core.data.AArtifact;

import java.util.Collection;

public interface INeighborArtifactVisualizer
{
    Collection<ANeighborArtifactVisualization> createNeighborArtifactVisualizations(
            final AArtifact artifact
            , final ANeighborArtifactVisualizationLabelFactory... neighborFactories
    );
}
