/*
 * Copyright (C) 2020, Oliver Moseler
 */
package de.unitrier.st.codesparks.core.visualization;

import de.unitrier.st.codesparks.core.data.AProfilingArtifact;

public interface IArtifactVisualizer
{
    AArtifactVisualization createArtifactVisualization(AProfilingArtifact artifact, AArtifactVisualizationLabelFactory... factories);
}
