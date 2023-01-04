/*
 *  Copyright 2023 Oliver Moseler
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
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
