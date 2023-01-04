/*
 *  Copyright 2023 Oliver Moseler
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */
package de.unitrier.st.codesparks.core.visualization;

import de.unitrier.st.codesparks.core.data.ArtifactPoolManager;
import de.unitrier.st.codesparks.core.data.IArtifactPool;
import de.unitrier.st.codesparks.core.data.AArtifact;

import javax.swing.*;
import java.util.HashMap;
import java.util.Map;

public class ArtifactVisualizationLabelFactoryCache
{
    private ArtifactVisualizationLabelFactoryCache()
    {
        cache = new HashMap<>();
    }

    private static volatile ArtifactVisualizationLabelFactoryCache instance;

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

    private final Object cacheLock = new Object();

    public void addToCache(
            String identifier
            , Class<? extends AArtifactVisualizationLabelFactory> factory
            , JLabel artifactVisualizationLabel
    )
    {
        JLabel jLabel = new JLabel(artifactVisualizationLabel.getIcon());
        synchronized (cacheLock)
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
        synchronized (cacheLock)
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
                    IArtifactPool artifactPool = ArtifactPoolManager.getInstance().getArtifactPool();
                    AArtifact artifact = artifactPool.getArtifact(artifactIdentifier);

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
