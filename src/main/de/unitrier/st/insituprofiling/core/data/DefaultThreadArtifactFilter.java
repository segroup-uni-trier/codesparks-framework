package de.unitrier.st.insituprofiling.core.data;

import de.unitrier.st.insituprofiling.core.visualization.popup.IThreadSelectable;

import java.util.Set;

public class DefaultThreadArtifactFilter implements IThreadArtifactFilter
{

    private final IThreadSelectable iThreadSelectable;

    public DefaultThreadArtifactFilter(IThreadSelectable iThreadSelectable)
    {
        this.iThreadSelectable = iThreadSelectable;
    }

    @Override
    public Set<String> getFilteredThreadArtifactIdentifiers()
    {
        return iThreadSelectable.getFilteredThreadArtifactIdentifiers();
    }

    @Override
    public Set<String> getSelectedThreadArtifactIdentifiers()
    {
        return iThreadSelectable.getSelectedThreadArtifactIdentifiers();
    }
}
