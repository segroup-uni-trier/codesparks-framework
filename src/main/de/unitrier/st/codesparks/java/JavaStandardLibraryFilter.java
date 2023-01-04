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
