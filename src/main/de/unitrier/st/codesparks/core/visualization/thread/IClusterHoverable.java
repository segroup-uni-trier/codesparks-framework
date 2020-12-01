package de.unitrier.st.codesparks.core.visualization.thread;

import de.unitrier.st.codesparks.core.data.CodeSparksThreadCluster;

/*
 * Copyright (c), Oliver Moseler, 2020
 */
public interface IClusterHoverable
{
    void onHover(CodeSparksThreadCluster cluster);
    void onExit();
}
