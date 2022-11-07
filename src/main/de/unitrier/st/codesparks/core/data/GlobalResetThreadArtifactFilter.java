/*
 * Copyright (c) 2021. Oliver Moseler
 */
package de.unitrier.st.codesparks.core.data;

import java.util.HashSet;
import java.util.Set;

public class GlobalResetThreadArtifactFilter implements IThreadArtifactFilter
{

    private static volatile IThreadArtifactFilter instance;

    private GlobalResetThreadArtifactFilter()
    {

    }

    public static IThreadArtifactFilter getInstance()
    {
        if (instance == null)
        {
            synchronized (GlobalResetThreadArtifactFilter.class)
            {
                if (instance == null)
                {
                    instance = new GlobalResetThreadArtifactFilter();
                }
            }
        }

        return instance;
    }

    @Override
    public Set<String> getFilteredThreadIdentifiers()
    {
        return new HashSet<>();
    }

    @Override
    public Set<String> getSelectedThreadIdentifiers()
    {
        final ArtifactPoolManager artifactPoolManager = ArtifactPoolManager.getInstance();
        final IArtifactPool artifactPool = artifactPoolManager.getArtifactPool();
        if (artifactPool == null)
        {
            return new HashSet<>();
        }
        final AArtifact programArtifact = artifactPool.getProgramArtifact();
        if (programArtifact == null)
        {
            return new HashSet<>();
        }
        return programArtifact.getThreadArtifactIdentifiers();
    }
}
