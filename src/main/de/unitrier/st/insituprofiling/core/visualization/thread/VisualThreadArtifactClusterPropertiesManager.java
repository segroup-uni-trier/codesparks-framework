package de.unitrier.st.insituprofiling.core.visualization.thread;

import de.unitrier.st.insituprofiling.core.data.ThreadArtifactCluster;

import java.util.HashMap;
import java.util.Map;

public class VisualThreadArtifactClusterPropertiesManager
{
    private VisualThreadArtifactClusterPropertiesManager()
    {
        propertiesMap = new HashMap<>();
    }

    private static volatile VisualThreadArtifactClusterPropertiesManager instance;

    public static VisualThreadArtifactClusterPropertiesManager getInstance()
    {
        if (instance == null)
        {
            synchronized (VisualThreadArtifactClusterPropertiesManager.class)
            {
                if (instance == null)
                {
                    instance = new VisualThreadArtifactClusterPropertiesManager();
                }
            }
        }
        return instance;
    }

    private final Map<Integer, VisualThreadArtifactClusterProperties> propertiesMap;

    public void registerProperties(VisualThreadArtifactClusterProperties properties)
    {
        synchronized (propertiesMap)
        {
            propertiesMap.put(properties.getCluster().getId(), properties);
        }
    }

    public VisualThreadArtifactClusterProperties getProperties(ThreadArtifactCluster cluster)
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
