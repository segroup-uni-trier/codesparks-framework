/*
 * Copyright (c) 2021. Oliver Moseler
 */
package de.unitrier.st.codesparks.core.visualization.popup;

import com.intellij.openapi.ui.popup.JBPopup;
import com.intellij.ui.components.JBPanel;
import com.intellij.util.ui.components.BorderLayoutPanel;

import java.awt.*;

public final class PopupPanel extends JBPanel<BorderLayoutPanel>
{
    private String type;

    public PopupPanel(LayoutManager layout, final String type)
    {
        super(layout);
        this.type = type;
    }

    public PopupPanel() { }

    private JBPopup popup;

    public String getType()
    {
        return type;
    }

    public void setType(final String type)
    {
        this.type = type;
    }

    public void registerPopup(JBPopup popup)
    {
        synchronized (this)
        {
            this.popup = popup;
        }
    }

    public void cancelPopup()
    {
        synchronized (this)
        {
            if (popup != null)
            {
                popup.cancel();
            }
        }
    }
}
