/*
 * Copyright (c) 2021. Oliver Moseler
 */

package de.unitrier.st.codesparks.core.data;

/*
Is package private on purpose!
 */
final class DefaultArtifact extends AArtifact
{
    DefaultArtifact(final String identifier, final String name)
    {
        super(identifier, name, DefaultThreadArtifact.class);
    }
}
