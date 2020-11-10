package de.unitrier.st.codesparks.core.visualization.callee;

import de.unitrier.st.codesparks.core.data.AArtifact;
import de.unitrier.st.codesparks.core.visualization.AArtifactVisualization;

public class AArtifactCalleeVisualization extends AArtifactVisualization
{
    public AArtifactCalleeVisualization(AArtifact artifact)
    {
        super(artifact);
        this.psiElement = null;
    }
}
