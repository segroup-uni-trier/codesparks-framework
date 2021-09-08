/*
 * Copyright (c) 2021. Oliver Moseler
 */
package de.unitrier.st.codesparks.core.data;

import java.util.Collection;

interface IThreadArtifactClusteringStrategy
{
    ThreadArtifactClustering clusterThreadArtifacts(final Collection<AThreadArtifact> threadArtifacts);
}
