/*
 * Copyright (c) 2021. Oliver Moseler
 */
package de.unitrier.st.codesparks.core;

import javax.swing.*;

public final class CodeSparksFlowManager
{
    private CodeSparksFlowManager() {}

    private static volatile CodeSparksFlowManager instance;

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

    private ACodeSparksFlow codeSparksFlow;

    public ACodeSparksFlow getCurrentCodeSparksFlow()
    {
        synchronized (this)
        {
            return codeSparksFlow;
        }
    }

    public void setCurrentCodeSparksFlow(ACodeSparksFlow codeSparksFlow)
    {
        synchronized (this)
        {
            this.codeSparksFlow = codeSparksFlow;
        }
    }

    public ImageIcon getImageIcon()
    {
        if (codeSparksFlow != null)
        {
            return codeSparksFlow.getImageIcon();
        } else
        {
            return CoreUtil.getDefaultImageIcon();
        }
    }
}
