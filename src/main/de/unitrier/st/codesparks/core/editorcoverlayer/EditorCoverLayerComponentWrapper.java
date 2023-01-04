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
package de.unitrier.st.codesparks.core.editorcoverlayer;

import javax.swing.*;
import java.awt.*;

public class EditorCoverLayerComponentWrapper extends JPanel
{
    private final JComponent component;
    private final EditorCoverLayerItem layerItem;

    EditorCoverLayerComponentWrapper(EditorCoverLayerItem layerItem)
    {
        this.layerItem = layerItem;
        this.component = layerItem.getComponent();
        this.component.setVisible(true);
        this.component.setLocation(0, 0);
        this.setLayout(null);
        this.setSize(component.getSize());
        this.add(this.component);
        this.setOpaque(false);
    }

    EditorCoverLayerItem getLayerItem()
    {
        return this.layerItem;
    }

    @Override
    public void paint(Graphics g)
    {
        super.paint(g);
        component.paint(g); // necessary!
    }
}
