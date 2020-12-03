package de.unitrier.st.codesparks.core;

import de.unitrier.st.codesparks.core.data.IThreadArtifactFilter;

/*
 * Copyright (c), Oliver Moseler, 2020
 */
public interface IThreadArtifactFilterable
{
    void applyThreadFilter(IThreadArtifactFilter threadFilter);
}
