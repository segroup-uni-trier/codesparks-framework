package de.unitrier.st.insituprofiling.core.visualization.thread;

import de.unitrier.st.insituprofiling.core.data.ThreadArtifactCluster;

public interface IClusterHoverable
{
    void onHover(ThreadArtifactCluster cluster);
    void onExit();
}
