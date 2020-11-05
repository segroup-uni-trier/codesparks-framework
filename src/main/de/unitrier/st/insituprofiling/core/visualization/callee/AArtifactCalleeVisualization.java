package de.unitrier.st.insituprofiling.core.visualization.callee;

import de.unitrier.st.insituprofiling.core.data.AProfilingArtifact;
import de.unitrier.st.insituprofiling.core.visualization.AArtifactVisualization;

public class AArtifactCalleeVisualization extends AArtifactVisualization
{
    public AArtifactCalleeVisualization(AProfilingArtifact artifact)
    {
        super(artifact);
        this.psiElement = null;
    }
}
