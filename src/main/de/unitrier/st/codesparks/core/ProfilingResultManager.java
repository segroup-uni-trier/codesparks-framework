package de.unitrier.st.codesparks.core;

public class ProfilingResultManager
{
    private static volatile ProfilingResultManager instance;

    private ProfilingResultManager() { }

    public static ProfilingResultManager getInstance()
    {
        if (instance == null)
        {
            synchronized (ProfilingResultManager.class)
            {
                if (instance == null)
                {
                    instance = new ProfilingResultManager();
                }
            }
        }
        return instance;
    }

    private IProfilingResult profilingResult;

    public IProfilingResult getProfilingResult()
    {
        return profilingResult;
    }

    public void setProfilingResult(IProfilingResult profilingResult)
    {
        this.profilingResult = profilingResult;
    }
}
