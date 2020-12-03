/*
 * Copyright (C) 2020, Oliver Moseler
 */
package de.unitrier.st.codesparks.core.visualization;

import de.unitrier.st.codesparks.core.data.ACodeSparksArtifact;

/*
 * Copyright (c), Oliver Moseler, 2020
 */
public class DefaultArtifactVisualizer implements IArtifactVisualizer<ACodeSparksArtifact>
{
    private volatile static IArtifactVisualizer<ACodeSparksArtifact> instance;

    private DefaultArtifactVisualizer() {}

    public static IArtifactVisualizer<ACodeSparksArtifact> getInstance()
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
    public AArtifactVisualization createArtifactVisualization(ACodeSparksArtifact artifact, AArtifactVisualizationLabelFactory... factories)
    {
        return new ArtifactVisualizationWrapper(artifact, factories);
    }
}
