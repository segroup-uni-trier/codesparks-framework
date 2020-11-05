package de.unitrier.st.insituprofiling.core.overview;

import de.unitrier.st.insituprofiling.core.data.AProfilingArtifact;

public interface IArtifactFilter
{
    boolean filterArtifact(final AProfilingArtifact artifact);
}
