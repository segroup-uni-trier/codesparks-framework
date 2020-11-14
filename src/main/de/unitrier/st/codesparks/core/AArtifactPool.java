package de.unitrier.st.codesparks.core;

import de.unitrier.st.codesparks.core.data.*;

import java.util.*;

public abstract class AArtifactPool implements IArtifactPool
{
    private IArtifactClassDisplayNameProvider artifactClassDisplayNameProvider;

    public void registerArtifactClassDisplayNameProvider(IArtifactClassDisplayNameProvider artifactClassDisplayNameProvider)
    {
        this.artifactClassDisplayNameProvider = artifactClassDisplayNameProvider;
    }

    protected AArtifactPool()
    {
        artifacts = new HashMap<>();
        artifactTrie = new ArtifactTrie(ArtifactTrieEdge.class);
    }

    protected AArtifactPool(IArtifactClassDisplayNameProvider artifactClassDisplayNameProvider)
    {
        this();
        this.artifactClassDisplayNameProvider = artifactClassDisplayNameProvider;
    }

    /*

     */

    protected final Object programArtifactLock = new Object();

    private AArtifact programArtifact;

    @Override
    public void setProgramArtifact(AArtifact programArtifact)
    {
        synchronized (programArtifactLock)
        {
            this.programArtifact = programArtifact;
        }
    }

    @Override
    public AArtifact getProgramArtifact()
    {
        synchronized (programArtifactLock)
        {
            return programArtifact;
        }
    }

    /*

     */

    protected final Object artifactsLock = new Object();

    private final Map<Class<? extends AArtifact>, Map<String, AArtifact>> artifacts;

    @Override
    public List<AArtifact> getArtifacts(Class<? extends AArtifact> artifactClass)
    {
        synchronized (artifactsLock)
        {
            Map<String, AArtifact> artifactsOfClassMap = artifacts.computeIfAbsent(artifactClass, k -> new HashMap<>());

            Collection<AArtifact> artifacts = artifactsOfClassMap.values();

            return new ArrayList<>(artifacts);
        }
    }

    @Override
    public AArtifact getArtifact(final String identifier)
    {
        if (identifier == null || "".equals(identifier))
        {
            return null;
        }
        synchronized (artifactsLock)
        {
            for (Map<String, AArtifact> value : artifacts.values())
            {
                AArtifact artifact = value.get(identifier);

                if (artifact != null)
                {
                    return artifact;
                }
            }
            return null;
        }
    }

    @Override
    public AArtifact getArtifact(Class<? extends AArtifact> artifactClass, String identifier)
    {
        synchronized (artifactsLock)
        {
            Map<String, AArtifact> artifactsOfClassMap = artifacts.computeIfAbsent(artifactClass, k -> new HashMap<>());

            //noinspection UnnecessaryLocalVariable
            AArtifact artifact = artifactsOfClassMap.get(identifier);

            return artifact;
        }
    }

    @Override
    public void addArtifact(AArtifact artifact)
    {
        synchronized (artifactsLock)
        {
            Map<String, AArtifact> artifactsOfClassMap = artifacts.computeIfAbsent(artifact.getClass(), k -> new HashMap<>());

            artifactsOfClassMap.put(artifact.getIdentifier(), artifact);
        }
    }

    private final ArtifactTrie artifactTrie;

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
    public Map<String, List<AArtifact>> getNamedArtifactTypeLists()
    {
        Map<String, List<AArtifact>> map = new HashMap<>();
        synchronized (artifactsLock)
        {
            for (Map.Entry<Class<? extends AArtifact>, Map<String, AArtifact>> classMapEntry : artifacts.entrySet())
            {
                Class<? extends AArtifact> artifactClass = classMapEntry.getKey();
                String classDisplayName;
                if (artifactClassDisplayNameProvider == null)
                {
                    classDisplayName = artifactClass.getTypeName();
                } else
                {
                    classDisplayName = artifactClassDisplayNameProvider.getDisplayName(artifactClass);
                }
                map.put(classDisplayName, new ArrayList<>(classMapEntry.getValue().values()));
            }
        }
        return map;
    }

    @Override
    public void applyThreadFilter(final ICodeSparksThreadFilter threadFilter)
    {
        if (threadFilter == null)
        {
            return;
        }
        synchronized (programArtifactLock)
        {
            AArtifact programArtifact = getProgramArtifact();
            if (programArtifact != null)
            {
//                applyFilter(programArtifact.getThreadArtifacts(), threadArtifactFilter);
                programArtifact.applyThreadFilter(threadFilter);
            }
        }

        synchronized (artifactsLock)
        {
            Collection<Map<String, AArtifact>> values = artifacts.values();

            for (Map<String, AArtifact> value : values)
            {
                value.values().forEach(profilingMethod -> {
                    profilingMethod.applyThreadFilter(threadFilter);
                    profilingMethod.getSuccessorsList().forEach(aNeighborProfilingArtifact
                            -> aNeighborProfilingArtifact.applyThreadFilter(threadFilter));
                    profilingMethod.getPredecessorsList().forEach(aNeighborProfilingArtifact
                            -> aNeighborProfilingArtifact.applyThreadFilter(threadFilter));
                });
            }
        }
    }

    @Override
    public void export(IArtifactPoolExportStrategy strategy)
    {
        strategy.export(this);
    }
}
