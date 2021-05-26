/*
 * Copyright (c) 2021. Oliver Moseler
 */
package de.unitrier.st.codesparks.core.visualization.thread;

import com.intellij.ui.JBColor;
import de.unitrier.st.codesparks.core.data.ThreadArtifactCluster;
import de.unitrier.st.codesparks.core.data.ThreadArtifactClustering;
import de.unitrier.st.codesparks.core.visualization.popup.ThreadColor;

import java.util.HashMap;
import java.util.Map;

public class VisualThreadClusterPropertiesManager
{
    private static final Map<ThreadArtifactClustering, VisualThreadClusterPropertiesManager> instances = new HashMap<>(8);

    public static VisualThreadClusterPropertiesManager getInstance(final ThreadArtifactClustering clustering)
    {
        synchronized (instances)
        {
            VisualThreadClusterPropertiesManager clusterPropertiesManager = instances.get(clustering);
            if (clusterPropertiesManager == null)
            {
                clusterPropertiesManager = new VisualThreadClusterPropertiesManager(clustering);
                instances.put(clustering, clusterPropertiesManager);
            }
            return clusterPropertiesManager;
        }
    }

    private final ThreadArtifactClustering clustering;
    private final Map<Integer, VisualThreadClusterProperties> propertiesMap;

    private VisualThreadClusterPropertiesManager(final ThreadArtifactClustering clustering)
    {
        propertiesMap = new HashMap<>();
        this.clustering = clustering;
    }

    public void registerProperties(VisualThreadClusterProperties properties)
    {
        synchronized (propertiesMap)
        {
            propertiesMap.put(properties.getCluster().getId(), properties);
        }
    }

    public VisualThreadClusterProperties getProperties(ThreadArtifactCluster cluster)
    {
        synchronized (propertiesMap)
        {
            return propertiesMap.get(cluster.getId());
        }
    }

    public void clearProperties()
    {
        synchronized (propertiesMap)
        {
            propertiesMap.clear();
        }
    }

    void buildProperties()
    {
        synchronized (propertiesMap)
        {
            int clusterNum = 0;
            for (final ThreadArtifactCluster cluster : clustering)
            {
                VisualThreadClusterProperties properties = propertiesMap.get(cluster.getId());
                if (properties == null)
                {
                    final JBColor color = ThreadColor.getNextColor(clusterNum);
                    final VisualThreadClusterProperties clusterProperties =
                            new VisualThreadClusterPropertiesBuilder(cluster).setColor(color).setPosition(clusterNum).get();
                    propertiesMap.put(cluster.getId(), clusterProperties);
                }
                clusterNum += 1;
            }
        }
    }

    public static void clearInstances()
    {
        synchronized (instances)
        {
            for (final VisualThreadClusterPropertiesManager value : instances.values())
            {
                value.clearProperties();
            }
            instances.clear();
        }
    }
}
