package de.unitrier.st.codesparks.core.visualization.thread;

import de.unitrier.st.codesparks.core.data.AArtifact;
import de.unitrier.st.codesparks.core.data.AThreadArtifact;

import java.util.Set;

/*
 * Copyright (c), Oliver Moseler, 2020
 */
public interface IThreadRadarDisplayData
{
    CodeSparksThreadDisplayData getSelectedThreadData(AArtifact artifact, Set<AThreadArtifact> selectedCodeSparksThreads);

    CodeSparksThreadDisplayData getHoveredThreadData(AArtifact artifact, Set<AThreadArtifact> hoveredCodeSparksThreads);
}
