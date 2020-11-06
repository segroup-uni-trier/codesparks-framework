package de.unitrier.st.codesparks.core.data;

import java.util.Collection;

interface IThreadClusteringStrategy
{
    ThreadArtifactClustering clusterThreadArtifacts(Collection<ThreadArtifact> threadArtifacts);
}
