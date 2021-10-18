/*
 * Copyright (c) 2021. Oliver Moseler
 */

package de.unitrier.st.codesparks.core.visualization.neighbor

import de.unitrier.st.codesparks.core.data.AArtifact
import de.unitrier.st.codesparks.core.data.AMetricIdentifier
import de.unitrier.st.codesparks.core.data.ANeighborArtifact
import de.unitrier.st.codesparks.core.visualization.VisConstants
import de.unitrier.st.codesparks.core.visualization.VisualizationUtil
import java.awt.Rectangle
import javax.swing.JLabel

class MirroredThreadForkNumberOfThreadsAndThreadTypesNeighborLabelFactory(primaryMetricIdentifier:
                                                                          AMetricIdentifier?, sequence: Int) :
        ANeighborArtifactVisualizationLabelFactory
        (primaryMetricIdentifier, sequence) {

    constructor(primaryMetricIdentifier:
                AMetricIdentifier?) : this(primaryMetricIdentifier, -1)


    override fun createNeighborArtifactLabel(artifact: AArtifact?, threadFilteredNeighborArtifactsOfLine: MutableList<ANeighborArtifact>?): JLabel {

        val threadFilteredNeighborArtifactsOfLineWithRuntime =
                threadFilteredNeighborArtifactsOfLine?.filter { neighbor: ANeighborArtifact ->
                    neighbor.getNumericalMetricValue(primaryMetricIdentifier) > 0
                }
        if (threadFilteredNeighborArtifactsOfLine!!.isEmpty()) {
            return emptyLabel();
        }
        val threadsPerColumn = 3
        val lineHeight = VisualizationUtil.getLineHeightFloor(VisConstants.getLineHeight(), threadsPerColumn)

        val X_OFFSET_LEFT = -1
        val X_OFFSET_RIGHT = 1
        val TOP_OFFSET = 6

        val threadMetaphorWidth = 24
        val barChartWidth = 24

        val clusterBarMaxWidth = 20

        val totalWidth = X_OFFSET_LEFT + threadMetaphorWidth + barChartWidth + X_OFFSET_RIGHT

        val threadSquareEdgeLength = 3

        val initialThreadSquareYPos = lineHeight - threadSquareEdgeLength - 2
        val threadSquareOffset = threadSquareEdgeLength + 1

        val graphics = getGraphics(totalWidth, lineHeight + TOP_OFFSET)

        graphics.color = VisConstants.BORDER_COLOR

        // The rectangle for the bars
        val threadVisualisationArea = Rectangle(X_OFFSET_LEFT, TOP_OFFSET, barChartWidth - 1, lineHeight - 1)
        graphics.drawRectangle(threadVisualisationArea)


        // Thread metaphor
        val barrierXOffset = 9
        val barrierXPos = barChartWidth + barrierXOffset //threadMetaphorWidth / 2;
        // Vertical bar or barrier, respectively
        val barrierWidth = 3
        graphics.fillRect(X_OFFSET_LEFT + barrierXPos, TOP_OFFSET, barrierWidth, lineHeight)

        // Subsequent arrow
        val arrowLength = threadMetaphorWidth / 2
        val arrowStartX = X_OFFSET_LEFT + barrierXPos + barrierWidth

        graphics.fillRect(arrowStartX, TOP_OFFSET + lineHeight / 2, arrowLength, 1)
        graphics.drawLine(arrowStartX + 3, TOP_OFFSET + lineHeight / 2 - 3, arrowStartX, TOP_OFFSET + lineHeight / 2)
        graphics.drawLine(arrowStartX + 3, TOP_OFFSET + lineHeight / 2 + 3, arrowStartX, TOP_OFFSET + lineHeight / 2)






        return makeLabel(graphics);
    }
}