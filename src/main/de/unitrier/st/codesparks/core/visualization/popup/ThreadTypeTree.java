/*
 * Copyright (c), Oliver Moseler, 2021
 */
package de.unitrier.st.codesparks.core.visualization.popup;

import com.intellij.ui.JBColor;
import de.unitrier.st.codesparks.core.data.AThreadArtifact;
import de.unitrier.st.codesparks.core.data.ThreadArtifactClustering;
import de.unitrier.st.codesparks.core.data.ThreadArtifactCluster;
import de.unitrier.st.codesparks.core.data.AMetricIdentifier;
import de.unitrier.st.codesparks.core.visualization.thread.VisualThreadClusterProperties;
import de.unitrier.st.codesparks.core.visualization.thread.VisualThreadClusterPropertiesManager;

import java.util.List;
import java.util.Map;
import java.util.Optional;

public class ThreadTypeTree extends ThreadTree
{
    public ThreadTypeTree(
            final Map<String, List<AThreadArtifact>> threadTreeContent
            , final AMetricIdentifier metricIdentifier
            , final ThreadArtifactClustering clustering
    )
    {
        super(threadTreeContent, metricIdentifier);
        if (clustering == null)
        {
            return;
        }
        final VisualThreadClusterPropertiesManager propertiesManager = VisualThreadClusterPropertiesManager.getInstance();
        for (final ThreadTreeLeafNode leafNode : leafNodes)
        {
            final AThreadArtifact threadArtifact = leafNode.getThreadArtifact();
            final Optional<ThreadArtifactCluster> first =
                    clustering.parallelStream().filter(threadArtifacts -> threadArtifacts.contains(threadArtifact)).findFirst();
            if (first.isEmpty())
            {
                continue;
            }
            final VisualThreadClusterProperties properties = propertiesManager.getProperties(first.get());
            if (properties == null)
            {
                continue;
            }
            final JBColor color = properties.getColor();
            leafNode.setColor(color);
        }
    }

    @Override
    public void toggleCluster(final ThreadArtifactCluster cluster)
    {
        for (final AThreadArtifact threadArtifact : cluster)
        {
            for (final ThreadTreeLeafNode leafNode : leafNodes)
            {
                final AThreadArtifact nodeCodeSparksThread = leafNode.getThreadArtifact();
                if (nodeCodeSparksThread == threadArtifact)
                {
                    leafNode.toggleSelected();
                    break;
                }
            }
        }
        repaint();
    }
}
