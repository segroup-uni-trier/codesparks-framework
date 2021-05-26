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

    private final Object colorLock = new Object();

    void setColor(final JBColor color)
    {
        synchronized (colorLock)
        {
            this.color = color;
        }
    }

    public JBColor getOrSetColor(final JBColor color)
    {
        synchronized (colorLock)
        {
            if (this.color == null)
            {
                this.color = color;
            }
            return this.color;
        }
    }

    private final Object posLock = new Object();

    void setPosition(final int position)
    {
        synchronized (posLock)
        {
            this.position = position;
        }
    }

    public int getOrSetPosition(final int position)
    {
        synchronized (posLock)
        {
            if (this.position < 0)
            {
                this.position = position;
            }
            return this.position;
        }
    }
}
