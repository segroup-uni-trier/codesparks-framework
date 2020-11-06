package de.unitrier.st.codesparks.core;

import de.unitrier.st.codesparks.core.data.*;

public abstract class AProfilingResult implements IProfilingResult
{
    private AProfilingArtifact globalArtifact;

    public void export(AProfilingResultExportStrategy strategy)
    {
        strategy.export(this);
    }

    protected final Object globalArtifactLock = new Object();

    @Override
    public void setGlobalArtifact(AProfilingArtifact globalArtifact)
    {
        synchronized (globalArtifactLock)
        {
            this.globalArtifact = globalArtifact;
        }
    }

    @Override
    public AProfilingArtifact getGlobalArtifact()
    {
        synchronized (globalArtifactLock)
        {
            return globalArtifact;
        }
    }

    private final ProfilingArtifactTrie profilingArtifactTrie;

    protected AProfilingResult()
    {
        profilingArtifactTrie = new ProfilingArtifactTrie(ProfilingArtifactTrieEdge.class);
    }

    @Override
    public ProfilingArtifactTrie getProfilingArtifactTrie()
    {
        return profilingArtifactTrie;
    }

//    protected final void applyFilter(AProfilingArtifact artifact, IThreadArtifactFilter threadArtifactFilter)
//    {
//        threadArtifacts.forEach(threadArtifact -> threadArtifact.setFiltered(threadArtifactFilter.filterThreadArtifact(threadArtifact)));
//    }

    @Override
    public void applyThreadArtifactFilter(IThreadArtifactFilter threadArtifactFilter)
    {
        synchronized (globalArtifactLock)
        {
            AProfilingArtifact globalArtifact = getGlobalArtifact();
            if (globalArtifact != null)
            {
//                applyFilter(globalArtifact.getThreadArtifacts(), threadArtifactFilter);
                globalArtifact.applyThreadArtifactFilter(threadArtifactFilter);
            }
        }
    }
}
