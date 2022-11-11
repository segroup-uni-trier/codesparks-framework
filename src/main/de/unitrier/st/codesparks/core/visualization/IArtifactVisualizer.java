/*
 * Copyright (c) 2022. Oliver Moseler
 */
package de.unitrier.st.codesparks.core.visualization;

import de.unitrier.st.codesparks.core.data.AArtifact;

public interface IArtifactVisualizer
{
    AArtifactVisualization createArtifactVisualization(
            final AArtifact artifact,
            final AArtifactVisualizationLabelFactory... factories
    );
}
