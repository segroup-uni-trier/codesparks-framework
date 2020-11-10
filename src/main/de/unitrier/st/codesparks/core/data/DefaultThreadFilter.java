package de.unitrier.st.codesparks.core.data;

import de.unitrier.st.codesparks.core.visualization.popup.IThreadSelectable;

import java.util.Set;

public class DefaultThreadFilter implements ICodeSparksThreadFilter
{

    private final IThreadSelectable iThreadSelectable;

    public DefaultThreadFilter(IThreadSelectable iThreadSelectable)
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
