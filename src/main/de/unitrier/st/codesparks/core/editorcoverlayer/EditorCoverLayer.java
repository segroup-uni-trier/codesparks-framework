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

import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.editor.VisualPosition;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

class EditorCoverLayer extends JComponent
{
    /*
    Although the editorCoverLayerItem holds a reference to the respective JComponent to draw, we need to store the JComponents twice in the
    following map because we run into a ConcurrentModificationException when repainting the items. See therefore the
    method paint. So in the case an user would rerun
    the profiling, the editor layer
    items will be removed from the list but also repainted
    from the ui thread which results in concurrency problems. We also cannot add synchronization to the add method since we would end up in
    a deadlock.
     */
    private final Map<EditorCoverLayerItem, EditorCoverLayerComponentWrapper> layerItems;
    private final Editor editor;

    EditorCoverLayer(Editor editor)
    {
        this.layerItems = new ConcurrentHashMap<>();
        this.editor = editor;
        this.setLayout(null); // Absolute Positioning

        final JComponent contentComponent = this.editor.getContentComponent();
        contentComponent.add(this);

        final int width = contentComponent.getWidth();
        final int height = contentComponent.getHeight();
        setBounds(0, 0, width, height);
        setSize(width, height);

        final ComponentAdapter componentAdapter = new ComponentAdapter()
        {
            @Override
            public void componentResized(ComponentEvent e)
            {   // Making sure the editor cover layer has always the same size as the underling editor
                super.componentResized(e);
                Component component = e.getComponent();
                setSize(component.getWidth(), component.getHeight());
            }
        };
        contentComponent.addComponentListener(componentAdapter);
    }

    boolean addLayerItem(final EditorCoverLayerItem layerItem)
    {
        synchronized (layerItems)
        {
            if (layerItems.containsKey(layerItem))
            {
                return false;
            }
            final EditorCoverLayerComponentWrapper wrapper = new EditorCoverLayerComponentWrapper(layerItem);
            layerItems.put(layerItem, wrapper);
            this.add(wrapper);
            wrapper.setEnabled(true);
            wrapper.setVisible(true);
            this.repaint();
            final JComponent component = layerItem.getComponent();
            component.updateUI();
            return true;
        }
    }

    private void removeLayerItem(final EditorCoverLayerItem layerItem)
    {
        if (layerItem == null)
        {
            return;
        }
        synchronized (layerItems)
        {
            layerItems.remove(layerItem);
        }
    }

    @Override
    public void paint(final Graphics g)
    {
        layerItems.values().forEach(wrapper ->
        {
            final EditorCoverLayerItem layerItem = wrapper.getLayerItem();
            if (!layerItem.isValid())
            {
                wrapper.setVisible(false);
                wrapper.setEnabled(false);
                remove(wrapper);
                removeLayerItem(layerItem);
            } else
            {
                final int offset = layerItem.getOffset(editor);
                final VisualPosition iconVisualPosition = editor.offsetToVisualPosition(offset);
                final Point point = editor.visualPositionToXY(iconVisualPosition);
                final int height = wrapper.getHeight();
                point.y = point.y - (height - editor.getLineHeight() + 2);
                wrapper.setLocation(point);
            }
        });
        super.paint(g);
    }

    @Override
    public boolean equals(final Object o)
    {
        if (this == o)
        {
            return true;
        }
        if (o == null || getClass() != o.getClass())
        {
            return false;
        }
        final EditorCoverLayer that = (EditorCoverLayer) o;
        return editor.equals(that.editor);
    }

    @Override
    public int hashCode()
    {
        return editor.hashCode();
    }

    void clear()
    {
        synchronized (layerItems)
        {
            layerItems.forEach((layerItem, editorCoverLayerComponentWrapper) -> editorCoverLayerComponentWrapper.removeAll());
            layerItems.clear();
        }
        removeAll();
    }
}
