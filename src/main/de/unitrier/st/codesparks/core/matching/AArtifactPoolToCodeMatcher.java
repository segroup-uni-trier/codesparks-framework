/*
 * Copyright (c) 2022.
 */

package de.unitrier.st.codesparks.core.matching;

import de.unitrier.st.codesparks.core.data.AArtifact;

public abstract class AArtifactPoolToCodeMatcher implements IArtifactPoolToCodeMatcher
{
    protected final Class<? extends AArtifact>[] artifactClasses;

    /**
     * @param classes The classes extending AArtifact.
     */
    @SafeVarargs
    protected AArtifactPoolToCodeMatcher(final Class<? extends AArtifact>... classes)
    {
        this.artifactClasses = classes;
    }
}
