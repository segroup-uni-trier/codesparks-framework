package de.unitrier.st.codesparks.core;

/*
 * Copyright (c), Oliver Moseler, 2020
 */
public final class CodeSparksFlowManager
{
    private CodeSparksFlowManager() {}

    private static CodeSparksFlowManager instance;

    public static CodeSparksFlowManager getInstance()
    {
        if (instance == null)
        {
            synchronized (CodeSparksFlowManager.class)
            {
                if (instance == null)
                {
                    instance = new CodeSparksFlowManager();
                }
            }
        }
        return instance;
    }

    private ACodeSparksFlow profilingFlow;

    public ACodeSparksFlow getCurrentCodeSparksFlow()
    {
        synchronized (this)
        {
            return profilingFlow;
        }
    }

    public void setCurrentCodeSparksFlow(ACodeSparksFlow profilingFlow)
    {
        synchronized (this)
        {
            this.profilingFlow = profilingFlow;
        }
    }
}
