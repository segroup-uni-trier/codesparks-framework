/*
 * Copyright (c) 2021. Oliver Moseler
 */
@file:JvmName("NeighborThreadVisualizationUtil")
@file:JvmMultifileClass

package de.unitrier.st.codesparks.core.visualization.neighbor

import de.unitrier.st.codesparks.core.data.AMetricIdentifier
import de.unitrier.st.codesparks.core.data.ANeighborArtifact
import de.unitrier.st.codesparks.core.data.AThreadArtifact

object NeighborThreadVisualizationUtil
{
    @JvmStatic
    fun getDifferentThreadsOfLine(
        threadFilteredNeighborArtifactsOfLine: MutableList<ANeighborArtifact>,
        primaryMetricIdentifier: AMetricIdentifier
    ):
            Set<AThreadArtifact>
    {
        val differentThreadsOfLine: MutableSet<AThreadArtifact> = HashSet()
        for (neighborArtifact in threadFilteredNeighborArtifactsOfLine)
        {
            val threadArtifacts = neighborArtifact.threadArtifacts
            for (threadArtifact in threadArtifacts.filter {
                it.getNumericalMetricValue(primaryMetricIdentifier) > 0 && it.isSelected
            })
            {
                if (differentThreadsOfLine.none { it.identifier.equals(threadArtifact.identifier) })
                {
                    differentThreadsOfLine.add(threadArtifact)
                }
            }
        }
        return differentThreadsOfLine
    }

    @JvmStatic
    fun getThreadTypesListOfLine(
        threadFilteredNeighborArtifactsOfLine: MutableList<ANeighborArtifact>,
        primaryMetricIdentifier: AMetricIdentifier
    ): Map<String, ArrayList<AThreadArtifact>>
    {
        val threadTypeListsOfLine: MutableMap<String, ArrayList<AThreadArtifact>> = mutableMapOf()
        for (neighborArtifact in threadFilteredNeighborArtifactsOfLine)
        {
            val currentThreadTypeLists =
                neighborArtifact.getThreadTypeListsOfSelectedThreadsWithNumericMetricValue(primaryMetricIdentifier)
            for (currentThreadTypeList in currentThreadTypeLists)
            {
                val key = currentThreadTypeList.key
                val list = threadTypeListsOfLine.getOrDefault(key, ArrayList())
                for (aThreadArtifact in currentThreadTypeList.value)
                {
                    if (list.none { it.identifier.equals(aThreadArtifact.identifier) })
                    { // No duplicates
                        list.add(aThreadArtifact)
                    }
                }
                threadTypeListsOfLine[key] = list
            }
        }
        return threadTypeListsOfLine
    }
}


