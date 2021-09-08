/*
 * Copyright (c) 2021. Oliver Moseler
 */
package de.unitrier.st.codesparks.core.data;

import java.util.Set;

public interface IThreadArtifactFilter
{
    //    boolean filterThreadArtifact(ThreadArtifact threadArtifact);
    //    boolean filterThreadArtifact(AProfilingArtifact artifact);
    Set<String> getFilteredThreadIdentifiers();

    Set<String> getSelectedThreadIdentifiers();

}
