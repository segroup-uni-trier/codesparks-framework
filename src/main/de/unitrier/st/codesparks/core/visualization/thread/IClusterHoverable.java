package de.unitrier.st.codesparks.core.visualization.thread;

import de.unitrier.st.codesparks.core.data.ThreadArtifactCluster;

/*
 * Copyright (c), Oliver Moseler, 2020
 */
public interface IClusterHoverable
{
    void onHover(ThreadArtifactCluster cluster);
    void onExit();
}
