/*
 * Copyright (c) 2021. Oliver Moseler
 */

package de.unitrier.st.codesparks.core.data

//import com.google.common.collect.Multisets
//import com.google.common.collect.Sets
//import kotlin.math.max
//
//fun similarity(t1: ArtifactTrie?, t2: ArtifactTrie?, artifactIdentifier: String): Double {
//    if (t1 == null || t2 == null) {
//        return 0.0
//    }
//    val intersectingTrie: ArtifactTrie = intersection(t1, t2, artifactIdentifier)!!
//    val intersectionSize = intersectingTrie.vertexSet().size.toLong()
//    if (intersectionSize == 0L) {
//        return 0.0
//    }
//    val sizeT1 = t1.getNumberOfNodesTill(artifactIdentifier)
//    val sizeT2 = t2.getNumberOfNodesTill(artifactIdentifier)
//    return intersectionSize.toDouble() / max(sizeT1, sizeT2)
//}
//
//
//fun distance(t1: ArtifactTrie, t2: ArtifactTrie, artifactIdentifier: String): Double {
//    val similarity: Double = similarity(t1, t2, artifactIdentifier)
//    assert(similarity in 0.0..1.0)
//    return 1 - similarity //Math.max(0, 1 - similarity);
//}
//
//
//private fun dfs(
//        t1: ArtifactTrie, t1Node: ArtifactTrieNode, t2: ArtifactTrie, t2Node: ArtifactTrieNode, intersection: ArtifactTrie, artifactIdentifier: String
//) {
//    if (t1Node != t2Node) {
//        return
//    }
//    intersection.addVertex(t1Node.identifier, t1Node.label)
//
//    // Has a parent, so add that edge to the intersecting trie
//    val artifactTrieEdges = t1.incomingEdgesOf(t1Node)
//    val first = artifactTrieEdges.stream().findFirst() // In trees there is only one parent
//    if (first.isPresent) {
//        val artifactTrieEdge = first.get()
//        val source = t1.getEdgeSource(artifactTrieEdge)
//        //            intersection.addVertex(source.getIdentifier(), source.getLabel());
//        intersection.addEdge(source, t1Node, ArtifactTrieEdge(source, t1Node))
//    }
//    if (t1Node.label == artifactIdentifier) {
//        return
//    }
//    val outEdgesOfT1 = t1.outgoingEdgesOf(t1Node)
//    //        final Set<ArtifactTrieEdge> outEdgesOfT2 = t2.outgoingEdgesOf(t2Node);
//    for (artifactTrieEdge in outEdgesOfT1) {
//        if (t2.containsEdge(artifactTrieEdge)) {
//            val targetT1 = artifactTrieEdge.target
//            val targetT2 = t2.getEdgeTarget(artifactTrieEdge)
//            dfs(t1, targetT1, t2, targetT2, intersection, artifactIdentifier)
//        }
//    }
//}
//
//fun intersection(t1: ArtifactTrie, t2: ArtifactTrie, artifactIdentifier: String): ArtifactTrie? {
//    val t1Root = t1.root // I doesn't matter which of the two tries we choose.
//    val t2Root = t2.root
//    if (t1Root != t2Root) {
//        return null
//    }
//    val intersection = ArtifactTrie(ArtifactTrieEdge::class.java)
//    dfs(t1, t1Root, t2, t2Root, intersection, artifactIdentifier)
//    return intersection
//}
//
//fun multisetJaccard(t1: ArtifactTrie, t2: ArtifactTrie): Double {
//    val multiSetT1 = t1.vertexLabelsMultiSet
//    val multiSetT2 = t2.vertexLabelsMultiSet
//    val intersection = Multisets.intersection(multiSetT1, multiSetT2)
//
//    @Suppress("UnstableApiUsage")
//    val union = Multisets.union(multiSetT1, multiSetT2)
//    return intersection.size / union.size.toDouble()
//}
//
//fun jaccard(t1: ArtifactTrie, t2: ArtifactTrie): Double {
//    val vertexLabelsSetT1 = t1.vertexLabelsSet
//    val vertexLabelsSetT2 = t2.vertexLabelsSet
//    val intersection: Set<String?> = Sets.intersection(vertexLabelsSetT1, vertexLabelsSetT2)
//    val union: Set<String?> = Sets.union(vertexLabelsSetT1, vertexLabelsSetT2)
//    return intersection.size / union.size.toDouble()
//}
//
//
