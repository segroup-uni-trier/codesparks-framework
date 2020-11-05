package de.unitrier.st.insituprofiling.core.visualization;

import de.unitrier.st.insituprofiling.core.IProfilingResult;
import de.unitrier.st.insituprofiling.core.ProfilingResultManager;
import de.unitrier.st.insituprofiling.core.data.AProfilingArtifact;
import de.unitrier.st.insituprofiling.core.logging.ProfilingLogger;

import javax.swing.*;
import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;
import java.util.Map;

public class ArtifactVisualizationLabelFactoryCache
{
    private ArtifactVisualizationLabelFactoryCache()
    {
        cache = new HashMap<>();
    }

    private static ArtifactVisualizationLabelFactoryCache instance;

    public static ArtifactVisualizationLabelFactoryCache getInstance()
    {
        if (instance == null)
        {
            synchronized (ArtifactVisualizationLabelFactoryCache.class)
            {
                if (instance == null)
                {
                    instance = new ArtifactVisualizationLabelFactoryCache();
                }
            }
        }
        return instance;
    }

    // Maps an artifact id on the factory id on its respective label
    private final Map<String, Map<Class<? extends AArtifactVisualizationLabelFactory>, JLabel>> cache;

    public void clearCache()
    {
        synchronized (this)
        {
            cache.values().forEach(Map::clear);
            cache.clear();
        }
    }

    public void addToCache(String identifier, Class<? extends AArtifactVisualizationLabelFactory> factory,
                                        JLabel artifactVisualizationLabel)
    {
        JLabel jLabel = new JLabel(artifactVisualizationLabel.getIcon());
        synchronized (this)
        {
            Map<Class<? extends AArtifactVisualizationLabelFactory>, JLabel> labelMap = cache.getOrDefault(identifier, new HashMap<>());
            labelMap.put(factory, jLabel);
            cache.put(identifier, labelMap);
        }
    }

    public JLabel getCachedArtifactVisualizationLabel(String identifier,
                                                                   Class<? extends AArtifactVisualizationLabelFactory> factory,
                                                                   boolean createIfAbsent)
    {
        synchronized (this)
        {
            Map<Class<? extends AArtifactVisualizationLabelFactory>, JLabel> classJLabelMap = cache.get(identifier);
            if (classJLabelMap == null)
            {
                if (createIfAbsent)
                {
                    classJLabelMap = new HashMap<>();
                } else
                {
                    return null;
                }
            }
            JLabel jLabel = classJLabelMap.get(factory);
            if (jLabel == null)
            {
                if (createIfAbsent)
                {
                    try
                    {
                        AArtifactVisualizationLabelFactory labelFactory = factory.getDeclaredConstructor().newInstance();
                        IProfilingResult profilingResult = ProfilingResultManager.getInstance().getProfilingResult();
                        AProfilingArtifact artifact = profilingResult.getArtifact(identifier);
                        if (artifact == null)
                        {
                            return null;
                        }
                        JLabel artifactLabel = labelFactory.createArtifactLabel(artifact);
                        artifactLabel.setHorizontalAlignment(SwingConstants.CENTER);
                        classJLabelMap.put(factory, artifactLabel);
                        cache.put(identifier, classJLabelMap);
                        return artifactLabel;
                    } catch (InstantiationException | IllegalAccessException | NoSuchMethodException | InvocationTargetException e)
                    {
//                    e.printStackTrace();
                        ProfilingLogger.addText(e.getMessage());
                    }
                }
                return null;
            }
            return jLabel;
        }
    }
}
