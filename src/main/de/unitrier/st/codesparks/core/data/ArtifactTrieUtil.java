/*
 * Copyright (c) 2021. Oliver Moseler
 */

package de.unitrier.st.codesparks.core.data;

import com.google.common.collect.Multiset;
import com.google.common.collect.Multisets;
import com.google.common.collect.Sets;

import java.util.Set;

public final class ArtifactTrieUtil
{
    private ArtifactTrieUtil() {}

    public static double multisetJaccard(final ArtifactTrie t1, final ArtifactTrie t2)
    {
        final Multiset<String> multiSetT1 = t1.getVertexLabelsMultiSet();
        final Multiset<String> multiSetT2 = t2.getVertexLabelsMultiSet();

        final Multiset<String> intersection = Multisets.intersection(multiSetT1, multiSetT2);

        //noinspection UnstableApiUsage
        final Multiset<String> union = Multisets.union(multiSetT1, multiSetT2);

        //noinspection UnnecessaryLocalVariable
        double ret = intersection.size() / (double) union.size();

        return ret;
    }

    public static double jaccard(final ArtifactTrie t1, final ArtifactTrie t2)
    {
        final Set<String> vertexLabelsSetT1 = t1.getVertexLabelsSet();
        final Set<String> vertexLabelsSetT2 = t2.getVertexLabelsSet();

        final Set<String> intersection = Sets.intersection(vertexLabelsSetT1, vertexLabelsSetT2);

        final Set<String> union = Sets.union(vertexLabelsSetT1, vertexLabelsSetT2);

        //noinspection UnnecessaryLocalVariable
        double ret = intersection.size() / (double) union.size();

        return ret;
    }

    private static int dfs(
            final ArtifactTrie t1
            , final ArtifactTrieNode t1Node
            , final ArtifactTrie t2
            , final ArtifactTrieNode t2Node
            , final ArtifactTrie intersection
            , final String artifactIdentifier
    )
    {
        if (!t1Node.equals(t2Node))
        {
            return 0;
        }
        if (t1Node.getNodeId().equals(artifactIdentifier))
        {
            return 1;
        }

        final Set<ArtifactTrieEdge> outEdgesOfT1 = t1.outgoingEdgesOf(t1Node);
        int ret = 0;
        for (final ArtifactTrieEdge artifactTrieEdge : outEdgesOfT1)
        {
            if (t2.containsEdge(artifactTrieEdge))
            {
                final ArtifactTrieNode targetT1 = artifactTrieEdge.getTarget();
                final ArtifactTrieNode targetT2 = t2.getEdgeTarget(artifactTrieEdge);
                final int childValue = dfs(t1, targetT1, t2, targetT2, intersection, artifactIdentifier);
                if (childValue > 0)
                {
                    ret = 1;
                    intersection.addVertex(t1Node.getPathId(), t1Node.getNodeId(), t1Node.getLabel());
                    intersection.addVertex(targetT1.getPathId(), t1Node.getNodeId(), targetT1.getLabel());
                    intersection.addEdge(t1Node, targetT1, new ArtifactTrieEdge(t1Node, targetT1));
                }
            }
        }
        return ret;
    }

    public static ArtifactTrie intersection(final ArtifactTrie t1, final ArtifactTrie t2, final String artifactIdentifier)
    {
        final ArtifactTrieNode t1Root = t1.getRoot(); // I doesn't matter which of the two tries we choose.
        final ArtifactTrieNode t2Root = t2.getRoot();
        if (!t1Root.equals(t2Root))
        {
            return null;
        }

        ArtifactTrie intersection = new ArtifactTrie(ArtifactTrieEdge.class, false);

        dfs(t1, t1Root, t2, t2Root, intersection, artifactIdentifier);

        return intersection;
    }

    public static double similarity(final ArtifactTrie t1, final ArtifactTrie t2, final String artifactIdentifier)
    {
        if (t1 == null || t2 == null)
        {
            return 0D;
        }

        final ArtifactTrie intersectingTrie = intersection(t1, t2, artifactIdentifier);

        if (intersectingTrie == null)
        {
            return 0D;
        }

        final long intersectionSize = intersectingTrie.vertexSet().size();

        if (intersectionSize == 0)
        {
            return 0D;
        }

        final long sizeT1 = t1.getNumberOfNodesOfSubtree(artifactIdentifier);
        final long sizeT2 = t2.getNumberOfNodesOfSubtree(artifactIdentifier);

        //noinspection UnnecessaryLocalVariable
        final double similarity = (double) intersectionSize / Math.max(sizeT1, sizeT2);

        return similarity;
    }

    public static double distance(final ArtifactTrie t1, final ArtifactTrie t2, final String artifactIdentifier)
    {
        final double similarity = similarity(t1, t2, artifactIdentifier);
        assert similarity >= 0D && similarity <= 1D;
        return 1 - similarity;//Math.max(0, 1 - similarity);
    }
}
