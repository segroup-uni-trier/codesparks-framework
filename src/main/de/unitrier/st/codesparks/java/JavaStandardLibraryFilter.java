/*
 * Copyright (c) 2021-2022. Oliver Moseler
 */
package de.unitrier.st.codesparks.java;

import de.unitrier.st.codesparks.core.data.AArtifact;
import de.unitrier.st.codesparks.core.overview.IArtifactFilter;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

public class JavaStandardLibraryFilter implements IArtifactFilter
{
    private static volatile IArtifactFilter instance;

    public static IArtifactFilter getInstance()
    {
        if (instance == null)
        {
            synchronized (JavaStandardLibraryFilter.class)
            {
                if (instance == null)
                {
                    instance = new JavaStandardLibraryFilter();
                }
            }
        }
        return instance;
    }

    private JavaStandardLibraryFilter() {}

    private static final Set<String> JAVA_SYSTEM_EXCLUDE_FILTER =
            new HashSet<>(Arrays.asList("java.", "jdk.", "sun.", "javax.", "sunw.", "com.sun"));

    @Override
    public boolean filterArtifact(AArtifact artifact)
    {
        return JAVA_SYSTEM_EXCLUDE_FILTER.stream()
                .anyMatch(s -> artifact.getIdentifier().toLowerCase().startsWith(s.toLowerCase()));
    }
}
