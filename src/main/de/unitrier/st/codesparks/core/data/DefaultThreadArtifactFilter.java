package de.unitrier.st.codesparks.core.data;

import de.unitrier.st.codesparks.core.visualization.popup.IThreadSelectable;

import java.util.Set;

/*
 * Copyright (c), Oliver Moseler, 2020
 */
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
