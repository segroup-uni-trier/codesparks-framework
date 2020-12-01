package de.unitrier.st.codesparks.core.visualization.thread;

import com.intellij.ui.JBColor;
import de.unitrier.st.codesparks.core.data.CodeSparksThreadCluster;

/*
 * Copyright (c), Oliver Moseler, 2020
 */
public class VisualThreadClusterProperties
{
    private final CodeSparksThreadCluster cluster;
    private final JBColor color;

    public VisualThreadClusterProperties(CodeSparksThreadCluster cluster, JBColor color)
    {
        this.cluster = cluster;
        this.color = color;
    }

    public JBColor getColor()
    {
        return color;
    }

    public CodeSparksThreadCluster getCluster()
    {
        return cluster;
    }
}
