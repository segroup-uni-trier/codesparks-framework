/*
 * Copyright (c) 2021. Oliver Moseler
 */
package de.unitrier.st.codesparks.core.visualization.thread;

import com.intellij.ui.JBColor;
import de.unitrier.st.codesparks.core.data.ThreadArtifactCluster;

public class VisualThreadClusterProperties
{
    private final ThreadArtifactCluster cluster;
    private JBColor color;
    private int position;

    public VisualThreadClusterProperties(final ThreadArtifactCluster cluster)
    {
        this.cluster = cluster;
        this.position = -1;
        this.color = null;
    }

    public ThreadArtifactCluster getCluster()
    {
        return cluster;
    }

    void setColor(final JBColor color)
    {
        this.color = color;
    }

    public JBColor getColor()
    {
        return this.color;
    }

    void setPosition(final int position)
    {
        this.position = position;
    }

    public int getPosition()
    {
        return this.position;
    }
}
