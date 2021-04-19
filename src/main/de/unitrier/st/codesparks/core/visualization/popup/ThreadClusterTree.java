/*
 * Copyright (c), Oliver Moseler, 2021
 */
package de.unitrier.st.codesparks.core.visualization.popup;

import com.intellij.ui.JBColor;
import de.unitrier.st.codesparks.core.data.AThreadArtifact;
import de.unitrier.st.codesparks.core.data.ThreadArtifactCluster;
import de.unitrier.st.codesparks.core.data.AMetricIdentifier;
import de.unitrier.st.codesparks.core.visualization.thread.VisualThreadClusterProperties;
import de.unitrier.st.codesparks.core.visualization.thread.VisualThreadClusterPropertiesManager;

import java.util.List;
import java.util.Map;

public class ThreadClusterTree extends ThreadTree
{
    public ThreadClusterTree(final Map<String, List<AThreadArtifact>> threadTreeContent, final AMetricIdentifier metricIdentifier)
    {
        super(threadTreeContent, metricIdentifier);
        final VisualThreadClusterPropertiesManager propertiesManager = VisualThreadClusterPropertiesManager.getInstance();
        for (final Map.Entry<List<AThreadArtifact>, ThreadTreeInnerNode> entry : innerNodes.entrySet())
        {
            final VisualThreadClusterProperties properties = propertiesManager.getProperties((ThreadArtifactCluster) entry.getKey());
            if (properties != null)
            {
                final JBColor color = properties.getColor();
                entry.getValue().setColor(color);
            }
        }
    }
}
