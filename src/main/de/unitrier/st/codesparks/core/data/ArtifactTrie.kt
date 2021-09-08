/*
 * Copyright (c) 2021. Oliver Moseler
 */
package de.unitrier.st.codesparks.core.data

//import org.jdom2.Element
//import org.jgrapht.graph.DefaultDirectedGraph
//
//private const val rootLabel: String = "root"
//private val rootId: Int = rootLabel.hashCode()
//
//class ArtifactTrie(edgeClass: Class<out ArtifactTrieEdge>) : DefaultDirectedGraph<ArtifactTrieNode, ArtifactTrieEdge>(edgeClass) {
//
//    var root: ArtifactTrieNode? = null
//
//    init {
//        root = ArtifactTrieNode(rootLabel.hashCode(), rootLabel)
//        super.addVertex(root)
//    }
//
//    fun addVertex(id: Int, label: String): ArtifactTrieNode {
//        val first = vertexSet().stream().filter { trieNode -> trieNode.id == id }.findFirst()
//        if (first.isEmpty) {
//            val artifactTrieNode = ArtifactTrieNode(id, label)
//            super.addVertex(artifactTrieNode)
//            return artifactTrieNode
//        }
//        return first.get()
//    }
//
//    override fun removeVertex(trieNode: ArtifactTrieNode?): Boolean {
//        val b = super.removeVertex(trieNode)
//        if (b) {
//            val id = trieNode?.id
//            if (root?.id == id) {
//                root = null
//            }
//        }
//        return b
//    }
//
//    private val trieLock: Any = Object()
//
//    fun add(methods: List<Element>) {
//        val strb = StringBuilder(rootLabel)
//        synchronized(trieLock)
//        {
//            var current: ArtifactTrieNode = root ?: addVertex(rootId, rootLabel)
//            for (i in methods.size - 1 downTo 0 step 1)
//            {
//                current.inc()
//                val method = methods[i]
//                val methodName = removeWhiteSpace(method.text)
//                strb.append(methodName)
//                var rawIdentifier = strb.toString()
//                rawIdentifier = rawIdentifier.replace(Regex("[<>\\$]")) {""}
//                val id = rawIdentifier.hashCode()
//                val node = addVertex(id, methodName)
//                val edge = ArtifactTrieEdge(current, node)
//                addEdge(current, node, edge)
//                current = node
//            }
//            current.inc()
//        }
//    }
//
//    private fun removeWhiteSpace(str: String): String {
//        return str.replace(Regex("[\\n]|[ ]")) { "" }.trim()
//    }
//
//}