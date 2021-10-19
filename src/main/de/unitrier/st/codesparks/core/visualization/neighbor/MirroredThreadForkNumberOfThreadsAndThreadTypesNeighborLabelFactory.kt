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
import de.unitrier.st.codesparks.core.visualization.thread.ThreadVisualizationUtil
import de.unitrier.st.codesparks.core.visualization.thread.VisualThreadClusterPropertiesManager
import java.awt.Rectangle
import javax.swing.JLabel

class MirroredThreadForkNumberOfThreadsAndThreadTypesNeighborLabelFactory(
    primaryMetricIdentifier:
    AMetricIdentifier?, sequence: Int
) :
    ANeighborArtifactVisualizationLabelFactory
        (primaryMetricIdentifier, sequence)
{

    override fun createNeighborArtifactLabel(
        artifact: AArtifact?,
        threadFilteredNeighborArtifactsOfLine: MutableList<ANeighborArtifact>?
    ): JLabel
    {
        if (threadFilteredNeighborArtifactsOfLine == null)
        {
            return emptyLabel()
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

        // val initialThreadSquareYPos = lineHeight - threadSquareEdgeLength - 2
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

        // Draw the clusters
        val differentThreadsOfLine: Set<AThreadArtifact> = NeighborThreadVisualizationUtil
            .getDifferentThreadsOfLine(threadFilteredNeighborArtifactsOfLine, primaryMetricIdentifier)
        val totalNumberOfSelectedThreadsOfLine = differentThreadsOfLine.size

        val threadTypesListOfLine = NeighborThreadVisualizationUtil.getThreadTypesListOfLine(
            threadFilteredNeighborArtifactsOfLine,
            primaryMetricIdentifier
        )

        val threadSquareYPos = lineHeight - threadSquareEdgeLength - 2

        val selectedClustering = artifact!!.selectedClustering ?: return makeLabel(graphics)

        val clusterPropertiesManager = VisualThreadClusterPropertiesManager.getInstance(selectedClustering)
        val drawPositions = ThreadVisualizationUtil.getDrawPositions(selectedClustering, clusterPropertiesManager)

        var clusterNum = 0
        for (threadCluster in selectedClustering)
        {
            val numberOfThreadsOfCluster = threadCluster.filter {
                it.isSelected && differentThreadsOfLine.any { thr ->
                    thr.identifier.equals(it.identifier)
                }
            }.size
            if (numberOfThreadsOfCluster == 0)
            {
                clusterNum += 1
                continue
            }

            val properties = clusterPropertiesManager.getOrDefault(threadCluster, clusterNum)
            val clusterColor = properties.color

            val positionToDrawCluster = drawPositions[threadCluster]
            val clusterYPos = TOP_OFFSET + threadSquareYPos - positionToDrawCluster!! * threadSquareOffset

            var percent: Double = numberOfThreadsOfCluster / totalNumberOfSelectedThreadsOfLine.toDouble()

            val totalNumberOfThreadsWidth =
                ThreadVisualizationUtil.getDiscreteTenValuedScaleWidth(percent, clusterBarMaxWidth)

            // Number of threads bar
            val backgroundMetricColor = VisualizationUtil.getBackgroundMetricColor(clusterColor, .35f)
            graphics.color = backgroundMetricColor
            graphics.fillRect(
                X_OFFSET_LEFT + barChartWidth - totalNumberOfThreadsWidth - 2, clusterYPos,
                totalNumberOfThreadsWidth, threadSquareEdgeLength
            )

            // The thread-types bar
            val threadTypesSetOfClusterOfLine: MutableSet<String> = HashSet()
            for (threadArtifact in threadCluster)
            {
                for (entry in threadTypesListOfLine.entries)
                {
                    if (entry.value.any { it.identifier.equals(threadArtifact.identifier) })
                    {
                        threadTypesSetOfClusterOfLine.add(entry.key)
                    }
                }
            }
            val numberOfThreadTypesOfClusterOfLine = threadTypesSetOfClusterOfLine.size
            percent = numberOfThreadTypesOfClusterOfLine / totalNumberOfSelectedThreadsOfLine.toDouble()

            val totalNumberOfThreadTypesOfClusterOfLineWidth =
                ThreadVisualizationUtil.getDiscreteTenValuedScaleWidth(percent, clusterBarMaxWidth)

            graphics.color = clusterColor
            graphics.fillRect(X_OFFSET_LEFT + barChartWidth - totalNumberOfThreadTypesOfClusterOfLineWidth - 2,
                clusterYPos, totalNumberOfThreadTypesOfClusterOfLineWidth, threadSquareEdgeLength)

            if (totalNumberOfThreadsWidth > 0)
            {
                graphics.fillRect(X_OFFSET_LEFT + barChartWidth - 2, clusterYPos + 1, arrowLength - 1, 1)
            }

            //-------------
            clusterNum += 1
        }

        return makeLabel(graphics)
    }
}