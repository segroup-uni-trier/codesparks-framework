/*
 * Copyright (c) 2021. Oliver Moseler
 */

package de.unitrier.st.codesparks.core.visualization.popup;

import java.awt.*;

public class TotalNumberOfThreadsButtonFillStrategy implements IThreadClusterButtonFillStrategy
{
    private static IThreadClusterButtonFillStrategy instance;

    public static IThreadClusterButtonFillStrategy getInstance()
    {
        if (instance == null)
        {
            synchronized (TotalNumberOfThreadsButtonFillStrategy.class)
            {
                if (instance == null)
                {
                    instance = new TotalNumberOfThreadsButtonFillStrategy();
                }
            }
        }
        return instance;
    }

    private TotalNumberOfThreadsButtonFillStrategy() {}

    @Override
    public void fillThreadClusterButton(final ThreadClusterButton threadClusterButton, final Graphics g)
    {
        final Graphics2D graphics = (Graphics2D) g;

        final IThreadSelectable threadSelectable = threadClusterButton.getThreadSelectable();
        if (threadSelectable != null)
        {

        }
    }
}
