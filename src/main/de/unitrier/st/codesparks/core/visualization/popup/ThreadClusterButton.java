/*
 * Copyright (c) 2021. Oliver Moseler
 */

package de.unitrier.st.codesparks.core.visualization.popup;

import com.intellij.ui.JBColor;
import com.intellij.ui.components.JBPanel;
import com.intellij.util.ui.components.BorderLayoutPanel;
import de.unitrier.st.codesparks.core.data.AArtifact;
import de.unitrier.st.codesparks.core.data.AMetricIdentifier;
import de.unitrier.st.codesparks.core.data.ThreadArtifactCluster;
import de.unitrier.st.codesparks.core.data.ThreadArtifactClustering;
import de.unitrier.st.codesparks.core.visualization.VisConstants;
import de.unitrier.st.codesparks.core.visualization.VisualizationUtil;
import de.unitrier.st.codesparks.core.visualization.thread.IClusterHoverable;
import de.unitrier.st.codesparks.core.visualization.thread.IThreadSelectableIndexProvider;

import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.HashSet;
import java.util.Set;
import java.util.List;

public class ThreadClusterButton extends JBPanel<BorderLayoutPanel>
{
    private final AArtifact artifact;
    private final ThreadArtifactClustering clustering;
    private final AMetricIdentifier metricIdentifier;
    private final Set<Component> componentsToRepaint;
    private final IThreadSelectableIndexProvider selectableIndexProvider;
    private final List<IThreadSelectable> threadSelectables;
    private final ThreadArtifactCluster cluster;
    private final JBColor color;
    private final Rectangle boundsRectangle;
    private final IThreadClusterButtonFillStrategy fillStrategy;
    private final IClusterHoverable clusterHoverable;

    public ThreadClusterButton(final AArtifact artifact
            , final ThreadArtifactClustering clustering
            , final AMetricIdentifier metricIdentifier
            , final ThreadArtifactCluster cluster
            , final IThreadSelectableIndexProvider selectableIndexProvider
            , final List<IThreadSelectable> threadSelectables
            , final JBColor color
            , final Rectangle boundsRectangle
            , final IThreadClusterButtonFillStrategy fillStrategy
            , final IClusterHoverable clusterHoverable
    )
    {
        this.artifact = artifact;
        this.clustering = clustering;
        this.metricIdentifier = metricIdentifier;
        this.cluster = cluster;
        this.selectableIndexProvider = selectableIndexProvider;
        this.threadSelectables = threadSelectables;
        this.color = color;
        this.boundsRectangle = boundsRectangle;
        this.fillStrategy = fillStrategy;
        this.clusterHoverable = clusterHoverable;
        this.componentsToRepaint = new HashSet<>(4);

        this.addMouseListener(ClusterButtonMouseAdapter.getInstance());
        this.setBounds(boundsRectangle);
    }

    public void setMouseIn(final boolean mouseIn)
    {
        this.mouseIn = mouseIn;
    }

    private boolean mouseIn = false;

    public void registerComponentToRepaint(final ThreadClusterButton component)
    {
        this.componentsToRepaint.add(component);
    }

    public Set<Component> getComponentsToRepaint()
    {
        return componentsToRepaint;
    }

    private final static BasicStroke dashed = new BasicStroke(1.0f,
            BasicStroke.CAP_BUTT, BasicStroke.JOIN_MITER, 10.0f, new float[]{5.0f}, 0.0f);

    @Override
    protected void paintComponent(final Graphics g)
    {
        super.paintComponent(g);

        final Graphics2D graphics = (Graphics2D) g;
        graphics.setStroke(dashed);
        graphics.setColor(color);
        graphics.drawRect(0, 0, getWidth() - 1, getHeight() - 1);
        if (fillStrategy != null)
        {
            fillStrategy.fillThreadClusterButton(this, clustering, g);
        }
        if (mouseIn)
        {
            graphics.setColor(VisConstants.ORANGE);
            final int strokeWidth = 2;
            graphics.setStroke(new BasicStroke(strokeWidth));
            graphics.drawRect(1, 1, getWidth() - strokeWidth, getHeight() - strokeWidth);
        }
    }

    public AArtifact getArtifact()
    {
        return artifact;
    }

    public List<IThreadSelectable> getThreadSelectables()
    {
        return threadSelectables;
    }

    public IThreadSelectableIndexProvider getSelectableIndexProvider()
    {
        return selectableIndexProvider;
    }

    public ThreadArtifactCluster getCluster()
    {
        return cluster;
    }

    public AMetricIdentifier getMetricIdentifier()
    {
        return metricIdentifier;
    }

    public Rectangle getBoundsRectangle()
    {
        return boundsRectangle;
    }

    public JBColor getColor()
    {
        return color;
    }

    public IClusterHoverable getClusterHoverable()
    {
        return clusterHoverable;
    }

    //    @Override
//    public void paint(final Graphics g)
//    {
//        super.paint(g);
//    }

    private static class ClusterButtonMouseAdapter extends MouseAdapter
    {
        private static MouseAdapter instance;

        public static MouseAdapter getInstance()
        {
            if (instance == null)
            {
                synchronized (ClusterButtonMouseAdapter.class)
                {
                    if (instance == null)
                    {
                        instance = new ClusterButtonMouseAdapter();
                    }
                }
            }
            return instance;
        }

        private ClusterButtonMouseAdapter() {}

        @Override
        public void mouseEntered(final MouseEvent e)
        {
            final ThreadClusterButton source = (ThreadClusterButton) e.getSource();
            VisualizationUtil.setCursorRecursively(source, Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
            source.setMouseIn(true);
            for (final Component component : source.getComponentsToRepaint())
            {
                if (component instanceof ThreadClusterButton)
                {
                    ((ThreadClusterButton) component).setMouseIn(true);
                }
                component.repaint();
            }
            source.repaint();
            source.getClusterHoverable().onHover(source.getCluster());
        }

        @Override
        public void mouseExited(final MouseEvent e)
        {
            final ThreadClusterButton source = (ThreadClusterButton) e.getSource();
            VisualizationUtil.setCursorRecursively(source, Cursor.getPredefinedCursor(Cursor.TEXT_CURSOR));
            source.setMouseIn(false);
            for (final Component component : source.getComponentsToRepaint())
            {
                if (component instanceof ThreadClusterButton)
                {
                    ((ThreadClusterButton) component).setMouseIn(false);
                }
                component.repaint();
            }
            source.repaint();
            source.getClusterHoverable().onExit();
        }

        @Override
        public void mouseClicked(final MouseEvent e)
        {
            final ThreadClusterButton source = (ThreadClusterButton) e.getSource();
            if (source != null)
            {
                final List<IThreadSelectable> threadSelectables = source.getThreadSelectables();
                for (final IThreadSelectable threadSelectable : threadSelectables)
                {
                    threadSelectable.toggleCluster(source.getCluster());
                }
                source.getClusterHoverable().onExit();
            }

//            new Thread(() -> {
//                try
//                {
//                    Thread.sleep(100);
//                    final Robot robot = new Robot();
//                    final int x = e.getLocationOnScreen().x;
//                    final int y = e.getLocationOnScreen().y;
//                    robot.mouseMove(x + 1, y);
//                } catch (AWTException | InterruptedException awtException)
//                {
//                    awtException.printStackTrace();
//                }
//            }).start();
        }
    }
}
