/*
 * Copyright (c) 2021. Oliver Moseler
 */
package de.unitrier.st.codesparks.core;

import de.unitrier.st.codesparks.core.data.IThreadArtifactFilter;

public interface IThreadArtifactFilterable
{
    void applyThreadFilter(IThreadArtifactFilter threadFilter);
}
