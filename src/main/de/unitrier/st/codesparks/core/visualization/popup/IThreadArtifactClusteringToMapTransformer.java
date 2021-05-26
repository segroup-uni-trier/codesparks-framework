/*
 * Copyright (c) 2021. Oliver Moseler
 */

package de.unitrier.st.codesparks.core.visualization.popup;

import de.unitrier.st.codesparks.core.data.AArtifact;
import de.unitrier.st.codesparks.core.data.AThreadArtifact;
import de.unitrier.st.codesparks.core.data.ThreadArtifactClustering;

import java.util.List;
import java.util.Map;

public interface IThreadArtifactClusteringToMapTransformer
{
    Map<String, List<AThreadArtifact>> transformClusteringToMap(AArtifact artifact, ThreadArtifactClustering threadArtifactClustering);
}
