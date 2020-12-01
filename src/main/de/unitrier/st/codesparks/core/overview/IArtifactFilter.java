package de.unitrier.st.codesparks.core.overview;

import de.unitrier.st.codesparks.core.data.AArtifact;

/*
 * Copyright (c), Oliver Moseler, 2020
 */
public interface IArtifactFilter
{
    boolean filterArtifact(final AArtifact artifact);
}
