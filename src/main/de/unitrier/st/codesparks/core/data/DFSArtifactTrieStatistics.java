/*
 * Copyright (c) 2021. Oliver Moseler
 */

package de.unitrier.st.codesparks.core.data;

import java.util.HashSet;
import java.util.Set;

@Deprecated
public class DFSArtifactTrieStatistics
{
    private final ArtifactTrie trie;
    private long maxDepth;
    private final Set<ArtifactTrieNode> leafNodes;
    private final Set<ArtifactTrieNode> innerNodes;

    public DFSArtifactTrieStatistics(final ArtifactTrie trie)
    {
        this.trie = trie;
        leafNodes = new HashSet<>();
        innerNodes = new HashSet<>();
        maxDepth = 0;

        iterate(trie.getRoot(), 0);
    }

    private void iterate(final ArtifactTrieNode node, final int depth)
    {
        if (trie.outDegreeOf(node) == 0)
        {
            maxDepth = Math.max(maxDepth, depth);
            leafNodes.add(node);
            return;
        }
        innerNodes.add(node);
        final Set<ArtifactTrieEdge> outEdges = trie.outgoingEdgesOf(node);
        for (final ArtifactTrieEdge edge : outEdges)
        {
            final ArtifactTrieNode target = trie.getEdgeTarget(edge);
            iterate(target, depth + 1);
        }
    }

    public long getMaxDepth()
    {
        return maxDepth;
    }

    public long getNrOfLeafNodes()
    {
        return leafNodes.size();
    }

    public long getNrOfInnerNodes()
    {
        return innerNodes.size();
    }
}
