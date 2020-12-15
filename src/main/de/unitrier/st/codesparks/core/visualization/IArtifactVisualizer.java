package de.unitrier.st.codesparks.core.visualization;

import de.unitrier.st.codesparks.core.data.AArtifact;

/*
 * Copyright (c), Oliver Moseler, 2020
 */
public interface IArtifactVisualizer
{
    @SuppressWarnings("unchecked")
    AArtifactVisualization createArtifactVisualization(AArtifact artifact, AArtifactVisualizationLabelFactory<AArtifact>... factories);
}
