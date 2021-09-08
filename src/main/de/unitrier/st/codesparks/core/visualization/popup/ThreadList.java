/*
 * Copyright (c) 2021. Oliver Moseler
 */
package de.unitrier.st.codesparks.core.visualization.popup;

import com.intellij.ui.ExpandedItemListCellRendererWrapper;
import com.intellij.ui.components.JBCheckBox;
import com.intellij.ui.components.JBList;
import de.unitrier.st.codesparks.core.data.*;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.HashSet;
import java.util.Set;

public class ThreadList extends AThreadSelectable
{
    private final JBList<JBCheckBox> list;

    public ThreadList(final AArtifact artifact, final AMetricIdentifier metricIdentifier, final boolean applyClusterColors)
    {
        list = new JBList<>(new ThreadListModel(artifact, metricIdentifier))
        {
            @Override
            public String getToolTipText(MouseEvent event)
            {
                final Point point = event.getPoint();
                final int index = locationToIndex(point);
                final AThreadArtifact threadArtifactAt = ((ThreadListModel) getModel()).getThreadArtifactAt(index);
                if (threadArtifactAt == null)
                {
                    return "";
                }
                String identifier = threadArtifactAt.getIdentifier();
                identifier = identifier.replaceAll("<", "&lt;").replaceAll(">", "&gt;");
                return identifier;
            }

            @Override
            public void repaint()
            {
                super.repaint();
                updateAndRepaintRegisteredComponents();
            }
        };
        component = list;
        final ThreadListCellRenderer threadListCellRenderer = new ThreadListCellRenderer(artifact, metricIdentifier, applyClusterColors);
        list.setCellRenderer(threadListCellRenderer);
        final JBList<JBCheckBox> that = list;
        list.addMouseListener(new MouseAdapter()
        {
            @Override
            public void mousePressed(MouseEvent e)
            {
                Point point = e.getPoint();
                int index = that.locationToIndex(point);
                threadListCellRenderer.toggleSelected(index);
                that.repaint();
            }
        });
    }

    public ThreadList(final AArtifact artifact, final AMetricIdentifier metricIdentifier)
    {
        this(artifact, metricIdentifier, true);
    }

    @Override
    public void deselectAll()
    {
        getThreadListCellRenderer().deselectAll();
        repaint();
    }

    @Override
    public void selectAll()
    {
        getThreadListCellRenderer().selectAll();
        repaint();
    }

    @Override
    public void invertAll()
    {
        for (int i = 0; i < list.getItemsCount(); i++)
        {
            getThreadListCellRenderer().toggleSelected(i);
        }
    }

    private ThreadListCellRenderer getThreadListCellRenderer()
    {
        ListCellRenderer<?> wrappee = ((ExpandedItemListCellRendererWrapper<?>) list.getCellRenderer()).getWrappee();
        return ((ThreadListCellRenderer) wrappee);
    }

    @Override
    public void toggleCluster(ThreadArtifactCluster cluster)
    {
        if (cluster == null) return;
        getThreadListCellRenderer().toggleCluster(cluster);
        repaint();
    }

    @Override
    public void setThreadArtifactClustering(final ThreadArtifactClustering threadArtifactClustering, final boolean retainCurrentSelection)
    {
        // I don't think that an implementation is necessary
    }

    @Override
    public void syncSelection(AThreadSelectable threadSelectable)
    {
        // TODO
    }

    @Override
    protected Set<AThreadArtifact> getThreadArtifacts(final boolean isSelected)
    {
        final boolean[] selected = getThreadListCellRenderer().getSelected();
        final Set<AThreadArtifact> threadArtifacts = new HashSet<>();
        final ThreadListModel model = (ThreadListModel) list.getModel();
        for (int i = 0; i < selected.length; i++)
        {
            if (selected[i] == isSelected)
            {
                final AThreadArtifact threadArtifactAt = model.getThreadArtifactAt(i);
                if (threadArtifactAt != null)
                {
                    threadArtifacts.add(threadArtifactAt);
                }
            }
        }
        return threadArtifacts;
    }
}
