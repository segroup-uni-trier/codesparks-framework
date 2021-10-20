/*
 * Copyright (c) 2021. Oliver Moseler
 */
//@file:JvmName("NeighborThreadVisualizationUtil")

package de.unitrier.st.codesparks.core.visualization.neighbor

import de.unitrier.st.codesparks.core.data.AMetricIdentifier
import de.unitrier.st.codesparks.core.data.ANeighborArtifact
import de.unitrier.st.codesparks.core.data.AThreadArtifact
import de.unitrier.st.codesparks.core.data.ThreadArtifactCluster

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
        val totalThreadFilteredMetricValueOfAllNeighborsOfLine = threadFilteredNeighborArtifactsOfLine.map { neighbor
            ->
            val neighborNonFilteredThreadArtifacts = neighbor.threadArtifacts.filter { it.isSelected }
            val neighborNumericalMetricValue = neighbor.getNumericalMetricValue(primaryMetricIdentifier)
            val neighborTotal = neighborNonFilteredThreadArtifacts.sumOf {
                it.getNumericalMetricValue(primaryMetricIdentifier) * neighborNumericalMetricValue
            }
            neighborTotal
        }.sumOf { it }
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
        val neighborArtifactsExecutedByThreadsOfTheCluster = neighborArtifacts.filter { neighbor ->
            neighbor.threadArtifacts.any { threadExecutingNeighbor ->
                threadExecutingNeighbor.isSelected && threadCluster.any { threadOfCluster ->
                    threadOfCluster.isSelected && threadOfCluster.identifier == threadExecutingNeighbor.identifier
                    // in Kotlin the '==' operator is the same as 'equals' in Java
                }
            }
        }

        var clusterRuntimeOfLine = 0.0
        val threads: MutableSet<AThreadArtifact> = HashSet(1 shl 4)

        for (neighborExecutedByAnyClusterThread in neighborArtifactsExecutedByThreadsOfTheCluster)
        {
            val neighborRuntime = neighborExecutedByAnyClusterThread.getNumericalMetricValue(primaryMetricIdentifier)
            for (thread in threadCluster.filter { it.isSelected })
            {
                val neighborThread = neighborExecutedByAnyClusterThread.getThreadArtifact(thread.identifier) ?: continue
                val neighborThreadRuntimeRatio = neighborThread.getNumericalMetricValue(primaryMetricIdentifier)
                clusterRuntimeOfLine += (neighborRuntime / totalRuntimeOfAllNeighborsOfLine) *
                        neighborThreadRuntimeRatio
                if (neighborThreadRuntimeRatio > 0)
                {
                    if (threads.none { it.identifier == neighborThread.identifier })
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

    @JvmStatic
    fun getThreadTypesOfClusterOfLine(
        threadTypesListOfLine: Map<String, ArrayList<AThreadArtifact>>,
        threadCluster: ThreadArtifactCluster
    ): Set<String>
    {
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
        return threadTypesSetOfClusterOfLine
    }

    @JvmStatic
    fun getThreadsOfClusterOfLine(differentThreadsOfLine: Set<AThreadArtifact>, threadCluster: ThreadArtifactCluster):
            List<AThreadArtifact>
    {
        val threadsOfClusterOfLine = threadCluster.filter {
            it.isSelected && differentThreadsOfLine.any { thr ->
                thr.identifier.equals(it.identifier)
            }
        }
        return threadsOfClusterOfLine
    }

}


