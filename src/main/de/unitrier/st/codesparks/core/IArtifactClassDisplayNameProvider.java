package de.unitrier.st.codesparks.core;

import de.unitrier.st.codesparks.core.data.AArtifact;

/*
 * Copyright (c), Oliver Moseler, 2020
 */
public interface IArtifactClassDisplayNameProvider
{
    String getDisplayName(Class<? extends AArtifact> artifactClass);
}
