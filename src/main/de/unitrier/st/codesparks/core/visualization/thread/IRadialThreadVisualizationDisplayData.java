package de.unitrier.st.codesparks.core.visualization.thread;

import de.unitrier.st.codesparks.core.data.AArtifact;
import de.unitrier.st.codesparks.core.data.CodeSparksThread;

import java.util.Set;

public interface IRadialThreadVisualizationDisplayData
{
    CodeSparksThreadDisplayData getSelectedThreadData(AArtifact artifact, Set<CodeSparksThread> selectedCodeSparksThreads);

    CodeSparksThreadDisplayData getHoveredThreadData(AArtifact artifact, Set<CodeSparksThread> hoveredCodeSparksThreads);
}
