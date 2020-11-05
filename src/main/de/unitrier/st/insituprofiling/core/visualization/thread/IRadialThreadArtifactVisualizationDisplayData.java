package de.unitrier.st.insituprofiling.core.visualization.thread;

import de.unitrier.st.insituprofiling.core.data.AProfilingArtifact;
import de.unitrier.st.insituprofiling.core.data.ThreadArtifact;

import java.util.Set;

public interface IRadialThreadArtifactVisualizationDisplayData
{
    ThreadArtifactDisplayData getSelectedThreadData(AProfilingArtifact artifact, Set<ThreadArtifact> selectedThreadArtifacts);

    ThreadArtifactDisplayData getHoveredThreadData(AProfilingArtifact artifact, Set<ThreadArtifact> hoveredThreadArtifacts);
}
