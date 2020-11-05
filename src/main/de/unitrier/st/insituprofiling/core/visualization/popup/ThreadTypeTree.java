package de.unitrier.st.insituprofiling.core.visualization.popup;

import com.intellij.ui.JBColor;
import de.unitrier.st.insituprofiling.core.data.ThreadArtifact;
import de.unitrier.st.insituprofiling.core.data.ThreadArtifactCluster;
import de.unitrier.st.insituprofiling.core.data.ThreadArtifactClustering;
import de.unitrier.st.insituprofiling.core.visualization.thread.VisualThreadArtifactClusterProperties;
import de.unitrier.st.insituprofiling.core.visualization.thread.VisualThreadArtifactClusterPropertiesManager;

import java.util.List;
import java.util.Map;
import java.util.Optional;

public class ThreadTypeTree extends ThreadTree
{
    public ThreadTypeTree(Map<String, List<ThreadArtifact>> threadTreeContent, ThreadArtifactClustering clustering)
    {
        super(threadTreeContent);

        if (clustering == null)
        {
            return;
        }

        VisualThreadArtifactClusterPropertiesManager propertiesManager = VisualThreadArtifactClusterPropertiesManager.getInstance();

        for (ThreadTreeLeafNode leafNode : leafNodes)
        {
            final ThreadArtifact threadArtifact = leafNode.getThreadArtifact();

            Optional<ThreadArtifactCluster> first =
                    clustering.parallelStream().filter(threadArtifacts -> threadArtifacts.contains(threadArtifact)).findFirst();

            if (!first.isPresent()) continue;

            VisualThreadArtifactClusterProperties properties = propertiesManager.getProperties(first.get());

            if (properties == null) continue;

            JBColor color = properties.getColor();
            leafNode.setColor(color);

//            for (ThreadArtifactCluster cluster : clustering)
//            {
//                if (!cluster.contains(threadArtifact)) continue;
//                VisualThreadArtifactClusterProperties properties = propertiesManager.getProperties(cluster);
//                if(properties == null) continue;
//                JBColor color = properties.getColor();
//                leafNode.setColor(color);
//                if (properties != null)
//                {
//                    JBColor color = properties.getColor();
//                    leafNode.setColor(color);
//                }
//                if (cluster.contains(threadArtifact))
//                {
//                    VisualThreadArtifactClusterProperties properties = propertiesManager.getProperties(cluster);
//                    if (properties != null)
//                    {
//                        JBColor color = properties.getColor();
//                        leafNode.setColor(color);
//                    }
//                }
//            }
        }
    }

    @Override
    public void toggleCluster(ThreadArtifactCluster cluster)
    {
        for (ThreadArtifact threadArtifact : cluster)
        {
            for (ThreadTreeLeafNode leafNode : leafNodes)
            {
                final ThreadArtifact nodeThreadArtifact = leafNode.getThreadArtifact();
                if (nodeThreadArtifact == threadArtifact)
                {
                    leafNode.toggleSelected();
                    break;
                }
            }
        }
        repaint();
    }
}
