package de.unitrier.st.codesparks.core.data
//
//import org.jgrapht.graph.DefaultDirectedGraph
//
//
//class ArtifactTrie(edgeClass: Class<out ArtifactTrieEdge>) : DefaultDirectedGraph<ArtifactTrieNode, ArtifactTrieEdge>(edgeClass) {
//
//    var root: ArtifactTrieNode? = null
//
//    init {
//        val rootIdentifier = "root"
//        root = ArtifactTrieNode(rootIdentifier, rootIdentifier)
//        super.addVertex(root)
//    }
//
//    fun addVertex(identifier: String, label: String): ArtifactTrieNode {
//        val newTrieNode = ArtifactTrieNode(identifier, label)
//        val b = addVertex(newTrieNode)
//        if (b) {
//            return newTrieNode
//        }
//        return vertexSet().stream().filter { trieNode -> trieNode.identifier == identifier }.findFirst().orElseThrow()
//    }
//
//}