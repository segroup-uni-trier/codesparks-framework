package de.unitrier.st.codesparks.core.visualization;

import de.unitrier.st.codesparks.core.data.AArtifact;

/*
 * Copyright (c), Oliver Moseler, 2020
 */
public interface IArtifactVisualizer
{
    AArtifactVisualization createArtifactVisualization(
            final AArtifact artifact
            , final AArtifactVisualizationLabelFactory... factories
    );
}
