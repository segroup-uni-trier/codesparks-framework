/*
 * Copyright (c), Oliver Moseler, 2021
 */
package de.unitrier.st.codesparks.core.data;

import java.util.Collection;

interface IThreadArtifactClusteringStrategy
{
    ThreadArtifactClustering clusterThreadArtifacts(final Collection<AThreadArtifact> threadArtifacts);
}
