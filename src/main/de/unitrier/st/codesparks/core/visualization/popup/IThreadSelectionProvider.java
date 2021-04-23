/*
 * Copyright (c) 2021. Oliver Moseler
 */

package de.unitrier.st.codesparks.core.visualization.popup;

import de.unitrier.st.codesparks.core.data.AThreadArtifact;
import de.unitrier.st.codesparks.core.data.ThreadArtifactCluster;

import java.util.Set;

public interface IThreadSelectionProvider
{
    Set<AThreadArtifact> getSelectedThreadArtifacts();

    Set<AThreadArtifact> getFilteredThreadArtifacts();

    Set<AThreadArtifact> getSelectedThreadArtifactsOfCluster(ThreadArtifactCluster cluster);
}
