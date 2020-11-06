package de.unitrier.st.codesparks.core;

public final class ProfilingFlowManager
{
    private ProfilingFlowManager() {}

    private static ProfilingFlowManager instance;

    public static ProfilingFlowManager getInstance()
    {
        if (instance == null)
        {
            synchronized (ProfilingFlowManager.class)
            {
                if (instance == null)
                {
                    instance = new ProfilingFlowManager();
                }
            }
        }
        return instance;
    }

    private AProfilingFlow profilingFlow;

    public synchronized AProfilingFlow getCurrentProfilingFlow()
    {
        return profilingFlow;
    }

    synchronized void setCurrentProfilingFlow(AProfilingFlow profilingFlow)
    {
        this.profilingFlow = profilingFlow;
    }
}
