/*
 * Copyright (c) 2022. Oliver Moseler
 */

package de.unitrier.st.codesparks.core;

import de.unitrier.st.codesparks.core.data.AArtifact;

import java.lang.annotation.Annotation;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.Arrays;
import java.util.Optional;

public final class ArtifactPoolToCodeMatcherUtil
{
    private ArtifactPoolToCodeMatcherUtil() {}

    @SafeVarargs
    public static Class<? extends AArtifact> findClassWithAnnotation(
            final Class<? extends Annotation> annotation,
            final Class<? extends AArtifact>... artifactClasses
    )
    {
        if (artifactClasses == null || artifactClasses.length < 1)
        {
            return null;
        }
        final Optional<Class<? extends AArtifact>> first =
                Arrays.stream(artifactClasses)
                        .filter(artifactClass -> artifactClass.isAnnotationPresent(annotation))
                        .findFirst();
        return first.orElse(null);
    }

    public static AArtifact instantiateArtifact(
            final Class<? extends AArtifact> artifactClass,
            final String... constructorParameters
    )
    {
        final Constructor<?>[] constructors = artifactClass.getConstructors();
        final Optional<Constructor<?>> first =
                Arrays.stream(constructors).
                        filter(constructor -> constructor.getParameterCount() == constructorParameters.length)
                        .findFirst();
        AArtifact artifact = null;
        if (first.isPresent())
        {
            final Constructor<?> constructor = first.get();
            try
            {
                artifact = (AArtifact) constructor.newInstance((Object[]) constructorParameters);
            } catch (InstantiationException | IllegalAccessException | InvocationTargetException e)
            {
                e.printStackTrace();
            }
        }
        return artifact;
    }
}
