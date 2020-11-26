package de.unitrier.st.codesparks.core.editorcoverlayer;

import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.editor.VisualPosition;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/*
 * Copyright (c), Oliver Moseler, 2020
 */
class EditorCoverLayer extends JComponent
{
    /*
    Although the editorCoverLayerItem holds a reference to the respective JComponent to draw, we need to store the JComponents twice in the
    following map because we run into a ConcurrentModificationException when repainting the items. See therefore the
    method paint. So in the case an user would rerun
    the profiling, the editor layer
    items will be removed from the list but also repainted
    from the ui thread which results in concurrency problems. We also cannot add synchronization to the add method since we would end up in
    an deadlock.
     */
    private final Map<EditorCoverLayerItem, EditorCoverLayerComponentWrapper> layerItems;
    private final Editor editor;

    EditorCoverLayer(Editor editor)
    {
        this.layerItems = new ConcurrentHashMap<>();//new HashMap<>();
        this.editor = editor;
        this.setLayout(null); // Absolute Positioning

        JComponent contentComponent = this.editor.getContentComponent();
        contentComponent.add(this);

        int width = contentComponent.getWidth();
        int height = contentComponent.getHeight();
        setBounds(0, 0, width, height);
        setSize(width, height);

        ComponentAdapter componentAdapter = new ComponentAdapter()
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

//    @Override
//    public Dimension getSize()
//    {
//        return super.getSize();
//    }

    boolean addLayerItem(final EditorCoverLayerItem layerItem)
    {
        //System.out.println(Thread.currentThread() + " accessing addLayerItem(final EditorCoverLayerItem layerItem)");
        synchronized (layerItems)
        {
            if (layerItems.containsKey(layerItem))
            {
                return false;
            }
            EditorCoverLayerComponentWrapper wrapper = new EditorCoverLayerComponentWrapper(layerItem);
            layerItems.put(layerItem, wrapper);
            this.add(wrapper);
            wrapper.setEnabled(true);
            wrapper.setVisible(true);
            this.repaint();
            JComponent component = layerItem.getComponent();
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
            //UIUtil.invokeLaterIfNeeded(() -> layerItems.remove(layerItem));
            layerItems.remove(layerItem);
        }
    }

    @Override
    public void paint(Graphics g)
    {
        layerItems.values().forEach(wrapper ->
        {
            EditorCoverLayerItem layerItem = wrapper.getLayerItem();
            if (!layerItem.isValid())
            {
                wrapper.setVisible(false);
                wrapper.setEnabled(false);
                remove(wrapper);
                removeLayerItem(layerItem);
            } else
            {
                int offset = layerItem.getOffset(editor);
                VisualPosition iconVisualPosition = editor.offsetToVisualPosition(offset);
                Point point = editor.visualPositionToXY(iconVisualPosition);
                int height = wrapper.getHeight();
                point.y = point.y - (height - editor.getLineHeight() + 2);
                wrapper.setLocation(point);
            }
        });
        super.paint(g);
    }

    @Override
    public boolean equals(Object o)
    {
        if (this == o)
        {
            return true;
        }
        if (o == null || getClass() != o.getClass())
        {
            return false;
        }
        EditorCoverLayer that = (EditorCoverLayer) o;
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
            layerItems.forEach((layerItem, editorCoverLayerComponentWrapper) -> {
                editorCoverLayerComponentWrapper.removeAll();
//                editorCoverLayerComponentWrapper.setVisible(false);
//                editorCoverLayerComponentWrapper.setEnabled(false);
            });
            layerItems.clear();
        }
//        for (Component component : getComponents())
//        {
//            component.setVisible(false);
//            component.setEnabled(false);
//        }
        removeAll();
//        setVisible(false);
//        setEnabled(false);
    }
}
