/*
 * Copyright (c) 2021. Oliver Moseler
 */

package de.unitrier.st.codesparks.core.visualization.neighbor

import de.unitrier.st.codesparks.core.data.AArtifact
import de.unitrier.st.codesparks.core.data.AMetricIdentifier
import de.unitrier.st.codesparks.core.data.ANeighborArtifact
import de.unitrier.st.codesparks.core.data.AThreadArtifact
import de.unitrier.st.codesparks.core.visualization.VisConstants
import java.awt.Font
import javax.swing.JLabel
import kotlin.math.ceil
import kotlin.math.floor

class TextualTotalNumberOfThreadTypesNeighborLabelFactory(primaryMetricIdentifier: AMetricIdentifier?, sequence: Int) :
    ANeighborArtifactVisualizationLabelFactory(primaryMetricIdentifier, sequence)
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

        val threadTypeLists: MutableMap<String, ArrayList<AThreadArtifact>> = mutableMapOf()

        for (neighborArtifact in threadFilteredNeighborArtifactsOfLine)
        {
            val currentThreadTypeLists =
                neighborArtifact.getThreadTypeListsOfSelectedThreadsWithNumericMetricValue(primaryMetricIdentifier)
            for (currentThreadTypeList in currentThreadTypeLists)
            {
                val key = currentThreadTypeList.key
                val list = threadTypeLists.getOrDefault(key, ArrayList())
                for (aThreadArtifact in currentThreadTypeList.value)
                {
                    if (list.none { thr -> thr.identifier.equals(aThreadArtifact.identifier) })
                    { // No duplicates
                        list.add(aThreadArtifact)
                    }
                }
                threadTypeLists[key] = list
            }
        }

        val numberOfDifferentThreadTypes: Int = threadTypeLists.size

        val lineHeight = VisConstants.getLineHeight()
        val graphics = getGraphics(300, lineHeight)
        graphics.color = VisConstants.BORDER_COLOR
        val arialFont = Font("Arial", Font.BOLD, 11)
        graphics.font = arialFont

        val fontHeight: Int = graphics.fontHeight()
        val halfLineHeight: Int = ceil(lineHeight / 2.0).toInt()
        val halfFontHeight: Int = ceil(fontHeight / 2.0).toInt()
        val textYPos: Int = halfLineHeight + halfFontHeight - (floor(halfFontHeight / 2.0) - 1).toInt()

        val totalNumberOfThreadTypesString = "/$numberOfDifferentThreadTypes"
        graphics.drawString(totalNumberOfThreadTypesString, X_OFFSET_LEFT, textYPos)

        val totalWidth = X_OFFSET_LEFT + graphics.stringWidth(totalNumberOfThreadTypesString)

        return makeLabel(graphics, totalWidth)
    }
}