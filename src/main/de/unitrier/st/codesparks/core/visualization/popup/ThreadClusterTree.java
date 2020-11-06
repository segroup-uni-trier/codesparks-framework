package de.unitrier.st.codesparks.core.visualization.popup;

import com.intellij.ui.JBColor;
import de.unitrier.st.codesparks.core.data.ThreadArtifact;
import de.unitrier.st.codesparks.core.data.ThreadArtifactCluster;
import de.unitrier.st.codesparks.core.visualization.thread.VisualThreadArtifactClusterProperties;
import de.unitrier.st.codesparks.core.visualization.thread.VisualThreadArtifactClusterPropertiesManager;

import java.util.List;
import java.util.Map;

public class ThreadClusterTree extends ThreadTree
{
    public ThreadClusterTree(Map<String, List<ThreadArtifact>> threadTreeContent)
    {
        super(threadTreeContent);
        VisualThreadArtifactClusterPropertiesManager propertiesManager = VisualThreadArtifactClusterPropertiesManager.getInstance();
        for (Map.Entry<List<ThreadArtifact>, ThreadTreeInnerNode> entry : innerNodes.entrySet())
        {
            VisualThreadArtifactClusterProperties properties = propertiesManager.getProperties((ThreadArtifactCluster) entry.getKey());
            if (properties != null)
            {
                JBColor color = properties.getColor();
                entry.getValue().setColor(color);
            }
        }
    }
}
