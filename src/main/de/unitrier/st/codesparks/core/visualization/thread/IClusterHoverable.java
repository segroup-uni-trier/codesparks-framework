package de.unitrier.st.codesparks.core.visualization.thread;

import de.unitrier.st.codesparks.core.data.CodeSparksThreadCluster;

public interface IClusterHoverable
{
    void onHover(CodeSparksThreadCluster cluster);
    void onExit();
}
