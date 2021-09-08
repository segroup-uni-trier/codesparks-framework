/*
 * Copyright (c) 2021. Oliver Moseler
 */
package de.unitrier.st.codesparks.core.visualization.neighbor;

import de.unitrier.st.codesparks.core.data.AArtifact;
import de.unitrier.st.codesparks.core.visualization.AArtifactVisualization;

public class ANeighborArtifactVisualization extends AArtifactVisualization
{
    public ANeighborArtifactVisualization(final AArtifact artifact)
    {
        super(artifact);
        this.psiElement = null;
    }
}
