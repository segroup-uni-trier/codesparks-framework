package de.unitrier.st.codesparks.core.data;

import de.unitrier.st.codesparks.core.ArtifactPoolManager;

import java.util.HashSet;
import java.util.Set;

public class GlobalResetThreadFilter implements ICodeSparksThreadFilter
{

    private static volatile ICodeSparksThreadFilter instance;

    private GlobalResetThreadFilter()
    {

    }

    public static ICodeSparksThreadFilter getInstance()
    {
        if (instance == null)
        {
            synchronized (GlobalResetThreadFilter.class)
            {
                if (instance == null)
                {
                    instance = new GlobalResetThreadFilter();
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
        return ArtifactPoolManager.getInstance().getArtifactPool().getProgramArtifact().getCodeSparksThreadIdentifiers();
    }
}
