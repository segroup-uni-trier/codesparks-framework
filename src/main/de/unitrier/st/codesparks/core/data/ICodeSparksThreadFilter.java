package de.unitrier.st.codesparks.core.data;

import java.util.Set;

/*
 * Copyright (c), Oliver Moseler, 2020
 */
public interface ICodeSparksThreadFilter
{
    //    boolean filterThreadArtifact(ThreadArtifact threadArtifact);
    //    boolean filterThreadArtifact(AProfilingArtifact artifact);
    Set<String> getFilteredThreadIdentifiers();

    Set<String> getSelectedThreadIdentifiers();

}
