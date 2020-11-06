package de.unitrier.st.codesparks.core.visualization.thread;

import com.intellij.ui.JBColor;
import de.unitrier.st.codesparks.core.data.ThreadArtifactCluster;

public class VisualThreadArtifactClusterProperties
{
    private final ThreadArtifactCluster cluster;
    private final JBColor color;

    public VisualThreadArtifactClusterProperties(ThreadArtifactCluster cluster, JBColor color)
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
