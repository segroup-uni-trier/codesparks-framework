/*
 * Copyright (c), Oliver Moseler, 2021
 */

package de.unitrier.st.codesparks.core.data;

import com.google.common.collect.HashMultiset;
import com.google.common.collect.Multiset;
import org.jdom2.Element;
import org.jgrapht.Graph;
import org.jgrapht.graph.DefaultDirectedGraph;
import org.jgrapht.traverse.AbstractGraphIterator;
import org.jgrapht.util.TypeUtil;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public class ArtifactTrie extends DefaultDirectedGraph<ArtifactTrieNode, ArtifactTrieEdge>
{
    private ArtifactTrieNode root;

    public ArtifactTrie(final Class<? extends ArtifactTrieEdge> edgeClass)
    {
        super(edgeClass);
        final String rootIdentifier = "root";
        root = new ArtifactTrieNode(rootIdentifier, rootIdentifier);
        super.addVertex(root);
    }

    public ArtifactTrieNode addVertex(final String identifier, final String label)
    {
        final ArtifactTrieNode node = new ArtifactTrieNode(identifier, label);
        final boolean b = addVertex(node);
        if (b)
        {
            return node;
        }
        return vertexSet().stream().filter(trieNode -> trieNode.getIdentifier().equals(identifier)).findFirst().orElseThrow();
    }

    @Override
    public boolean removeVertex(final ArtifactTrieNode trieNode)
    {
        final boolean b = super.removeVertex(trieNode);
        if (b)
        {
            final String identifier = trieNode.getIdentifier();
            if (root != null && root.getIdentifier().equals(identifier))
            {
                root = null;
            }
        }
        return b;
    }

    @SuppressWarnings("unused")
    public ArtifactTrieNode getRoot()
    {
        return root;
    }

    private final Object trieLock = new Object();

    public void add(final List<Element> methods)
    {
        final String rootStr = "root";
        final StringBuilder strb = new StringBuilder(rootStr);
        synchronized (trieLock)
        {
            ArtifactTrieNode current = addVertex(rootStr, rootStr);
            for (int i = methods.size() - 1; i > -1; i--)
            {
                current.inc();
                Element method = methods.get(i);
                String methodName = removeWhiteSpace(method.getText());
                strb.append(methodName);
                String identifier = removeWhiteSpace(strb.toString()
                        .replaceAll("<", "")
                        .replaceAll(">", "")
                        .replaceAll("\\$", ""));
                ArtifactTrieNode node = addVertex(identifier, methodName);
                ArtifactTrieEdge edge = new ArtifactTrieEdge(current, node);
                this.addEdge(current, node, edge);
                current = node;
            }
        }
    }

    private String removeWhiteSpace(final String str)
    {
        return str.replaceAll("\\n", "")
                .replaceAll(" ", "")
                .trim();
    }

    public void export(final IArtifactTrieExportStrategy strategy)
    {
        strategy.export(this);
    }

    @Override
    public boolean containsEdge(final ArtifactTrieEdge artifactTrieEdge)
    {
        return super.containsEdge(artifactTrieEdge.getSource(), artifactTrieEdge.getTarget());
    }

    @Override
    public boolean equals(final Object obj)
    {   // Mostly copied from class org.jgrapht.graph.AbstractGraph<V, E>
        if (this == obj)
        {
            return true;
        }
        if ((obj == null) || (getClass() != obj.getClass()))
        {
            return false;
        }

        Graph<ArtifactTrieNode, ArtifactTrieEdge> g = TypeUtil.uncheckedCast(obj);

        if (!vertexSet().equals(g.vertexSet()))
        {
            return false;
        }
        if (edgeSet().size() != g.edgeSet().size())
        {
            return false;
        }

        for (ArtifactTrieEdge e : edgeSet())
        {
            ArtifactTrieNode source = getEdgeSource(e);
            ArtifactTrieNode target = getEdgeTarget(e);

            // This failed
//            if (!g.containsEdge(e))
//            {
//                return false;
//            }
            // Replaced by this! Also over wrote the "containsEdge" method with this. See above!
            if (!g.containsEdge(source, target))
            {
                return false;
            }

            if (!g.getEdgeSource(e).equals(source) || !g.getEdgeTarget(e).equals(target))
            {
                return false;
            }

            if (Math.abs(getEdgeWeight(e) - g.getEdgeWeight(e)) > 10e-7)
            {
                return false;
            }
        }

        return true;
    }

    public Set<String> getVertexLabelsSet()
    {
        return vertexSet().stream().map(ArtifactTrieNode::getLabel).collect(Collectors.toSet());
    }

    public Multiset<String> getVertexLabelsMultiSet()
    {
        final Multiset<String> multiset = HashMultiset.create();
        final Set<ArtifactTrieNode> artifactTrieNodes = vertexSet();
        for (final ArtifactTrieNode artifactTrieNode : artifactTrieNodes)
        {
            multiset.add(artifactTrieNode.getLabel());
        }
        return multiset;
    }

    public void traverse(final AArtifactTrieTraversalListener traversalListener)
    {
        final AbstractGraphIterator<ArtifactTrieNode, ArtifactTrieEdge> iterator = traversalListener.getIterator(this);
        iterator.addTraversalListener(traversalListener);
        while (iterator.hasNext())
        {
            iterator.next();
        }
    }

    private long getNumberOfNodesOfSubtreeIncludingOtherPaths(final ArtifactTrieNode node, final String artifactIdentifier)
    {
        if (node == null)
        {
            return 0;
        }
        if (node.getLabel().equals(artifactIdentifier))
        {
            return 1;
        }
        final Set<ArtifactTrieEdge> outEdges = outgoingEdgesOf(node);
        if (outEdges.size() == 0)
        { // Is leaf node and is not equal to artifactIdentifier
            return 1;
        }
        long edgeSum = 0;
        for (final ArtifactTrieEdge artifactTrieEdge : outEdges)
        {
            final ArtifactTrieNode target = artifactTrieEdge.getTarget();
            final long edgeCnt = getNumberOfNodesOfSubtreeIncludingOtherPaths(target, artifactIdentifier);
            edgeSum += edgeCnt;
        }
        long ret = edgeSum;
        if (edgeSum > 0)
        { // if any of the outgoing edges produced a value greater than zero, the current node is on a path to a node with label equal to
            // 'artifactIdentifier' and thus has to be counted as well
            ret += 1;
        }
        return ret;
    }

    private long getNumberOfNodesOfSubtree(final ArtifactTrieNode node, final String artifactIdentifier)
    {
        if (node == null)
        {
            return 0;
        }
        if (node.getLabel().equals(artifactIdentifier))
        {
            return 1;
        }
        final Set<ArtifactTrieEdge> outEdges = outgoingEdgesOf(node);
        if (outEdges.size() == 0)
        { // Is leaf node and is not equal to artifactIdentifier
            return 0;
        }
        long edgeSum = 0;
        for (final ArtifactTrieEdge artifactTrieEdge : outEdges)
        {
            final ArtifactTrieNode target = artifactTrieEdge.getTarget();
            final long edgeCnt = getNumberOfNodesOfSubtree(target, artifactIdentifier);
            edgeSum += edgeCnt;
        }
        long ret = edgeSum;
        if (edgeSum > 0)
        { // if any of the outgoing edges produced a value greater than zero, the current node is on a path to a node with label equal to
            // 'artifactIdentifier' and thus has to be counted as well
            ret += 1;
        }
        return ret;
    }

    /**
     * Computes the number of nodes of the subtree which consists of minimal paths from the 'root' to nodes with label
     * 'artifactIdentifier' and all other paths which do not contain any node with label 'artifactIdentifier'. The 'root' node can already have label
     * 'artifactIdentifier' and thus, paths of length 0 are considered.
     *
     * @param artifactIdentifier The label of the node where the paths must end.
     * @return The number of nodes of the resulting subtree.
     */
    public long getNumberOfNodesOfSubtreeIncludingOtherPaths(final String artifactIdentifier)
    {
        final ArtifactTrieNode root = getRoot();
        //noinspection UnnecessaryLocalVariable
        final long nodesTill = getNumberOfNodesOfSubtreeIncludingOtherPaths(root, artifactIdentifier);
        return nodesTill;
    }

    /**
     * Computes the number of nodes of the subtree which consists solely of minimal paths from the 'root' to nodes with label
     * 'artifactIdentifier'. The 'root' node can already have label 'artifactIdentifier' and thus, paths of length 0 are considered.
     *
     * @param artifactIdentifier The label of the node where the paths must end.
     * @return The number of nodes of the resulting subtree.
     */
    public long getNumberOfNodesOfSubtree(final String artifactIdentifier)
    {
        final ArtifactTrieNode root = getRoot();
        //noinspection UnnecessaryLocalVariable
        final long nodesTill = getNumberOfNodesOfSubtree(root, artifactIdentifier);
        return nodesTill;
    }

}
