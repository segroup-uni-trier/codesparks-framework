package de.unitrier.st.codesparks.core.data;

import org.jdom2.Element;
import org.jgrapht.Graph;
import org.jgrapht.graph.DefaultDirectedGraph;
import org.jgrapht.traverse.AbstractGraphIterator;
import org.jgrapht.util.TypeUtil;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/*
 * Copyright (c), Oliver Moseler, 2020
 */
public class ArtifactTrie extends DefaultDirectedGraph<ArtifactTrieNode, ArtifactTrieEdge> implements ITrieVisitable
{
    private final ArtifactTrieNode root;
    private final Map<String, ArtifactTrieNode> nodes;

    public ArtifactTrie(final Class<? extends ArtifactTrieEdge> edgeClass)
    {
        super(edgeClass);
        nodes = new HashMap<>();
        final String rootIdentifier = "root";
        this.root = new ArtifactTrieNode(rootIdentifier, rootIdentifier);
        nodes.put(rootIdentifier, this.root);
        addVertex(this.root);
    }

    private ArtifactTrieNode getNode(final String identifier, final String label)
    {
        ArtifactTrieNode node = nodes.get(identifier);
        if (node == null)
        {
            node = new ArtifactTrieNode(identifier, label);
            nodes.put(identifier, node);
        }
        node.inc();
        return node;
    }

    public ArtifactTrieNode addVertex(final String identifier, final String label)
    {
        ArtifactTrieNode node = getNode(identifier, label);
        addVertex(node);
        return node;
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

    @Override
    public void accept(final ATrieVisitorAdapter trieVisitor)
    {
        final AbstractGraphIterator<ArtifactTrieNode, ArtifactTrieEdge> iterator = trieVisitor.getIterator(this);
        iterator.addTraversalListener(trieVisitor);
        while (iterator.hasNext())
        {
            iterator.next();
        }
    }
}
