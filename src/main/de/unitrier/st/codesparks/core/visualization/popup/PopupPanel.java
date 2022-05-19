/*
 * Copyright (c) 2022. Oliver Moseler
 */
package de.unitrier.st.codesparks.core.visualization.popup;

import com.intellij.openapi.ui.popup.JBPopup;
import com.intellij.ui.components.JBPanel;
import com.intellij.util.ui.components.BorderLayoutPanel;

import java.awt.*;

public final class PopupPanel extends JBPanel<BorderLayoutPanel>
{
    private final String description;

    public PopupPanel(final String description)
    {
        super(new BorderLayout());
        this.description = description;
    }

    public String getDescription()
    {
        return description;
    }

    private JBPopup popup;
    private final Object popupLock = new Object();

    public void registerPopup(final JBPopup popup)
    {
        synchronized (popupLock)
        {
            this.popup = popup;
        }
    }

    public void cancelPopup()
    {
        synchronized (popupLock)
        {
            if (popup != null)
            {
                popup.cancel();
            }
        }
    }
}
