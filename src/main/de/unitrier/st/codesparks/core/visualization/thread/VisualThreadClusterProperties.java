package de.unitrier.st.codesparks.core.visualization.thread;

import com.intellij.ui.JBColor;
import de.unitrier.st.codesparks.core.data.ThreadArtifactCluster;

/*
 * Copyright (c), Oliver Moseler, 2020
 */
public class VisualThreadClusterProperties
{
    private final ThreadArtifactCluster cluster;
    private JBColor color;
    private int position;

    public VisualThreadClusterProperties(final ThreadArtifactCluster cluster)
    {
//        this(cluster, null, -1);
        this.cluster = cluster;
        this.position = -1;
        this.color = null;
    }

//    public VisualThreadClusterProperties(final ThreadArtifactCluster cluster,  final JBColor color)
//    {
//        this(cluster, color, -1);
//    }
//
//    public VisualThreadClusterProperties(final ThreadArtifactCluster cluster, final JBColor color, final int position)
//    {
//        this.cluster = cluster;
//        this.color = color;
//        this.position = position;
//    }

    public JBColor getColor()
    {
        return color;
    }

    public ThreadArtifactCluster getCluster()
    {
        return cluster;
    }

    void setColor(final JBColor color)
    {
        this.color = color;
    }

    void setPosition(final int position)
    {
        this.position = position;
    }

    public int getPosition()
    {
        return position;
    }
}
