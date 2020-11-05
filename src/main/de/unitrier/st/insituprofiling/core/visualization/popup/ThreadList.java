package de.unitrier.st.insituprofiling.core.visualization.popup;

import com.intellij.ui.ExpandedItemListCellRendererWrapper;
import com.intellij.ui.components.JBCheckBox;
import com.intellij.ui.components.JBList;
import de.unitrier.st.insituprofiling.core.data.AProfilingArtifact;
import de.unitrier.st.insituprofiling.core.data.ThreadArtifact;
import de.unitrier.st.insituprofiling.core.data.ThreadArtifactCluster;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.HashSet;
import java.util.Set;

public class ThreadList extends AThreadSelectable
{
    private final JBList<JBCheckBox> list;

    public ThreadList(AProfilingArtifact artifact)
    {
        list = new JBList<JBCheckBox>(new ThreadListModel(artifact))
        {
            @Override
            public String getToolTipText(MouseEvent event)
            {
                Point point = event.getPoint();
                int index = locationToIndex(point);
                ThreadArtifact threadArtifactAt = ((ThreadListModel) getModel()).getThreadArtifactAt(index);
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
                if (componentsToRepaintOnSelection == null)
                {
                    return;
                }
                for (Component component : componentsToRepaintOnSelection)
                {
                    component.repaint();
                }
            }
        };
        component = list;
        final ThreadListCellRenderer threadListCellRenderer = new ThreadListCellRenderer(artifact);
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
    public void invertAll() {
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
    public void syncSelection(AThreadSelectable threadSelectable)
    {
        // TODO
    }

    @Override
    protected Set<ThreadArtifact> getThreadArtifacts(final boolean isSelected)
    {
        boolean[] selected = getThreadListCellRenderer().getSelected();
        final Set<ThreadArtifact> threadArtifacts = new HashSet<>();
        ThreadListModel model = (ThreadListModel) list.getModel();
        for (int i = 0; i < selected.length; i++)
        {
            if (selected[i] == isSelected)
            {
                ThreadArtifact threadArtifactAt = model.getThreadArtifactAt(i);
                if (threadArtifactAt != null)
                {
                    threadArtifacts.add(threadArtifactAt);
                }
            }
        }
        return threadArtifacts;
    }
}
