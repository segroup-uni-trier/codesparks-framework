package de.unitrier.st.codesparks.core.data;

import de.unitrier.st.codesparks.core.ArtifactPoolManager;
import de.unitrier.st.codesparks.core.IArtifactPool;

import java.util.HashSet;
import java.util.Set;

/*
 * Copyright (c), Oliver Moseler, 2020
 */
public class GlobalResetThreadFilter implements ICodeSparksThreadFilter
{

    private static volatile ICodeSparksThreadFilter instance;

    private GlobalResetThreadFilter()
    {

    }

    public static ICodeSparksThreadFilter getInstance()
    {
        if (instance == null)
        {
            synchronized (GlobalResetThreadFilter.class)
            {
                if (instance == null)
                {
                    instance = new GlobalResetThreadFilter();
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
        return programArtifact.getCodeSparksThreadIdentifiers();
    }
}
