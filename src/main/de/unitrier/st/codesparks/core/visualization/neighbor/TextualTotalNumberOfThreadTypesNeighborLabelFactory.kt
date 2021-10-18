/*
 * Copyright (c) 2021. Oliver Moseler
 */

package de.unitrier.st.codesparks.core.visualization.neighbor

import de.unitrier.st.codesparks.core.data.AArtifact
import de.unitrier.st.codesparks.core.data.AMetricIdentifier
import de.unitrier.st.codesparks.core.data.ANeighborArtifact
import javax.swing.JLabel

class TextualTotalNumberOfThreadTypesNeighborLabelFactory(primaryMetricIdentifier: AMetricIdentifier?, sequence: Int)
    : ANeighborArtifactVisualizationLabelFactory(primaryMetricIdentifier, sequence) {
    override fun createNeighborArtifactLabel(artifact: AArtifact?, threadFilteredNeighborArtifactsOfLine: MutableList<ANeighborArtifact>?): JLabel {
        return emptyLabel()
    }
}