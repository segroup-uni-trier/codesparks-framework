package de.unitrier.st.codesparks.core;

import de.unitrier.st.codesparks.core.data.AArtifact;
import de.unitrier.st.codesparks.core.data.AMetricArtifact;

/*
 * Copyright (c), Oliver Moseler, 2020
 */
public interface IArtifactClassDisplayNameProvider
{
    String getDisplayName(Class<? extends AMetricArtifact> artifactClass);
}
