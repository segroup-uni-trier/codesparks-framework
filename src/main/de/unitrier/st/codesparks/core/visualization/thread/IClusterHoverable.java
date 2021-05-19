/*
 * Copyright (c) 2021. Oliver Moseler
 */
package de.unitrier.st.codesparks.core.visualization.thread;

import de.unitrier.st.codesparks.core.data.ThreadArtifactCluster;

public interface IClusterHoverable
{
    void onHover(ThreadArtifactCluster cluster);

    void onExit();
}
