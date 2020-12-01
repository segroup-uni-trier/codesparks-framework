package de.unitrier.st.codesparks.core.editorcoverlayer;

import javax.swing.*;
import java.awt.*;

/*
 * Copyright (c), Oliver Moseler, 2020
 */
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
        // component.setOpaque(false);
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
