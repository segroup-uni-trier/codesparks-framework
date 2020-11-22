package de.unitrier.st.codesparks.core.visualization.popup;

import com.intellij.ui.ExpandedItemListCellRendererWrapper;
import com.intellij.ui.components.JBCheckBox;
import com.intellij.ui.components.JBList;
import de.unitrier.st.codesparks.core.data.AArtifact;
import de.unitrier.st.codesparks.core.data.ACodeSparksThread;
import de.unitrier.st.codesparks.core.data.CodeSparksThreadCluster;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.HashSet;
import java.util.Set;

public class ThreadList extends AThreadSelectable
{
    private final JBList<JBCheckBox> list;

    public ThreadList(AArtifact artifact, final String metricIdentifier)
    {
        list = new JBList<JBCheckBox>(new ThreadListModel(artifact, metricIdentifier))
        {
            @Override
            public String getToolTipText(MouseEvent event)
            {
                Point point = event.getPoint();
                int index = locationToIndex(point);
                ACodeSparksThread codeSparksThreadAt = ((ThreadListModel) getModel()).getThreadArtifactAt(index);
                if (codeSparksThreadAt == null)
                {
                    return "";
                }
                String identifier = codeSparksThreadAt.getIdentifier();
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
        final ThreadListCellRenderer threadListCellRenderer = new ThreadListCellRenderer(artifact, metricIdentifier);
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
    public void toggleCluster(CodeSparksThreadCluster cluster)
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
    protected Set<ACodeSparksThread> getThreadArtifacts(final boolean isSelected)
    {
        boolean[] selected = getThreadListCellRenderer().getSelected();
        final Set<ACodeSparksThread> codeSparksThreads = new HashSet<>();
        ThreadListModel model = (ThreadListModel) list.getModel();
        for (int i = 0; i < selected.length; i++)
        {
            if (selected[i] == isSelected)
            {
                ACodeSparksThread codeSparksThreadAt = model.getThreadArtifactAt(i);
                if (codeSparksThreadAt != null)
                {
                    codeSparksThreads.add(codeSparksThreadAt);
                }
            }
        }
        return codeSparksThreads;
    }
}
