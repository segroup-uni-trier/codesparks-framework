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
package de.unitrier.st.codesparks.core.data;

import java.util.HashSet;
import java.util.Set;

public class GlobalResetThreadArtifactFilter implements IThreadArtifactFilter
{

    private static volatile IThreadArtifactFilter instance;

    private GlobalResetThreadArtifactFilter()
    {

    }

    public static IThreadArtifactFilter getInstance()
    {
        if (instance == null)
        {
            synchronized (GlobalResetThreadArtifactFilter.class)
            {
                if (instance == null)
                {
                    instance = new GlobalResetThreadArtifactFilter();
                }
            }
        }

        return instance;
    }

    @Override
    public Set<String> getFilteredThreadIdentifiers()
    {
        return new HashSet<>();
    }

    @Override
    public Set<String> getSelectedThreadIdentifiers()
    {
        final ArtifactPoolManager artifactPoolManager = ArtifactPoolManager.getInstance();
        final IArtifactPool artifactPool = artifactPoolManager.getArtifactPool();
        if (artifactPool == null)
        {
            return new HashSet<>();
        }
        final AArtifact programArtifact = artifactPool.getProgramArtifact();
        if (programArtifact == null)
        {
            return new HashSet<>();
        }
        return programArtifact.getThreadArtifactIdentifiers();
    }
}
