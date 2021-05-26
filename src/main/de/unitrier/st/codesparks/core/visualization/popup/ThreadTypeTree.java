/*
 * Copyright (c), Oliver Moseler, 2021
 */
package de.unitrier.st.codesparks.core.visualization.popup;

import com.intellij.ui.JBColor;
import de.unitrier.st.codesparks.core.data.*;
import de.unitrier.st.codesparks.core.visualization.thread.VisualThreadClusterProperties;
import de.unitrier.st.codesparks.core.visualization.thread.VisualThreadClusterPropertiesManager;

import java.util.List;
import java.util.Map;
import java.util.Optional;

public class ThreadTypeTree extends ThreadTree
{
    public ThreadTypeTree(
            final AArtifact artifact
            , final ThreadArtifactClustering threadArtifactClustering
            , final AMetricIdentifier metricIdentifier
    )
    {
        super(artifact, threadArtifactClustering, metricIdentifier);
    }

    @Override
    public void toggleCluster(final ThreadArtifactCluster cluster)
    {
        for (final AThreadArtifact threadArtifact : cluster)
        {
            for (final ThreadTreeLeafNode leafNode : leafNodes)
            {
                final AThreadArtifact leafNodeThreadArtifact = leafNode.getThreadArtifact();
                if (leafNodeThreadArtifact == threadArtifact)
                {
                    leafNode.toggleSelected();
                    break;
                }
            }
        }
        repaint();
    }

    @Override
    public void setThreadArtifactClustering(final ThreadArtifactClustering threadArtifactClustering)
    {
        super.setThreadArtifactClustering(threadArtifactClustering);
        // Do the coloring of the entries
        final VisualThreadClusterPropertiesManager propertiesManager = VisualThreadClusterPropertiesManager.getInstance(threadArtifactClustering);
        for (final ThreadTreeLeafNode leafNode : leafNodes)
        {
            final AThreadArtifact threadArtifact = leafNode.getThreadArtifact();
            final Optional<ThreadArtifactCluster> first =
                    threadArtifactClustering.parallelStream().filter(threadArtifacts -> threadArtifacts.contains(threadArtifact)).findFirst();
            if (first.isEmpty())
            {
                continue;
            }
            final VisualThreadClusterProperties properties = propertiesManager.getProperties(first.get());
            if (properties == null)
            {
                continue;
            }
            final JBColor color = properties.getOrSetColor(leafNode.getColor());
            leafNode.setColor(color);
        }
    }

    @Override
    public Map<String, List<AThreadArtifact>> transformClusteringToMap(final AArtifact artifact, final ThreadArtifactClustering threadArtifactClustering)
    {
        //noinspection UnnecessaryLocalVariable
        final Map<String, List<AThreadArtifact>> threadTypeLists = artifact.getThreadTypeListsOfThreadsWithNumericMetricValue(metricIdentifier);
        return threadTypeLists;
    }
}
