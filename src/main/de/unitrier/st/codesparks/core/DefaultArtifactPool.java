/*
 * Copyright (c), Oliver Moseler, 2021
 */
package de.unitrier.st.codesparks.core;

import de.unitrier.st.codesparks.core.data.*;
import de.unitrier.st.codesparks.core.logging.CodeSparksLogger;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.*;

public class DefaultArtifactPool implements IArtifactPool
{
    public DefaultArtifactPool()
    {
        this.artifacts = new HashMap<>();
    }

    public DefaultArtifactPool(final IArtifactClassDisplayNameProvider artifactClassDisplayNameProvider)
    {
        this.artifacts = new HashMap<>();
        this.artifactClassDisplayNameProvider = artifactClassDisplayNameProvider;
    }

    private IArtifactClassDisplayNameProvider artifactClassDisplayNameProvider;

    @Override
    public void registerArtifactClassDisplayNameProvider(final IArtifactClassDisplayNameProvider artifactClassDisplayNameProvider)
    {
        this.artifactClassDisplayNameProvider = artifactClassDisplayNameProvider;
    }

    /*
     * Program artifact
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
     * Artifacts map
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
            final Map<String, AArtifact> artifactsOfClassMap = artifacts.computeIfAbsent(artifactClass, k -> new HashMap<>());
            //noinspection UnnecessaryLocalVariable
            final AArtifact artifact = artifactsOfClassMap.get(identifier);
            return artifact;
        }
    }

    @Override
    public final AArtifact getOrCreateArtifact(final Class<? extends AArtifact> artifactClass, final String identifier, final Object... arguments)
    {
        if (artifactClass == null || identifier == null)
        {
            return null;
        }
        synchronized (artifactsLock)
        {
            final Map<String, AArtifact> artifactsOfClassMap = artifacts.computeIfAbsent(artifactClass, k -> new HashMap<>());
            AArtifact artifact = artifactsOfClassMap.get(identifier);
            if (artifact == null)
            {
                final Constructor<? extends AArtifact> declaredConstructor;
                try
                {
                    if (arguments.length == 0)
                    {
                        declaredConstructor = artifactClass.getDeclaredConstructor(String.class);
                        artifact = declaredConstructor.newInstance(identifier);
                    } else
                    {
                        final int len = arguments.length + 1;
                        Class<?>[] classes = new Class[len];
                        Object[] objects = new Object[len];
                        classes[0] = String.class;
                        objects[0] = identifier;
                        for (int i = 1; i < len; i++)
                        {
                            classes[i] = arguments[i - 1].getClass();
                            objects[i] = arguments[i - 1];
                        }
                        declaredConstructor = artifactClass.getDeclaredConstructor(classes);
                        artifact = declaredConstructor.newInstance(objects);
                    }
                    artifactsOfClassMap.put(identifier, artifact);
                    artifacts.put(artifactClass, artifactsOfClassMap);
                } catch (NoSuchMethodException | InvocationTargetException | InstantiationException | IllegalAccessException e)
                {
                    e.printStackTrace();
                }
            }
            return artifact;
        }
    }

    @Override
    public AArtifact getOrCreateThreadArtifact(final Class<? extends AThreadArtifact> threadArtifactClass, final String threadIdentifier)
    {
        if (threadArtifactClass == null || threadIdentifier == null || "".equals(threadIdentifier))
        {
            return null;
        }
        AArtifact artifact;
        synchronized (artifactsLock)
        {
            final Map<String, AArtifact> artifactsOfClassMap = artifacts.computeIfAbsent(threadArtifactClass, k -> new HashMap<>());
            artifact = artifactsOfClassMap.get(threadIdentifier);
            if (artifact == null)
            {
                try
                {
                    final Constructor<? extends AArtifact> declaredConstructor = threadArtifactClass.getDeclaredConstructor(String.class);
                    artifact = declaredConstructor.newInstance(threadIdentifier);
                    artifactsOfClassMap.put(threadIdentifier, artifact);
                    artifacts.put(threadArtifactClass, artifactsOfClassMap);
                } catch (NoSuchMethodException | IllegalAccessException | InstantiationException | InvocationTargetException e)
                {
                    e.printStackTrace();
                }
            }
        }
        return artifact;
    }

    @Override
    public final void addArtifact(final AArtifact artifact)
    {
        synchronized (artifactsLock)
        {
            final Map<String, AArtifact> artifactsOfClassMap = artifacts.computeIfAbsent(artifact.getClass(), k -> new HashMap<>());
            artifactsOfClassMap.put(artifact.getIdentifier(), artifact);
        }
    }

    @Override
    public final void applyThreadFilter(final IThreadArtifactFilter threadFilter)
    {
        if (threadFilter == null)
        {
            return;
        }
        synchronized (programArtifactLock)
        {
            programArtifact.applyThreadFilter(threadFilter);
        }

        synchronized (artifactsLock)
        {
            Collection<Map<String, AArtifact>> values = artifacts.values();
            for (Map<String, AArtifact> value : values)
            {
                value.values()
                        .forEach(artifact -> {
                            artifact.applyThreadFilter(threadFilter);
                            artifact.getSuccessorsList().forEach(aNeighborProfilingArtifact
                                    -> aNeighborProfilingArtifact.applyThreadFilter(threadFilter));
                            artifact.getPredecessorsList().forEach(aNeighborProfilingArtifact
                                    -> aNeighborProfilingArtifact.applyThreadFilter(threadFilter));
                        });
            }
        }
    }

    @Override
    public void clear()
    {
        for (final Map.Entry<Class<? extends AArtifact>, Map<String, AArtifact>> classMapEntry : artifacts.entrySet())
        {
            final Map<String, AArtifact> value = classMapEntry.getValue();
            for (final Map.Entry<String, AArtifact> stringAArtifactEntry : value.entrySet())
            {
                stringAArtifactEntry.getValue().clear();
            }
            value.clear();
        }
        artifacts.clear();
    }

    @Override
    public void export(IArtifactPoolExportStrategy strategy)
    {
        strategy.export(this);
    }
}
