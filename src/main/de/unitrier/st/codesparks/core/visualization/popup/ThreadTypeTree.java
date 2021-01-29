package de.unitrier.st.codesparks.core.visualization.popup;

import com.intellij.ui.JBColor;
import de.unitrier.st.codesparks.core.data.AThreadArtifact;
import de.unitrier.st.codesparks.core.data.ThreadArtifactClustering;
import de.unitrier.st.codesparks.core.data.ThreadArtifactCluster;
import de.unitrier.st.codesparks.core.data.IMetricIdentifier;
import de.unitrier.st.codesparks.core.visualization.thread.VisualThreadClusterProperties;
import de.unitrier.st.codesparks.core.visualization.thread.VisualThreadClusterPropertiesManager;

import java.util.List;
import java.util.Map;
import java.util.Optional;

/*
 * Copyright (c), Oliver Moseler, 2020
 */
public class ThreadTypeTree extends ThreadTree
{
    public ThreadTypeTree(
            final Map<String, List<AThreadArtifact>> threadTreeContent
            , final IMetricIdentifier metricIdentifier
            , final ThreadArtifactClustering clustering
    )
    {
        super(threadTreeContent, metricIdentifier);

        if (clustering == null)
        {
            return;
        }

        VisualThreadClusterPropertiesManager propertiesManager = VisualThreadClusterPropertiesManager.getInstance();

        for (ThreadTreeLeafNode leafNode : leafNodes)
        {
            final AThreadArtifact codeSparksThread = leafNode.getThreadArtifact();

            Optional<ThreadArtifactCluster> first =
                    clustering.parallelStream().filter(threadArtifacts -> threadArtifacts.contains(codeSparksThread)).findFirst();

//                if (!first.isPresent())
            if (first.isEmpty())
            {
                continue;
            }

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
    public void toggleCluster(ThreadArtifactCluster cluster)
    {
        for (AThreadArtifact codeSparksThread : cluster)
        {
            for (ThreadTreeLeafNode leafNode : leafNodes)
            {
                final AThreadArtifact nodeCodeSparksThread = leafNode.getThreadArtifact();
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
