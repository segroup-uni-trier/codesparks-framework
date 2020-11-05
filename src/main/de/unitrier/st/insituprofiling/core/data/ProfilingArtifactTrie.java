package de.unitrier.st.insituprofiling.core.data;

import org.jdom2.Element;
import org.jgrapht.Graph;
import org.jgrapht.graph.DefaultDirectedGraph;
import org.jgrapht.util.TypeUtil;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ProfilingArtifactTrie extends DefaultDirectedGraph<ProfilingArtifactTrieNode, ProfilingArtifactTrieEdge>
{
    private final ProfilingArtifactTrieNode root;
    private final Map<String, ProfilingArtifactTrieNode> nodes;

    public ProfilingArtifactTrie(Class<? extends ProfilingArtifactTrieEdge> edgeClass)
    {
        super(edgeClass);
        nodes = new HashMap<>();
        root = new ProfilingArtifactTrieNode("root", "root");
        nodes.put("root", root);
        addVertex(root);
    }

    private ProfilingArtifactTrieNode getNode(String identifier, String label)
    {
        ProfilingArtifactTrieNode node = nodes.get(identifier);
        if (node == null)
        {
            node = new ProfilingArtifactTrieNode(identifier, label);
            nodes.put(identifier, node);
        }
        node.inc();
        return node;
    }

    public ProfilingArtifactTrieNode addVertex(String identifier, String label)
    {
        ProfilingArtifactTrieNode node = getNode(identifier, label);
        addVertex(node);
        return node;
    }

    @SuppressWarnings("unused")
    public ProfilingArtifactTrieNode getRoot()
    {
        return root;
    }

    public synchronized void add(List<Element> methods)
    {
        String rootStr = "root";
        ProfilingArtifactTrieNode current = addVertex(rootStr, rootStr);
        StringBuilder strb = new StringBuilder(rootStr);
        for (int i = methods.size() - 1; i > -1; i--)
        {
            Element method = methods.get(i);
            String methodName = removeWhiteSpace(method.getText());
            strb.append(methodName);
            String identifier = removeWhiteSpace(strb.toString()
                    .replaceAll("<", "")
                    .replaceAll(">", "")
                    .replaceAll("\\$", ""));
            ProfilingArtifactTrieNode node = addVertex(identifier, methodName);
            ProfilingArtifactTrieEdge edge = new ProfilingArtifactTrieEdge(current, node);
            this.addEdge(current, node, edge);
            current = node;
        }
    }

    private String removeWhiteSpace(String str)
    {
        return str.replaceAll("\\n", "")
                .replaceAll(" ", "")
                .trim();
    }

    public void export(IProfilingArtifactTrieExportStrategy strategy)
    {
        strategy.export(this);
    }

    @Override
    public boolean containsEdge(ProfilingArtifactTrieEdge profilingArtifactTrieEdge)
    {
        return super.containsEdge(profilingArtifactTrieEdge.getSource(), profilingArtifactTrieEdge.getTarget());
    }

    @Override
    public boolean equals(Object obj)
    {   // Mostly copied from class org.jgrapht.graph.AbstractGraph<V, E>
        if (this == obj)
        {
            return true;
        }
        if ((obj == null) || (getClass() != obj.getClass()))
        {
            return false;
        }

        Graph<ProfilingArtifactTrieNode, ProfilingArtifactTrieEdge> g = TypeUtil.uncheckedCast(obj);

        if (!vertexSet().equals(g.vertexSet()))
        {
            return false;
        }
        if (edgeSet().size() != g.edgeSet().size())
        {
            return false;
        }

        for (ProfilingArtifactTrieEdge e : edgeSet())
        {
            ProfilingArtifactTrieNode source = getEdgeSource(e);
            ProfilingArtifactTrieNode target = getEdgeTarget(e);

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
}
