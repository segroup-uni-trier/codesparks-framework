/*
 * Copyright (c) 2021. Oliver Moseler
 */

package de.unitrier.st.codesparks.core.data

import com.google.common.collect.Multisets
import com.google.common.collect.Sets
import kotlin.math.max

fun similarity(t1: ArtifactTrie?, t2: ArtifactTrie?, artifactIdentifier: String): Double {
    if (t1 == null || t2 == null) {
        return 0.0
    }
    val intersectingTrie: ArtifactTrie = intersection(t1, t2, artifactIdentifier)!!
    val intersectionSize = intersectingTrie.vertexSet().size.toLong()
    if (intersectionSize == 0L) {
        return 0.0
    }
    val sizeT1 = t1.getNumberOfNodesOfSubtree(artifactIdentifier)
    val sizeT2 = t2.getNumberOfNodesOfSubtree(artifactIdentifier)
    return intersectionSize.toDouble() / max(sizeT1, sizeT2)
}


fun distance(t1: ArtifactTrie, t2: ArtifactTrie, artifactIdentifier: String): Double {
    val similarity: Double = similarity(t1, t2, artifactIdentifier)
    assert(similarity in 0.0..1.0)
    return 1 - similarity //Math.max(0, 1 - similarity);
}


private fun dfs(
        t1: ArtifactTrie
        , t1Node: ArtifactTrieNode
        , t2: ArtifactTrie
        , t2Node: ArtifactTrieNode
        , intersection: ArtifactTrie
        , artifactIdentifier: String
): Int {
    if (t1Node != t2Node) {
        return 0
    }
    if (t1Node.label == artifactIdentifier) {
        return 1
    }

    val outEdgesOfT1 = t1.outgoingEdgesOf(t1Node)
    var ret = 0
    for (artifactTrieEdge in outEdgesOfT1) {
        if (t2.containsEdge(artifactTrieEdge)) {
            val targetT1 = artifactTrieEdge.target
            val targetT2 = t2.getEdgeTarget(artifactTrieEdge)
            val childValue = dfs(t1, targetT1, t2, targetT2, intersection, artifactIdentifier)
            if (childValue > 0)
            {
                ret = 1
                intersection.addVertex(t1Node.id, t1Node.label)
                intersection.addVertex(targetT1.id, targetT1.label)
                intersection.addEdge(t1Node, targetT1, ArtifactTrieEdge(t1Node, targetT1))
            }
        }
    }
    return ret
}

fun intersection(t1: ArtifactTrie, t2: ArtifactTrie, artifactIdentifier: String): ArtifactTrie? {
    val t1Root = t1.root // I doesn't matter which of the two tries we choose.
    val t2Root = t2.root
    if (t1Root != t2Root) {
        return null
    }
    val intersection = ArtifactTrie(ArtifactTrieEdge::class.java)
    dfs(t1, t1Root, t2, t2Root, intersection, artifactIdentifier)
    return intersection
}

fun multisetJaccard(t1: ArtifactTrie, t2: ArtifactTrie): Double {
    val multiSetT1 = t1.vertexLabelsMultiSet
    val multiSetT2 = t2.vertexLabelsMultiSet
    val intersection = Multisets.intersection(multiSetT1, multiSetT2)

    @Suppress("UnstableApiUsage")
    val union = Multisets.union(multiSetT1, multiSetT2)
    return intersection.size / union.size.toDouble()
}

fun jaccard(t1: ArtifactTrie, t2: ArtifactTrie): Double {
    val vertexLabelsSetT1 = t1.vertexLabelsSet
    val vertexLabelsSetT2 = t2.vertexLabelsSet
    val intersection: Set<String?> = Sets.intersection(vertexLabelsSetT1, vertexLabelsSetT2)
    val union: Set<String?> = Sets.union(vertexLabelsSetT1, vertexLabelsSetT2)
    return intersection.size / union.size.toDouble()
}


