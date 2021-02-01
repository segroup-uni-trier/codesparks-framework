/*
 * Copyright (c) 2021. Oliver Moseler
 */

package de.unitrier.st.codesparks.core.data;

import org.jgrapht.Graph;
import org.jgrapht.event.ConnectedComponentTraversalEvent;
import org.jgrapht.event.EdgeTraversalEvent;
import org.jgrapht.event.TraversalListener;
import org.jgrapht.event.VertexTraversalEvent;
import org.jgrapht.traverse.AbstractGraphIterator;

public abstract class AArtifactTrieTraversalListener implements TraversalListener<ArtifactTrieNode, ArtifactTrieEdge>
{
    protected Graph<ArtifactTrieNode, ArtifactTrieEdge> graph;

    final void setGraph(final Graph<ArtifactTrieNode, ArtifactTrieEdge> graph)
    {
        this.graph = graph;
    }

    abstract AbstractGraphIterator<ArtifactTrieNode, ArtifactTrieEdge> getIterator(final Graph<ArtifactTrieNode, ArtifactTrieEdge> graph);

    @Override
    public void connectedComponentFinished(final ConnectedComponentTraversalEvent e) { }

    @Override
    public void connectedComponentStarted(final ConnectedComponentTraversalEvent e) { }

    @Override
    public void edgeTraversed(final EdgeTraversalEvent<ArtifactTrieEdge> e) { }

    @Override
    public void vertexFinished(final VertexTraversalEvent<ArtifactTrieNode> e) { }
}
