/*
 * Copyright (c) 2022. Oliver Moseler
 */
package de.unitrier.st.codesparks.core.visualization;

import de.unitrier.st.codesparks.core.data.AArtifact;

public class DefaultArtifactVisualizer implements IArtifactVisualizer
{
    private volatile static IArtifactVisualizer instance;

    private DefaultArtifactVisualizer() {}

    public static IArtifactVisualizer getInstance()
    {
        if (instance == null)
        {
            synchronized (DefaultDataVisualizer.class)
            {
                if (instance == null)
                {
                    instance = new DefaultArtifactVisualizer();
                }
            }
        }
        return instance;
    }

    @Override
    public final AArtifactVisualization createArtifactVisualization(
            final AArtifact artifact,
            final AArtifactVisualizationLabelFactory... factories
    )
    {
        return new ArtifactVisualizationWrapper(artifact, factories);
    }
}
