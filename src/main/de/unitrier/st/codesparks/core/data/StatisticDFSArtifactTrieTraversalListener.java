/*
 * Copyright (c) 2021. Oliver Moseler
 */

package de.unitrier.st.codesparks.core.data;

import org.jgrapht.event.VertexTraversalEvent;
import org.jgrapht.traverse.AbstractGraphIterator;
import org.jgrapht.traverse.DepthFirstIterator;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class StatisticDFSArtifactTrieTraversalListener extends AArtifactTrieTraversalListener
{
    private final Map<Integer, Set<ArtifactTrieNode>> nodesPerLevel;
    private final Set<ArtifactTrieNode> leafNodes;
    private final Set<ArtifactTrieNode> innerNodes;
    private int maxDepth;
    private int edgeCnt;

    public StatisticDFSArtifactTrieTraversalListener()
    {
        maxDepth = 0;
        edgeCnt = 0;
        leafNodes = new HashSet<>();
        innerNodes = new HashSet<>();
        nodesPerLevel = new HashMap<>();
    }

    private void addNodeToLevel(ArtifactTrieNode node, int level)
    {
        final Set<ArtifactTrieNode> artifactTrieNodes = nodesPerLevel.computeIfAbsent(level, integer -> new HashSet<>());
        artifactTrieNodes.add(node);
    }

    @Override
    public void vertexTraversed(final VertexTraversalEvent<ArtifactTrieNode> e)
    {
        final ArtifactTrieNode vertex = e.getVertex();
        if (graph.inDegreeOf(vertex) > 0)
        { // Don't count the root node!
            edgeCnt = edgeCnt + 1;
        }
        addNodeToLevel(vertex, edgeCnt);
        if (graph.outDegreeOf(vertex) == 0)
        {
            maxDepth = Math.max(maxDepth, edgeCnt);
            leafNodes.add(vertex);
        } else
        {
            innerNodes.add(vertex);
        }
    }

    @Override
    public void vertexFinished(final VertexTraversalEvent<ArtifactTrieNode> e)
    {
        edgeCnt = edgeCnt - 1;
    }

    public int getNrOfLeafNodes()
    {
        return leafNodes.size();
    }

    public Set<ArtifactTrieNode> getLeafNodes()
    {
        return leafNodes;
    }

    public int getNrOfInnerNodes()
    {
        return innerNodes.size();
    }

    public Set<ArtifactTrieNode> getInnerNodes()
    {
        return innerNodes;
    }

    public int getMaxDepth()
    {
        return maxDepth;
    }

    public Set<ArtifactTrieNode> getNodesOfLevel(int level)
    {
        return nodesPerLevel.getOrDefault(level, new HashSet<>());
    }

    @Override
    public AbstractGraphIterator<ArtifactTrieNode, ArtifactTrieEdge> getIterator()
    {
        return new DepthFirstIterator<>(graph);
    }
}
