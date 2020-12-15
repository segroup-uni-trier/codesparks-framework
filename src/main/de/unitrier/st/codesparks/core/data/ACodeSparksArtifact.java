package de.unitrier.st.codesparks.core.data;

/*
 * Copyright (c), Oliver Moseler, 2020
 */
@Deprecated
public abstract class ACodeSparksArtifact extends AArtifact
{
//    public ACodeSparksArtifact(final String name, final String identifier)
//    {
//        this(name, identifier, null);
//    }

    public ACodeSparksArtifact(final String name, final String identifier, final Class<? extends AThreadArtifact> threadArtifactClass)
    {
        super(name, identifier, threadArtifactClass);
    }
}
