package de.unitrier.st.codesparks.core;

import de.unitrier.st.codesparks.core.data.*;

public abstract class AArtifactPool implements IArtifactPool
{
    private AArtifact globalArtifact;

    public void export(AArtifactPoolExportStrategy strategy)
    {
        strategy.export(this);
    }

    protected final Object globalArtifactLock = new Object();

    @Override
    public void setProgramArtifact(AArtifact globalArtifact)
    {
        synchronized (globalArtifactLock)
        {
            this.globalArtifact = globalArtifact;
        }
    }

    @Override
    public AArtifact getProgramArtifact()
    {
        synchronized (globalArtifactLock)
        {
            return globalArtifact;
        }
    }

    private final ArtifactTrie artifactTrie;

    protected AArtifactPool()
    {
        artifactTrie = new ArtifactTrie(ArtifactTrieEdge.class);
    }

    @Override
    public ArtifactTrie getArtifactTrie()
    {
        return artifactTrie;
    }

//    protected final void applyFilter(AProfilingArtifact artifact, IThreadArtifactFilter threadArtifactFilter)
//    {
//        threadArtifacts.forEach(threadArtifact -> threadArtifact.setFiltered(threadArtifactFilter.filterThreadArtifact(threadArtifact)));
//    }

    @Override
    public void applyThreadFilter(ICodeSparksThreadFilter threadFilter)
    {
        synchronized (globalArtifactLock)
        {
            AArtifact globalArtifact = getProgramArtifact();
            if (globalArtifact != null)
            {
//                applyFilter(globalArtifact.getThreadArtifacts(), threadArtifactFilter);
                globalArtifact.applyThreadFilter(threadFilter);
            }
        }
    }
}
