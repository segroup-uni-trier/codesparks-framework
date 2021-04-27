/*
 * Copyright (c) 2021. Oliver Moseler
 */

package de.unitrier.st.codesparks.core.visualization.popup;

import com.intellij.ui.components.JBPanel;
import com.intellij.util.ui.components.BorderLayoutPanel;
import de.unitrier.st.codesparks.core.visualization.VisConstants;

import java.awt.*;

public class ThreadClusterJBPanel extends JBPanel<BorderLayoutPanel>
{
    private final int maxWidth;

    public ThreadClusterJBPanel(final int maxWidth)
    {
        this.maxWidth = maxWidth;
    }

    public void setMouseIn(final boolean mouseIn)
    {
        this.mouseIn = mouseIn;
    }

    private boolean mouseIn = false;

    @Override
    public void paint(final Graphics g)
    {
        super.paint(g);
        if (mouseIn)
        {
            final Graphics2D graphics = (Graphics2D) getGraphics();
            graphics.setColor(VisConstants.ORANGE);
            graphics.drawRect(0, 0, getWidth(), getHeight());
        }
    }
}
