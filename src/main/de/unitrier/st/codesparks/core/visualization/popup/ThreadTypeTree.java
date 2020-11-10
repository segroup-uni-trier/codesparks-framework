package de.unitrier.st.codesparks.core.visualization.popup;

import com.intellij.ui.JBColor;
import de.unitrier.st.codesparks.core.data.CodeSparksThread;
import de.unitrier.st.codesparks.core.data.CodeSparksThreadClustering;
import de.unitrier.st.codesparks.core.data.CodeSparksThreadCluster;
import de.unitrier.st.codesparks.core.visualization.thread.VisualThreadClusterProperties;
import de.unitrier.st.codesparks.core.visualization.thread.VisualThreadClusterPropertiesManager;

import java.util.List;
import java.util.Map;
import java.util.Optional;

public class ThreadTypeTree extends ThreadTree
{
    public ThreadTypeTree(Map<String, List<CodeSparksThread>> threadTreeContent, CodeSparksThreadClustering clustering)
    {
        super(threadTreeContent);

        if (clustering == null)
        {
            return;
        }

        VisualThreadClusterPropertiesManager propertiesManager = VisualThreadClusterPropertiesManager.getInstance();

        for (ThreadTreeLeafNode leafNode : leafNodes)
        {
            final CodeSparksThread codeSparksThread = leafNode.getThreadArtifact();

            Optional<CodeSparksThreadCluster> first =
                    clustering.parallelStream().filter(threadArtifacts -> threadArtifacts.contains(codeSparksThread)).findFirst();

            if (!first.isPresent()) continue;

            VisualThreadClusterProperties properties = propertiesManager.getProperties(first.get());

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
    public void toggleCluster(CodeSparksThreadCluster cluster)
    {
        for (CodeSparksThread codeSparksThread : cluster)
        {
            for (ThreadTreeLeafNode leafNode : leafNodes)
            {
                final CodeSparksThread nodeCodeSparksThread = leafNode.getThreadArtifact();
                if (nodeCodeSparksThread == codeSparksThread)
                {
                    leafNode.toggleSelected();
                    break;
                }
            }
        }
        repaint();
    }
}
