/*
 * Copyright (C) 2020, Oliver Moseler
 */
package de.unitrier.st.codesparks.core.visualization;

import de.unitrier.st.codesparks.core.data.AArtifact;

public interface IArtifactVisualizer
{
    AArtifactVisualization createArtifactVisualization(AArtifact artifact, AArtifactVisualizationLabelFactory... factories);
}
