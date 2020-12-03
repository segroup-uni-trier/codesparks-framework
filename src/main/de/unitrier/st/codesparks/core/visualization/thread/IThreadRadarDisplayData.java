package de.unitrier.st.codesparks.core.visualization.thread;

import de.unitrier.st.codesparks.core.data.ASourceCodeArtifact;
import de.unitrier.st.codesparks.core.data.AThreadArtifact;

import java.util.Set;

/*
 * Copyright (c), Oliver Moseler, 2020
 */
public interface IThreadRadarDisplayData
{
    CodeSparksThreadDisplayData getSelectedThreadData(ASourceCodeArtifact artifact, Set<AThreadArtifact> selectedCodeSparksThreads);

    CodeSparksThreadDisplayData getHoveredThreadData(ASourceCodeArtifact artifact, Set<AThreadArtifact> hoveredCodeSparksThreads);
}
