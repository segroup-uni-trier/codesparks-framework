package de.unitrier.st.insituprofiling.core.data;

import java.util.Set;

public interface IThreadArtifactFilter
{
    //    boolean filterThreadArtifact(ThreadArtifact threadArtifact);
    //    boolean filterThreadArtifact(AProfilingArtifact artifact);
    Set<String> getFilteredThreadArtifactIdentifiers();

    Set<String> getSelectedThreadArtifactIdentifiers();

}
