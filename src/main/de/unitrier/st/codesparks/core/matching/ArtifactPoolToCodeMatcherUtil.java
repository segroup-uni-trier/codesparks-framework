/*
 *  Copyright 2023 Oliver Moseler
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */

package de.unitrier.st.codesparks.core.matching;

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
