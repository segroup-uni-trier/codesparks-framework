/*
 * Copyright (C) 2020, Oliver Moseler
 */
package de.unitrier.st.insituprofiling.core.visualization.callee;

import de.unitrier.st.insituprofiling.core.data.AProfilingArtifact;

import java.util.Collection;

public interface IArtifactCalleeVisualizer
{
    Collection<AArtifactCalleeVisualization> createArtifactCalleeVisualizations(AProfilingArtifact artifact,
                                                                                AArtifactCalleeVisualizationLabelFactory... calleeFactories);
}
