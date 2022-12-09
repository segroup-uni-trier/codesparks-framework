/*
 * Copyright (c) 2021. Oliver Moseler
 */
package de.unitrier.st.codesparks.core.data;

import java.util.Set;

public class DefaultThreadArtifactFilter implements IThreadArtifactFilter
{

    private final IThreadSelectable iThreadSelectable;

    public DefaultThreadArtifactFilter(IThreadSelectable iThreadSelectable)
    {
        this.iThreadSelectable = iThreadSelectable;
    }

    @Override
    public Set<String> getFilteredThreadIdentifiers()
    {
        return iThreadSelectable.getFilteredThreadArtifactIdentifiers();
    }

    @Override
    public Set<String> getSelectedThreadIdentifiers()
    {
        return iThreadSelectable.getSelectedThreadArtifactIdentifiers();
    }
}
