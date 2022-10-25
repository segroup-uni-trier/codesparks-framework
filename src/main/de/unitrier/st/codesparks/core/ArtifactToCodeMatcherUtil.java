/*
 * Copyright (c) 2022. Oliver Moseler
 */

package de.unitrier.st.codesparks.core;

import de.unitrier.st.codesparks.core.data.AArtifact;

import java.lang.annotation.Annotation;
import java.util.Arrays;
import java.util.Optional;

public final class ArtifactToCodeMatcherUtil
{
    private ArtifactToCodeMatcherUtil() {}

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
}
