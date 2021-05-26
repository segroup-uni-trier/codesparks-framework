/*
 * Copyright (c), Oliver Moseler, 2021
 */
package de.unitrier.st.codesparks.core.visualization.popup;

import com.intellij.ui.JBColor;
import de.unitrier.st.codesparks.core.data.*;
import de.unitrier.st.codesparks.core.visualization.thread.VisualThreadClusterProperties;
import de.unitrier.st.codesparks.core.visualization.thread.VisualThreadClusterPropertiesManager;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ThreadClusterTree extends ThreadTree
{
    public ThreadClusterTree(
            final ThreadArtifactClustering threadArtifactClustering
            , final AMetricIdentifier metricIdentifier)
    {
        super(null, threadArtifactClustering, metricIdentifier);
    }

    @Override
    public void setThreadArtifactClustering(final ThreadArtifactClustering threadArtifactClustering)
    {
        super.setThreadArtifactClustering(threadArtifactClustering);
        // Do the coloring of the entries
        final VisualThreadClusterPropertiesManager propertiesManager = VisualThreadClusterPropertiesManager.getInstance(threadArtifactClustering);
        for (final Map.Entry<List<AThreadArtifact>, ThreadTreeInnerNode> entry : innerNodes.entrySet())
        {
            final ThreadArtifactCluster cluster = (ThreadArtifactCluster) entry.getKey();
            final VisualThreadClusterProperties properties = propertiesManager.getProperties(cluster);
            if (properties != null)
            {
                final JBColor color = properties.getOrSetColor(null);
                if (color != null)
                {
                    entry.getValue().setColor(color);
                }
            }
        }
    }

    @Override
    public Map<String, List<AThreadArtifact>> transformClusteringToMap(final AArtifact artifact, final ThreadArtifactClustering threadArtifactClustering)
    {
        final Map<String, List<AThreadArtifact>> map = new HashMap<>(threadArtifactClustering.size());
        int clusterId = 1;
        for (final ThreadArtifactCluster threadArtifacts : threadArtifactClustering)
        {
            map.put("Cluster:" + clusterId++, threadArtifacts);
        }
        return map;
    }
}
