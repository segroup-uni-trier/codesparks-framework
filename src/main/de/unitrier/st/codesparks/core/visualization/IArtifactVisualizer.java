/*
 * Copyright (C) 2020, Oliver Moseler
 */
package de.unitrier.st.codesparks.core.visualization;

import de.unitrier.st.codesparks.core.data.ACodeSparksArtifact;

/*
 * Copyright (c), Oliver Moseler, 2020
 */
public interface IArtifactVisualizer
{
    AArtifactVisualization createArtifactVisualization(ACodeSparksArtifact artifact, AArtifactVisualizationLabelFactory<ACodeSparksArtifact>... factories);
}
