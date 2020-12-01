package de.unitrier.st.codesparks.core.data;

import org.jgrapht.graph.DefaultEdge;

/*
 * Copyright (c), Oliver Moseler, 2020
 */
public class ArtifactTrieEdge extends DefaultEdge
{
    private String label;
    private ArtifactTrieNode source;
    private ArtifactTrieNode target;

    private void init(String label, ArtifactTrieNode source, ArtifactTrieNode target)
    {
        this.label = label;
        this.source = source;
        this.target = target;
    }

    ArtifactTrieEdge(String label, ArtifactTrieNode source, ArtifactTrieNode target)
    {
        init(label, source, target);
    }

    public ArtifactTrieEdge(ArtifactTrieNode source, ArtifactTrieNode target)
    {
        init("", source, target);
    }

    String getLabel()
    {
        return label;
    }

    @Override
    public ArtifactTrieNode getSource()
    {
        return source;
    }

    @Override
    public ArtifactTrieNode getTarget()
    {
        return target;
    }
}
