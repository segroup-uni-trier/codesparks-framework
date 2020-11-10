package de.unitrier.st.codesparks.core.visualization.thread;

import com.intellij.ui.JBColor;
import de.unitrier.st.codesparks.core.data.CodeSparksThreadCluster;

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
