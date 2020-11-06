/*
 * Copyright (C) 2020, Oliver Moseler
 */
package de.unitrier.st.codesparks.core.visualization.callee;

import de.unitrier.st.codesparks.core.data.AProfilingArtifact;

import java.util.Collection;

public interface IArtifactCalleeVisualizer
{
    Collection<AArtifactCalleeVisualization> createArtifactCalleeVisualizations(AProfilingArtifact artifact,
                                                                                AArtifactCalleeVisualizationLabelFactory... calleeFactories);
}
