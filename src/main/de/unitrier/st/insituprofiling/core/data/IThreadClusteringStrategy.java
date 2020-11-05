package de.unitrier.st.insituprofiling.core.data;

import java.util.Collection;

interface IThreadClusteringStrategy
{
    ThreadArtifactClustering clusterThreadArtifacts(Collection<ThreadArtifact> threadArtifacts);
}
