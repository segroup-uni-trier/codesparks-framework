package de.unitrier.st.codesparks.core.visualization.thread;

import com.intellij.ui.JBColor;
import de.unitrier.st.codesparks.core.data.ThreadArtifactCluster;

/*
 * Copyright (c), Oliver Moseler, 2020
 */
public class VisualThreadClusterProperties
{
    private final ThreadArtifactCluster cluster;
    private final JBColor color;

    public VisualThreadClusterProperties(ThreadArtifactCluster cluster, JBColor color)
    {
        this.cluster = cluster;
        this.color = color;
    }

    public JBColor getColor()
    {
        return color;
    }

    public ThreadArtifactCluster getCluster()
    {
        return cluster;
    }
}
