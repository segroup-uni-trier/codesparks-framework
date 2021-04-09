/*
 * Copyright (c) 2021. Oliver Moseler
 */

package de.unitrier.st.codesparks.core.data;

/*
Is package private on purpose!
 */
final class DefaultThreadArtifact extends AThreadArtifact
{
    DefaultThreadArtifact(final String identifier)
    {
        super(identifier, DefaultThreadArtifact.class);
    }
}