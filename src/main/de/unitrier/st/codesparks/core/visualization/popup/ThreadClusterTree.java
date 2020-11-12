package de.unitrier.st.codesparks.core.visualization.popup;

import com.intellij.ui.JBColor;
import de.unitrier.st.codesparks.core.data.ACodeSparksThread;
import de.unitrier.st.codesparks.core.data.CodeSparksThreadCluster;
import de.unitrier.st.codesparks.core.visualization.thread.VisualThreadClusterProperties;
import de.unitrier.st.codesparks.core.visualization.thread.VisualThreadClusterPropertiesManager;

import java.util.List;
import java.util.Map;

public class ThreadClusterTree extends ThreadTree
{
    public ThreadClusterTree(Map<String, List<ACodeSparksThread>> threadTreeContent)
    {
        super(threadTreeContent);
        VisualThreadClusterPropertiesManager propertiesManager = VisualThreadClusterPropertiesManager.getInstance();
        for (Map.Entry<List<ACodeSparksThread>, ThreadTreeInnerNode> entry : innerNodes.entrySet())
        {
            VisualThreadClusterProperties properties = propertiesManager.getProperties((CodeSparksThreadCluster) entry.getKey());
            if (properties != null)
            {
                JBColor color = properties.getColor();
                entry.getValue().setColor(color);
            }
        }
    }
}
