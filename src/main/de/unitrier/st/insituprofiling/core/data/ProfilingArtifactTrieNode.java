package de.unitrier.st.insituprofiling.core.data;

import org.jetbrains.annotations.NotNull;

public class ProfilingArtifactTrieNode
{
    private final String identifier;
    private final String label;
    private long cnt;

    ProfilingArtifactTrieNode(@NotNull String identifier, @NotNull String label)
    {
        this.identifier = identifier;
        this.label = label;
    }

    synchronized void inc()
    {
        cnt++;
    }

    @NotNull
    public String getIdentifier()
    {
        return identifier;
    }

    public synchronized String getLabel()
    {
        return String.format("%s (%d)", label, cnt);
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
        if (!(obj instanceof ProfilingArtifactTrieNode)) return false;
//        if (identifier == null) return false; // the member "identifier" must never be null!
        return identifier.equals(((ProfilingArtifactTrieNode) obj).getIdentifier());
//        return super.equals(obj);
    }

    @Override
    public int hashCode()
    {
        return identifier.hashCode();//super.hashCode();
    }
}
