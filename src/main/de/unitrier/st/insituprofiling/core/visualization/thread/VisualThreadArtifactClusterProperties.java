package de.unitrier.st.insituprofiling.core.visualization.thread;

import com.intellij.ui.JBColor;
import de.unitrier.st.insituprofiling.core.data.ThreadArtifactCluster;

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
