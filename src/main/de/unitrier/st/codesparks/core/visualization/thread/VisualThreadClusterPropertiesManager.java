/*
 * Copyright (c) 2021. Oliver Moseler
 */
package de.unitrier.st.codesparks.core.visualization.thread;

import com.intellij.ui.JBColor;
import de.unitrier.st.codesparks.core.data.ThreadArtifactCluster;
import de.unitrier.st.codesparks.core.data.ThreadArtifactClustering;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

public class VisualThreadClusterPropertiesManager implements Serializable
{
    private static final Map<Long, VisualThreadClusterPropertiesManager> instances = new HashMap<>(1 << 5);

    public static VisualThreadClusterPropertiesManager getInstance(final ThreadArtifactClustering clustering)
    {
        VisualThreadClusterPropertiesManager clusterPropertiesManager;
        final long id = clustering.getId();
        synchronized (instances)
        {
            clusterPropertiesManager = instances.get(id);
            if (clusterPropertiesManager == null)
            {
                clusterPropertiesManager = new VisualThreadClusterPropertiesManager(clustering);
                instances.put(id, clusterPropertiesManager);
            }
        }
        return clusterPropertiesManager;
    }

    private final ThreadArtifactClustering clustering;

    private VisualThreadClusterPropertiesManager(final ThreadArtifactClustering clustering)
    {
        this.clustering = clustering;
        propertiesMap = new HashMap<>(clustering.size());
    }

    private final Map<Long, VisualThreadClusterProperties> propertiesMap;

    public void registerProperties(final VisualThreadClusterProperties properties)
    {
        synchronized (propertiesMap)
        {
            propertiesMap.put(properties.getCluster().getId(), properties);
        }
    }

    @SuppressWarnings("unused")
    public void clearProperties()
    {
        synchronized (propertiesMap)
        {
            propertiesMap.clear();
        }
    }

    void buildDefaultProperties()
    {
        int clusterNum = 0;
        synchronized (propertiesMap)
        {
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

//    public static void clearInstances()
//    {
//        synchronized (instances)
//        {
//            for (final VisualThreadClusterPropertiesManager value : instances.values())
//            {
//                value.clearProperties();
//            }
//            instances.clear();
//        }
//    }

    public VisualThreadClusterProperties getOrDefault(final ThreadArtifactCluster cluster, final int clusterNum)
    {
        VisualThreadClusterProperties visualThreadClusterProperties;
        final long id = cluster.getId();
        synchronized (propertiesMap)
        {
            visualThreadClusterProperties = propertiesMap.get(id);
            if (visualThreadClusterProperties == null)
            {
                final JBColor color = ThreadColor.getNextColor(clusterNum);
                visualThreadClusterProperties =
                        new VisualThreadClusterPropertiesBuilder(cluster).setColor(color).setPosition(clusterNum).get();
                propertiesMap.put(id, visualThreadClusterProperties);
            }
        }
        return visualThreadClusterProperties;
    }
}
