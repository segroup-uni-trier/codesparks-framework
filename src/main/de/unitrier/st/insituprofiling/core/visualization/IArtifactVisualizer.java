/*
 * Copyright (C) 2020, Oliver Moseler
 */
package de.unitrier.st.insituprofiling.core.visualization;

import de.unitrier.st.insituprofiling.core.data.AProfilingArtifact;

public interface IArtifactVisualizer
{
    AArtifactVisualization createArtifactVisualization(AProfilingArtifact artifact, AArtifactVisualizationLabelFactory... factories);
}
