package de.unitrier.st.codesparks.core.data;

import java.util.Collection;

/*
 * Copyright (c), Oliver Moseler, 2020
 */
interface IThreadArtifactClusteringStrategy
{
    ThreadArtifactClustering clusterCodeSparksThreads(Collection<AThreadArtifact> codeSparksThreads);
}
