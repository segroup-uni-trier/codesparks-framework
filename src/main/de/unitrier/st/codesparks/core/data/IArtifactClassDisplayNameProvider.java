/*
 * Copyright (c) 2021-2022.
 */
package de.unitrier.st.codesparks.core.data;

public interface IArtifactClassDisplayNameProvider
{
    String getDisplayName(Class<? extends AArtifact> artifactClass);
}
