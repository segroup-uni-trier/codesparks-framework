package de.unitrier.st.codesparks.core;

/*
 * Copyright (c), Oliver Moseler, 2020
 */
public class ArtifactPoolManager
{
    private static volatile ArtifactPoolManager instance;

    private ArtifactPoolManager() { }

    public static ArtifactPoolManager getInstance()
    {
        if (instance == null)
        {
            synchronized (ArtifactPoolManager.class)
            {
                if (instance == null)
                {
                    instance = new ArtifactPoolManager();
                }
            }
        }
        return instance;
    }

    private IArtifactPool artifactPool;

    public IArtifactPool getArtifactPool()
    {
        return artifactPool;
    }

    public void setArtifactPool(IArtifactPool artifactPool)
    {
        this.artifactPool = artifactPool;
    }
}
