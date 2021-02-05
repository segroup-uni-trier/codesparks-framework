package de.unitrier.st.codesparks.core.data;

/*
 * Copyright (c), Oliver Moseler, 2020
 */
public class ArtifactTrieNode
{
    private final int id;
    private final String label;
    private long cnt;

    ArtifactTrieNode(final int id, final String label)
    {
        this.id = id;
        this.label = label;
    }

    int getId()
    {
        return id;
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
        return String.valueOf(id);
    }

    @Override
    public String toString()
    {
        return id + ":" + label;
    }

    @Override
    public boolean equals(final Object obj)
    {
        if (obj == null) return false;
        if (!(obj instanceof ArtifactTrieNode)) return false;
//        if (identifier == null) return false; // the member "identifier" must never be null!
        return id == ((ArtifactTrieNode) obj).getId();//identifier.equals(((ArtifactTrieNode) obj).getIdentifier());
    }

    @Override
    public int hashCode()
    {
        return id;//identifier.hashCode();//super.hashCode();
    }
}
