/*
 * Copyright (C) 2020, Oliver Moseler
 */
package de.unitrier.st.codesparks.core.visualization.neighbor;

import de.unitrier.st.codesparks.core.data.AArtifact;

import java.util.Collection;

/*
 * Copyright (c), Oliver Moseler, 2020
 */
public interface INeighborArtifactVisualizer
{
    Collection<ANeighborArtifactVisualization> createArtifactCalleeVisualizations(
            final AArtifact artifact
            , final ANeighborArtifactVisualizationLabelFactory... neighborFactories
    );
}
