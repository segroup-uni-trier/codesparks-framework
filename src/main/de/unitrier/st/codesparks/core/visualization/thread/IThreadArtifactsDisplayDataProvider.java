/*
 * Copyright (c), Oliver Moseler, 2021
 */
package de.unitrier.st.codesparks.core.visualization.thread;

import de.unitrier.st.codesparks.core.data.AArtifact;
import de.unitrier.st.codesparks.core.data.AThreadArtifact;

import java.util.Set;

public interface IThreadArtifactsDisplayDataProvider
{
    ThreadArtifactDisplayData getDisplayDataOfSelectedThreads(final AArtifact artifact, final Set<AThreadArtifact> selectedThreadArtifacts);

    ThreadArtifactDisplayData getDisplayDataOfHoveredThreads(final AArtifact artifact, final Set<AThreadArtifact> hoveredThreadArtifacts);
}
