/*
 * Copyright (c) 2021. Oliver Moseler
 */

package de.unitrier.st.codesparks.core.visualization.popup;

import com.intellij.ui.JBColor;
import com.intellij.ui.components.JBPanel;
import com.intellij.util.ui.components.BorderLayoutPanel;
import de.unitrier.st.codesparks.core.visualization.VisConstants;

import javax.swing.*;
import java.awt.*;

public class ThreadClusterButton extends JButton//JBPanel<BorderLayoutPanel>
{
    private final int maxWidth;

    public ThreadClusterButton(final int maxWidth)
    {
        this.maxWidth = maxWidth;
        this.setBorder(null);
        //this.setBorder(BorderFactory.createLineBorder(VisConstants.ORANGE));
        //this.setBorderPainted(false);
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
