package de.unitrier.st.insituprofiling.core;

import de.unitrier.st.insituprofiling.core.data.IThreadArtifactFilter;

public interface IThreadArtifactFilterable
{
    void applyThreadArtifactFilter(IThreadArtifactFilter threadArtifactFilter);
}
