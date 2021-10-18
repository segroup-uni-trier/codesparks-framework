/*
 * Copyright (c) 2021. Oliver Moseler
 */

package de.unitrier.st.codesparks.core.visualization.neighbor

import de.unitrier.st.codesparks.core.data.AArtifact
import de.unitrier.st.codesparks.core.data.AMetricIdentifier
import de.unitrier.st.codesparks.core.data.ANeighborArtifact
import javax.swing.JLabel

class MirroredThreadForkNumberOfThreadsAndThreadTypesNeighborLabelFactory(primaryMetricIdentifier:
                                                                          AMetricIdentifier?, sequence: Int) :
        ANeighborArtifactVisualizationLabelFactory
        (primaryMetricIdentifier, sequence) {

    constructor(primaryMetricIdentifier:
                AMetricIdentifier?) : this(primaryMetricIdentifier, -1)


    override fun createNeighborArtifactLabel(artifact: AArtifact?, threadFilteredNeighborArtifactsOfLine: MutableList<ANeighborArtifact>?): JLabel {
        TODO("Not yet implemented")
    }
}