/*
 * Copyright (c) 2021. Oliver Moseler
 */
package de.unitrier.st.codesparks.core.overview;

import de.unitrier.st.codesparks.core.data.AArtifact;

public interface IArtifactFilter
{
    boolean filterArtifact(final AArtifact artifact);
}
