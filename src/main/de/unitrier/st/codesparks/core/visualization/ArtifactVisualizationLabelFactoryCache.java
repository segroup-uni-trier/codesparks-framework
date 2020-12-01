package de.unitrier.st.codesparks.core.visualization;

import de.unitrier.st.codesparks.core.ArtifactPoolManager;
import de.unitrier.st.codesparks.core.IArtifactPool;
import de.unitrier.st.codesparks.core.data.AArtifact;

import javax.swing.*;
import java.util.HashMap;
import java.util.Map;

/*
 * Copyright (c), Oliver Moseler, 2020
 */
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

    public void addToCache(
            String identifier
            , Class<? extends AArtifactVisualizationLabelFactory> factory
            , JLabel artifactVisualizationLabel
    )
    {
        JLabel jLabel = new JLabel(artifactVisualizationLabel.getIcon());
        synchronized (this)
        {
            Map<Class<? extends AArtifactVisualizationLabelFactory>, JLabel> labelMap = cache.getOrDefault(identifier, new HashMap<>());
            labelMap.put(factory, jLabel);
            cache.put(identifier, labelMap);
        }
    }

    public JLabel getCachedArtifactVisualizationLabel(
            final String artifactIdentifier
            , final AArtifactVisualizationLabelFactory factory
            , final boolean createIfAbsent
    )
    {
        synchronized (this)
        {
            Map<Class<? extends AArtifactVisualizationLabelFactory>, JLabel> classJLabelMap = cache.get(artifactIdentifier);
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
            JLabel jLabel = classJLabelMap.get(factory.getClass());
            if (jLabel == null)
            {
                if (createIfAbsent)
                {
                    IArtifactPool profilingResult = ArtifactPoolManager.getInstance().getArtifactPool();
                    AArtifact artifact = profilingResult.getArtifact(artifactIdentifier);
                    if (artifact == null)
                    {
                        return null;
                    }
                    JLabel artifactLabel = factory.createArtifactLabel(artifact);
                    artifactLabel.setHorizontalAlignment(SwingConstants.CENTER);
                    classJLabelMap.put(factory.getClass(), artifactLabel);
                    cache.put(artifactIdentifier, classJLabelMap);
                    return artifactLabel;
                }
                return null;
            }
            return jLabel;
        }
    }
}
