package de.unitrier.st.codesparks.core.visualization;

import de.unitrier.st.codesparks.core.data.AArtifact;
import de.unitrier.st.codesparks.core.logging.CodeSparksLogger;
import de.unitrier.st.codesparks.core.IArtifactPool;
import de.unitrier.st.codesparks.core.ArtifactPoolManager;

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
                        IArtifactPool profilingResult = ArtifactPoolManager.getInstance().getArtifactPool();
                        AArtifact artifact = profilingResult.getArtifact(identifier);
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
                        CodeSparksLogger.addText(e.getMessage());
                    }
                }
                return null;
            }
            return jLabel;
        }
    }
}
