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
    private final ThreadArtifactClustering clustering;

    private static final Map<Long, VisualThreadClusterPropertiesManager> instances = new HashMap<>(8);

    public static VisualThreadClusterPropertiesManager getInstance(final ThreadArtifactClustering clustering)
    {
        synchronized (instances)
        {
            final long id = clustering.getId();
            VisualThreadClusterPropertiesManager clusterPropertiesManager = instances.get(id);
            if (clusterPropertiesManager == null)
            {
                clusterPropertiesManager = new VisualThreadClusterPropertiesManager(clustering);
                instances.put(id, clusterPropertiesManager);
            }
            return clusterPropertiesManager;
        }
    }

    private VisualThreadClusterPropertiesManager(final ThreadArtifactClustering clustering)
    {
        this.clustering = clustering;
        propertiesMap = new HashMap<>();
    }

    private final Map<Long, VisualThreadClusterProperties> propertiesMap;

    public void registerProperties(final VisualThreadClusterProperties properties)
    {
        synchronized (propertiesMap)
        {
            propertiesMap.put(properties.getCluster().getId(), properties);
        }
    }

//    public VisualThreadClusterProperties getProperties(final ThreadArtifactCluster cluster)
//    {
//        synchronized (propertiesMap)
//        {
//            return propertiesMap.get(cluster.getId());
//        }
//    }

    public void clearProperties()
    {
        synchronized (propertiesMap)
        {
            propertiesMap.clear();
        }
    }

    void buildDefaultProperties()
    {
        synchronized (propertiesMap)
        {
            int clusterNum = 0;
            for (final ThreadArtifactCluster cluster : clustering)
            {
                final JBColor color = ThreadColor.getNextColor(clusterNum);
                final VisualThreadClusterProperties clusterProperties =
                        new VisualThreadClusterPropertiesBuilder(cluster).setColor(color).setPosition(clusterNum).get();
                propertiesMap.put(cluster.getId(), clusterProperties);
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

    public VisualThreadClusterProperties getOrDefault(final ThreadArtifactCluster cluster, final int clusterNum)
    {
        synchronized (propertiesMap)
        {
            final long id = cluster.getId();
            VisualThreadClusterProperties visualThreadClusterProperties = propertiesMap.get(id);
            if (visualThreadClusterProperties == null)
            {
                final JBColor color = ThreadColor.getNextColor(clusterNum);
                visualThreadClusterProperties =
                        new VisualThreadClusterPropertiesBuilder(cluster).setColor(color).setPosition(clusterNum).get();
                propertiesMap.put(id, visualThreadClusterProperties);
            }
            return visualThreadClusterProperties;
        }
    }
}
