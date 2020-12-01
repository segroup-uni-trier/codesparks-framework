package de.unitrier.st.codesparks.core.data;

import java.util.Collection;

/*
 * Copyright (c), Oliver Moseler, 2020
 */
interface ICodeSparksThreadClusteringStrategy
{
    CodeSparksThreadClustering clusterCodeSparksThreads(Collection<ACodeSparksThread> codeSparksThreads);
}
