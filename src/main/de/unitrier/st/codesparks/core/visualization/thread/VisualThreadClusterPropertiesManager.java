package de.unitrier.st.codesparks.core.visualization.thread;

import de.unitrier.st.codesparks.core.data.CodeSparksThreadCluster;

import java.util.HashMap;
import java.util.Map;

/*
 * Copyright (c), Oliver Moseler, 2020
 */
public class VisualThreadClusterPropertiesManager
{
    private VisualThreadClusterPropertiesManager()
    {
        propertiesMap = new HashMap<>();
    }

    private static volatile VisualThreadClusterPropertiesManager instance;

    public static VisualThreadClusterPropertiesManager getInstance()
    {
        if (instance == null)
        {
            synchronized (VisualThreadClusterPropertiesManager.class)
            {
                if (instance == null)
                {
                    instance = new VisualThreadClusterPropertiesManager();
                }
            }
        }
        return instance;
    }

    private final Map<Integer, VisualThreadClusterProperties> propertiesMap;

    public void registerProperties(VisualThreadClusterProperties properties)
    {
        synchronized (propertiesMap)
        {
            propertiesMap.put(properties.getCluster().getId(), properties);
        }
    }

    public VisualThreadClusterProperties getProperties(CodeSparksThreadCluster cluster)
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

}
