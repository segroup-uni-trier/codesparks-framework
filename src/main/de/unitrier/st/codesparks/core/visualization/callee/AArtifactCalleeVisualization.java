package de.unitrier.st.codesparks.core.visualization.callee;

import de.unitrier.st.codesparks.core.data.AProfilingArtifact;
import de.unitrier.st.codesparks.core.visualization.AArtifactVisualization;

public class AArtifactCalleeVisualization extends AArtifactVisualization
{
    public AArtifactCalleeVisualization(AProfilingArtifact artifact)
    {
        super(artifact);
        this.psiElement = null;
    }
}
