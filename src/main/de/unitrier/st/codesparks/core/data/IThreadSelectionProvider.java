/*
 * Copyright (c) 2021. Oliver Moseler
 */

package de.unitrier.st.codesparks.core.data;

import java.util.Set;

public interface IThreadSelectionProvider
{
    Set<AThreadArtifact> getSelectedThreadArtifacts();

    Set<AThreadArtifact> getFilteredThreadArtifacts();

    Set<AThreadArtifact> getSelectedThreadArtifactsOfCluster(ThreadArtifactCluster cluster);

    Set<AThreadArtifact> getFilteredThreadArtifactsOfCluster(ThreadArtifactCluster cluster);
}
