/*
 * Copyright (c) 2021. Oliver Moseler
 */
@file:JvmName("NeighborThreadVisualizationUtil")
@file:JvmMultifileClass

package de.unitrier.st.codesparks.core.visualization.neighbor

import de.unitrier.st.codesparks.core.data.AMetricIdentifier
import de.unitrier.st.codesparks.core.data.ANeighborArtifact
import de.unitrier.st.codesparks.core.data.AThreadArtifact
import java.util.stream.Collectors

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

    @JvmStatic
    fun getTotalThreadFilteredMetricValueOfAllNeighborsOfLine(
        threadFilteredNeighborArtifactsOfLine: List<ANeighborArtifact>,
        primaryMetricIdentifier: AMetricIdentifier
    ): Double
    {
        val totalThreadFilteredMetricValueOfAllNeighborsOfLine = threadFilteredNeighborArtifactsOfLine.stream()
            .mapToDouble { neighbor: ANeighborArtifact ->
                val neighborNonFilteredThreadArtifacts =
                    neighbor.threadArtifacts.stream().filter { obj: AThreadArtifact -> obj.isSelected }
                        .collect(Collectors.toList())
                val neighborNumericalMetricValue =
                    neighbor.getNumericalMetricValue(primaryMetricIdentifier)
                var neighborTotal = 0.0
                for (neighborThread in neighborNonFilteredThreadArtifacts)
                {
                    neighborTotal += neighborThread.getNumericalMetricValue(primaryMetricIdentifier) * neighborNumericalMetricValue
                }
                neighborTotal
            }.sum()
        return totalThreadFilteredMetricValueOfAllNeighborsOfLine
    }

    @JvmStatic
    fun getThreadFilteredClusterMetricValueOfLineRelativeToTotal(
        neighborArtifacts: List<ANeighborArtifact>,
        threadCluster: List<AThreadArtifact>,
        totalRuntimeOfAllNeighborsOfLine: Double,
        primaryMetricIdentifier: AMetricIdentifier,
        average: Boolean
    ): Double
    {
        val neighborArtifactsExecutedByThreadsOfTheCluster = neighborArtifacts.stream()
            .filter { neighbor: ANeighborArtifact ->
                neighbor.threadArtifacts
                    .stream()
                    .anyMatch { threadExecutingNeighbor: AThreadArtifact ->
                        threadExecutingNeighbor.isSelected &&
                                threadCluster.stream()
                                    .anyMatch { threadOfCluster: AThreadArtifact -> threadOfCluster.isSelected && threadOfCluster.identifier == threadExecutingNeighbor.identifier }
                    }
            }
            .collect(Collectors.toList())

        var clusterRuntimeOfLine = 0.0
        val threads: MutableSet<AThreadArtifact> = java.util.HashSet(1 shl 4)

        for (neighborExecutedByAnyClusterThread in neighborArtifactsExecutedByThreadsOfTheCluster)
        {
            val neighborRuntime = neighborExecutedByAnyClusterThread.getNumericalMetricValue(primaryMetricIdentifier)
            for (thread in threadCluster.stream().filter { obj: AThreadArtifact -> obj.isSelected }
                .collect(Collectors.toList()))
            {
                val neighborThread = neighborExecutedByAnyClusterThread.getThreadArtifact(thread.identifier) ?: continue
                val neighborThreadRuntimeRatio = neighborThread.getNumericalMetricValue(primaryMetricIdentifier)
                clusterRuntimeOfLine += neighborRuntime / totalRuntimeOfAllNeighborsOfLine * neighborThreadRuntimeRatio
                if (neighborThreadRuntimeRatio > 0)
                { //
                    if (threads.stream().noneMatch { t: AThreadArtifact -> t.identifier == neighborThread.identifier })
                    { // Maybe the same thread (identifier) executes different callees in a single line, but it must not be counted multiple times!
                        threads.add(neighborThread)
                    }
                }
            }
        }
        return if (average)
        {
            clusterRuntimeOfLine / threads.size
        } else clusterRuntimeOfLine
    }

}


