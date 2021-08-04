/*
 * Copyright (c) 2021. Oliver Moseler
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
