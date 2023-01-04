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
