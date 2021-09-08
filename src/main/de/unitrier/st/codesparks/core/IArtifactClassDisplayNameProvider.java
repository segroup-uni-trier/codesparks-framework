/*
 * Copyright (c) 2021. Oliver Moseler
 */
package de.unitrier.st.codesparks.core;

import de.unitrier.st.codesparks.core.data.AArtifact;

public interface IArtifactClassDisplayNameProvider
{
    String getDisplayName(Class<? extends AArtifact> artifactClass);
}
