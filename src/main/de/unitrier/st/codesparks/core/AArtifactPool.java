package de.unitrier.st.codesparks.core;

import de.unitrier.st.codesparks.core.data.*;
import de.unitrier.st.codesparks.core.logging.CodeSparksLogger;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.*;

/*
 * Copyright (c), Oliver Moseler, 2020
 */
public abstract class AArtifactPool implements IArtifactPool
{
    protected AArtifactPool()
    {
        artifacts = new HashMap<>();
    }

    protected AArtifactPool(IArtifactClassDisplayNameProvider artifactClassDisplayNameProvider)
    {
        this();
        this.artifactClassDisplayNameProvider = artifactClassDisplayNameProvider;
    }

    private IArtifactClassDisplayNameProvider artifactClassDisplayNameProvider;

    public void registerArtifactClassDisplayNameProvider(IArtifactClassDisplayNameProvider artifactClassDisplayNameProvider)
    {
        this.artifactClassDisplayNameProvider = artifactClassDisplayNameProvider;
    }

    /*

     */

    private final Object programArtifactLock = new Object();

    private AArtifact programArtifact;

    @Override
    public final AArtifact getProgramArtifact()
    {
        synchronized (programArtifactLock)
        {
            return programArtifact;
        }
    }

    @Override
    public final AArtifact getOrCreateProgramArtifact(final Class<? extends AArtifact> artifactClass)
    {
        if (programArtifact == null)
        {
            try
            {
                final Constructor<? extends AArtifact> declaredConstructor = artifactClass.getDeclaredConstructor(String.class, String.class);
                final String name = "Program";
                synchronized (programArtifactLock)
                { // Double checked locking! This method might be called by multiple threads simultaneously. For instance, that's the case in the
                    // stack sampling thread analysis strategies of the CodeSparks-JPT instance because that data is processed simultaneously using a thread
                    // pool!
                    if (programArtifact == null)
                    {
                        programArtifact = declaredConstructor.newInstance(name, name);
                    } else
                    {
                        CodeSparksLogger.addText("Thread '%s' tried to assign a new program artifact although it is not null!", Thread.currentThread());
                    }
                }
            } catch (IllegalAccessException | InstantiationException | InvocationTargetException | NoSuchMethodException e)
            {
                e.printStackTrace();
            }
        }
        return programArtifact;
    }

    /*

     */

    private final Object artifactsLock = new Object();

    private final Map<Class<? extends AArtifact>, Map<String, AArtifact>> artifacts;

    @Override
    public String getArtifactClassDisplayName(final Class<? extends AArtifact> artifactClass)
    {
        if (artifactClassDisplayNameProvider == null)
        {
            return "n/a";
        }
        return artifactClassDisplayNameProvider.getDisplayName(artifactClass);
    }

    @Override
    public Map<Class<? extends AArtifact>, List<AArtifact>> getArtifacts()
    {
        Map<Class<? extends AArtifact>, List<AArtifact>> map = new HashMap<>();
        synchronized (artifactsLock)
        {
            for (final Map.Entry<Class<? extends AArtifact>, Map<String, AArtifact>> entry : artifacts.entrySet())
            {
                map.put(entry.getKey(), new ArrayList<>(entry.getValue().values()));
            }
        }
        return map;
    }

    @Override
    public final List<AArtifact> getArtifacts(Class<? extends AArtifact> artifactClass)
    {
        if (artifactClass == null)
        {
            return new ArrayList<>(0);
        }
        synchronized (artifactsLock)
        {
            Map<String, AArtifact> artifactsOfClassMap = artifacts.computeIfAbsent(artifactClass, k -> new HashMap<>());

            Collection<AArtifact> artifacts = artifactsOfClassMap.values();

            return new ArrayList<>(artifacts);
        }
    }

    @Override
    public final AArtifact getArtifact(final String identifier)
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
    public final AArtifact getArtifact(final Class<? extends AArtifact> artifactClass, final String identifier)
    {
        if (artifactClass == null || identifier == null)
        {
            return null;
        }
        synchronized (artifactsLock)
        {
            Map<String, AArtifact> artifactsOfClassMap = artifacts.computeIfAbsent(artifactClass, k -> new HashMap<>());

            //noinspection UnnecessaryLocalVariable
            AArtifact artifact = artifactsOfClassMap.get(identifier);

            return artifact;
        }
    }

    @Override
    public final void addArtifact(AArtifact artifact)
    {
        synchronized (artifactsLock)
        {
            Map<String, AArtifact> artifactsOfClassMap = artifacts.computeIfAbsent(artifact.getClass(), k -> new HashMap<>());

            artifactsOfClassMap.put(artifact.getIdentifier(), artifact);
        }
    }

//    @Override
//    @Deprecated
//    public final Map<String, List<AArtifact>> getNamedArtifactTypeLists()
//    {
//        Map<String, List<AArtifact>> map = new HashMap<>();
//        synchronized (artifactsLock)
//        {
//            for (Map.Entry<Class<? extends AArtifact>, Map<String, AArtifact>> classMapEntry : artifacts.entrySet())
//            {
//                Class<? extends AArtifact> artifactClass = classMapEntry.getKey();
//                String classDisplayName;
//                if (artifactClassDisplayNameProvider == null)
//                {
//                    classDisplayName = artifactClass.getTypeName();
//                } else
//                {
//                    classDisplayName = artifactClassDisplayNameProvider.getDisplayName(artifactClass);
//                }
//                map.put(classDisplayName, new ArrayList<>(classMapEntry.getValue().values()));
//            }
//        }
//        return map;
//    }

    @Override
    public final void applyThreadFilter(final ICodeSparksThreadFilter threadFilter)
    {
        if (threadFilter == null)
        {
            return;
        }
        synchronized (programArtifactLock)
        {
            if (programArtifact != null)
            {
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
