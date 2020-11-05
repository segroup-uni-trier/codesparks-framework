package de.unitrier.st.insituprofiling.core.data;

import de.unitrier.st.insituprofiling.core.ProfilingResultManager;

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
    public Set<String> getFilteredThreadArtifactIdentifiers()
    {
        return new HashSet<>();
    }

    @Override
    public Set<String> getSelectedThreadArtifactIdentifiers()
    {
        return ProfilingResultManager.getInstance().getProfilingResult().getGlobalArtifact().getThreadArtifactIdentifiers();
    }
}
