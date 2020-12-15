/*
 * Copyright (C) 2020, Oliver Moseler
 */
package de.unitrier.st.codesparks.core.visualization.neighbor;

import de.unitrier.st.codesparks.core.data.AArtifact;
import de.unitrier.st.codesparks.core.data.ACodeSparksArtifact;

import java.util.Collection;

/*
 * Copyright (c), Oliver Moseler, 2020
 */
public interface INeighborArtifactVisualizer
{
    Collection<ANeighborArtifactVisualization> createArtifactCalleeVisualizations(AArtifact artifact,
                                                                                  ANeighborArtifactVisualizationLabelFactory... neighborFactories);
}
