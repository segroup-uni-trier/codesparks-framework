package de.unitrier.st.codesparks.core.data;

import org.jetbrains.annotations.NotNull;

/*
 * Copyright (c), Oliver Moseler, 2020
 */
public class ArtifactTrieNode
{
    private final String identifier;
    private final String label;
    private long cnt;

    ArtifactTrieNode(@NotNull String identifier, @NotNull String label)
    {
        this.identifier = identifier;
        this.label = label;
    }

    private final Object cntLock = new Object();

    void inc()
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
    public long getCnt()
    {
        synchronized (cntLock)
        {
            return cnt;
        }
    }

    @NotNull
    public String getIdentifier()
    {
        return identifier;
    }

    public String getLabel()
    {
        synchronized (cntLock)
        {
            return String.format("%s (%d)", label, cnt);
        }
    }

    @Override
    public String toString()
    {
        return identifier;
    }

    @Override
    public boolean equals(Object obj)
    {
        if (obj == null) return false;
        if (!(obj instanceof ArtifactTrieNode)) return false;
//        if (identifier == null) return false; // the member "identifier" must never be null!
        return identifier.equals(((ArtifactTrieNode) obj).getIdentifier());
//        return super.equals(obj);
    }

    @Override
    public int hashCode()
    {
        return identifier.hashCode();//super.hashCode();
    }
}
