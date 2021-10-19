/*
 * Copyright (c) 2021. Oliver Moseler
 */

package de.unitrier.st.codesparks.core.visualization.neighbor

import de.unitrier.st.codesparks.core.data.AArtifact
import de.unitrier.st.codesparks.core.data.AMetricIdentifier
import de.unitrier.st.codesparks.core.data.ANeighborArtifact
import de.unitrier.st.codesparks.core.data.AThreadArtifact
import de.unitrier.st.codesparks.core.visualization.VisConstants
import de.unitrier.st.codesparks.core.visualization.VisualizationUtil
import java.awt.Font
import javax.swing.JLabel
import kotlin.math.ceil
import kotlin.math.floor

class TextualTotalNumberOfThreadsNeighborLabelFactory(primaryMetricIdentifier: AMetricIdentifier?, sequence: Int) :
        ANeighborArtifactVisualizationLabelFactory(primaryMetricIdentifier, sequence) {
    override fun createNeighborArtifactLabel(artifact: AArtifact?, threadFilteredNeighborArtifactsOfLine: MutableList<ANeighborArtifact>?): JLabel {
        if (threadFilteredNeighborArtifactsOfLine == null) {
            return emptyLabel()
        }
        val differentThreads: MutableSet<AThreadArtifact> = HashSet()
        for (neighborArtifact in threadFilteredNeighborArtifactsOfLine) {
            val threadArtifacts = neighborArtifact.threadArtifacts
            for (threadArtifact in threadArtifacts.filter { thr ->
                thr.getNumericalMetricValue(primaryMetricIdentifier) > 0 && thr.isSelected
            }) {
                if (differentThreads.none { thread -> thread.identifier.equals(threadArtifact.identifier) }) {
                    differentThreads.add(threadArtifact)
                }
            }
        }

        val numberOfDifferentThreads = differentThreads.size

        val threadsPerColumn = 3
        val X_OFFSET_LEFT = this.X_OFFSET_LEFT + 0
        val TEXT_START_OFFSET_LEFT = 1

        val lineHeight = VisualizationUtil.getLineHeightCeil(VisConstants.getLineHeight(), threadsPerColumn)

        val graphics = getGraphics(300, lineHeight)
        var textWidth = 0
        graphics.color = VisConstants.BORDER_COLOR
        val arialFont = Font("Arial", Font.BOLD, 11)
        graphics.font = arialFont

        val fontHeight: Int = graphics.fontHeight()
        val halfLineHeight: Int = ceil(lineHeight / 2.0).toInt()
        val halfFontHeight: Int = ceil(fontHeight / 2.0).toInt()
        val textYPos: Int = halfLineHeight + halfFontHeight - (floor(halfFontHeight / 2.0) - 1).toInt()

        val totalNumberOfThreadsString = numberOfDifferentThreads.toString()
        graphics.drawString(totalNumberOfThreadsString, X_OFFSET_LEFT + TEXT_START_OFFSET_LEFT + textWidth, textYPos)

        textWidth += graphics.stringWidth(totalNumberOfThreadsString)

        val totalWidth = X_OFFSET_LEFT + TEXT_START_OFFSET_LEFT + textWidth

        return makeLabel(graphics, totalWidth)
    }
}