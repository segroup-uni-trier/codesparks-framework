/*
 * Copyright (C) 2020, Oliver Moseler
 */
package de.unitrier.st.codesparks.core.visualization;

import de.unitrier.st.codesparks.core.data.AProfilingArtifact;

public class DefaultArtifactVisualizer implements IArtifactVisualizer
{
    private volatile static IArtifactVisualizer instance;

    private DefaultArtifactVisualizer() {}

    public static IArtifactVisualizer getInstance()
    {
        if (instance == null)
        {
            synchronized (DefaultProfilingDataVisualizer.class)
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
    public AArtifactVisualization createArtifactVisualization(AProfilingArtifact artifact, AArtifactVisualizationLabelFactory... factories)
    {
        return new ArtifactVisualizationWrapper(artifact, factories);
    }
}
