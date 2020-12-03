/*
 * Copyright (C) 2020, Oliver Moseler
 */
package de.unitrier.st.codesparks.core.visualization;

import de.unitrier.st.codesparks.core.data.AArtifact;

/*
 * Copyright (c), Oliver Moseler, 2020
 */
public interface IArtifactVisualizer<T extends AArtifact>
{
    AArtifactVisualization createArtifactVisualization(T artifact, AArtifactVisualizationLabelFactory... factories);
}
