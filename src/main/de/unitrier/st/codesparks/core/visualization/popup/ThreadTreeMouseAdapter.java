package de.unitrier.st.codesparks.core.visualization.popup;

import com.intellij.openapi.application.ApplicationManager;
import de.unitrier.st.codesparks.core.visualization.thread.ARadialThreadVisualization;

import javax.swing.*;
import javax.swing.tree.TreePath;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

public class ThreadTreeMouseAdapter extends MouseAdapter
{
    private final ARadialThreadVisualization radialThreadVisualization;

    public ThreadTreeMouseAdapter(ARadialThreadVisualization radialThreadVisualization)
    {
        this.radialThreadVisualization = radialThreadVisualization;
    }

    @Override
    public void mouseClicked(MouseEvent e)
    {
        final JTree tree = (JTree) e.getSource();
        if (tree == null)
        {
            return;
        }
        final java.awt.Point point = e.getPoint();
        final TreePath pathForLocation = tree.getPathForLocation(point.x, point.y);
        if (pathForLocation == null)
        {
            return;
        }
        ColoredSelectableTreeNode lastPathComponent = (ColoredSelectableTreeNode) pathForLocation.getLastPathComponent();
        if (lastPathComponent == null)
        {
            return;
        }
        lastPathComponent.toggleSelected();
        ApplicationManager.getApplication().invokeLater(tree::updateUI);
        radialThreadVisualization.repaint();
    }
}
