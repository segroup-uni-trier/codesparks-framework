/*
 * Copyright (C) 2020, Oliver Moseler
 */
package de.unitrier.st.codesparks.core.visualization.neighbor;

import de.unitrier.st.codesparks.core.data.AArtifact;

import java.util.Collection;

public interface INeighborArtifactVisualizer
{
    Collection<ANeighborArtifactVisualization> createArtifactCalleeVisualizations(AArtifact artifact,
                                                                                  ANeighborArtifactVisualizationLabelFactory... calleeFactories);
}
