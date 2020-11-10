package de.unitrier.st.codesparks.core.data;

import java.util.Collection;

interface ICodeSparksThreadClusteringStrategy
{
    CodeSparksThreadClustering clusterCodeSparksThreads(Collection<CodeSparksThread> codeSparksThreads);
}
