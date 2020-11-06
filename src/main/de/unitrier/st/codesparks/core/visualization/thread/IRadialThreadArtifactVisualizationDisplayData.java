package de.unitrier.st.codesparks.core.visualization.thread;

import de.unitrier.st.codesparks.core.data.AProfilingArtifact;
import de.unitrier.st.codesparks.core.data.ThreadArtifact;

import java.util.Set;

public interface IRadialThreadArtifactVisualizationDisplayData
{
    ThreadArtifactDisplayData getSelectedThreadData(AProfilingArtifact artifact, Set<ThreadArtifact> selectedThreadArtifacts);

    ThreadArtifactDisplayData getHoveredThreadData(AProfilingArtifact artifact, Set<ThreadArtifact> hoveredThreadArtifacts);
}
