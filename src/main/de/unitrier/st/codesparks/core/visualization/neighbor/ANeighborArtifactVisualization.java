package de.unitrier.st.codesparks.core.visualization.neighbor;

import de.unitrier.st.codesparks.core.data.ACodeSparksArtifact;
import de.unitrier.st.codesparks.core.visualization.AArtifactVisualization;

/*
 * Copyright (c), Oliver Moseler, 2020
 */
public class ANeighborArtifactVisualization extends AArtifactVisualization
{
    public ANeighborArtifactVisualization(ACodeSparksArtifact artifact)
    {
        super(artifact);
        this.psiElement = null;
    }
}
