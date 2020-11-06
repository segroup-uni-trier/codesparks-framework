package de.unitrier.st.codesparks.core.visualization.callee;

import de.unitrier.st.codesparks.core.visualization.AVisualizationSequence;

public abstract class AArtifactCalleeVisualizationLabelFactory extends AVisualizationSequence
        implements IArtifactCalleeVisualizationLabelFactory
{
    protected AArtifactCalleeVisualizationLabelFactory() {}

    protected AArtifactCalleeVisualizationLabelFactory(int sequence) {super(sequence, false);}
}