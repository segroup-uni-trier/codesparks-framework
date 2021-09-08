/*
 * Copyright (c) 2021. Oliver Moseler
 */
package de.unitrier.st.codesparks.core.data;

public class ArtifactTrieNode
{
    private final int pathId;
    private final String nodeId;
    private final String label;
    private long cnt;

    ArtifactTrieNode(final int pathId, final String nodeId, final String label)
    {
        this.pathId = pathId;
        this.nodeId = nodeId;
        this.label = label;
    }

    int getPathId()
    {
        return pathId;
    }

    String getNodeId()
    {
        return nodeId;
    }

    private final Object cntLock = new Object();

    final void inc()
    {
        synchronized (cntLock)
        {
            cnt++;
        }
    }

    /**
     * This method is only used for testing purposes.
     *
     * @return The actual satellite data 'cnt' associated with the node. It represents the number of times that node occurs in the respective calling context.
     */
    public final long getCnt()
    {
        synchronized (cntLock)
        {
            return cnt;
        }
    }

    public final String getMetricLabel()
    {
        synchronized (cntLock)
        {
            return String.format("%s (%d)", label, cnt);
        }
    }

    public String getLabel()
    {
        return label;
    }

    public String getIdString()
    {
        return String.valueOf(pathId);
    }

    @Override
    public String toString()
    {
        return pathId + ":" + label;
    }

    @Override
    public boolean equals(final Object obj)
    {
        if (obj == null) return false;
        if (!(obj instanceof ArtifactTrieNode)) return false;
//        if (identifier == null) return false; // the member "identifier" must never be null!
        return pathId == ((ArtifactTrieNode) obj).getPathId();//identifier.equals(((ArtifactTrieNode) obj).getIdentifier());
    }

    @Override
    public int hashCode()
    {
        return pathId;//identifier.hashCode();//super.hashCode();
    }
}
