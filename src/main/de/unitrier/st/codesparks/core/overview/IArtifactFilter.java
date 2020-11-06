package de.unitrier.st.codesparks.core.overview;

import de.unitrier.st.codesparks.core.data.AProfilingArtifact;

public interface IArtifactFilter
{
    boolean filterArtifact(final AProfilingArtifact artifact);
}
