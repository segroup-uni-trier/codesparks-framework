package de.unitrier.st.insituprofiling.core.data;

import org.jgrapht.graph.DefaultEdge;

public class ProfilingArtifactTrieEdge extends DefaultEdge
{
    private String label;
    private ProfilingArtifactTrieNode source;
    private ProfilingArtifactTrieNode target;

    private void init(String label, ProfilingArtifactTrieNode source, ProfilingArtifactTrieNode target)
    {
        this.label = label;
        this.source = source;
        this.target = target;
    }

    ProfilingArtifactTrieEdge(String label, ProfilingArtifactTrieNode source, ProfilingArtifactTrieNode target)
    {
        init(label, source, target);
    }

    public ProfilingArtifactTrieEdge(ProfilingArtifactTrieNode source, ProfilingArtifactTrieNode target)
    {
        init("", source, target);
    }

    String getLabel()
    {
        return label;
    }

    @Override
    public ProfilingArtifactTrieNode getSource()
    {
        return source;
    }

    @Override
    public ProfilingArtifactTrieNode getTarget()
    {
        return target;
    }
}
