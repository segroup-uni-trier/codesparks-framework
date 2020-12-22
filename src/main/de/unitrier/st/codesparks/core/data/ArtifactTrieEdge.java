package de.unitrier.st.codesparks.core.data;

import org.jgrapht.graph.DefaultEdge;

/*
 * Copyright (c), Oliver Moseler, 2020
 */
public class ArtifactTrieEdge extends DefaultEdge
{
    private final String label;
    private final ArtifactTrieNode source;
    private final ArtifactTrieNode target;

    ArtifactTrieEdge(final String label, final ArtifactTrieNode source, final ArtifactTrieNode target)
    {
        this.label = label;
        this.source = source;
        this.target = target;
    }

    public ArtifactTrieEdge(final ArtifactTrieNode source, final ArtifactTrieNode target)
    {
        this("", source, target);
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
