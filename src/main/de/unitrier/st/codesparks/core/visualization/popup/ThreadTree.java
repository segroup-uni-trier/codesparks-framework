package de.unitrier.st.codesparks.core.visualization.popup;

import com.intellij.openapi.application.ApplicationManager;
import com.intellij.util.ui.ThreeStateCheckBox;
import de.unitrier.st.codesparks.core.data.AThreadArtifact;
import de.unitrier.st.codesparks.core.data.AMetricIdentifier;
import de.unitrier.st.codesparks.core.logging.UserActivityEnum;
import de.unitrier.st.codesparks.core.logging.UserActivityLogger;
import de.unitrier.st.codesparks.core.data.ThreadArtifactCluster;
import de.unitrier.st.codesparks.core.data.ThreadArtifactComparator;

import javax.swing.*;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreePath;
import javax.swing.tree.TreeSelectionModel;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.List;
import java.util.*;
import java.util.stream.Collectors;

/*
 * Copyright (c), Oliver Moseler, 2020
 */
public class ThreadTree extends AThreadSelectable
{
    protected final List<ThreadTreeLeafNode> leafNodes;
    protected final Map<List<AThreadArtifact>, ThreadTreeInnerNode> innerNodes;

    public ThreadTree(
            final Map<String, List<AThreadArtifact>> threadTreeContent
            , final AMetricIdentifier metricIdentifier
    )
    {
        leafNodes = new ArrayList<>();
        innerNodes = new HashMap<>();
        JTree tree = new JTree()
        {
            @Override
            public void repaint()
            {
                super.repaint();
//                if (innerNodes != null)
//                {
                innerNodes.values().forEach(innerNode -> {
                    innerNode.setState(retrieveInnerNodeState(innerNode));
                    innerNode.setUserObject(innerNode.getFullDisplayString());
                });
//                }
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
        component = tree;

        DefaultMutableTreeNode root = new DefaultMutableTreeNode("Root");

        List<Map.Entry<String, List<AThreadArtifact>>> entries = new ArrayList<>(threadTreeContent.entrySet());
        entries.sort(Map.Entry.comparingByValue((o1, o2) -> {
                    double sum1 = o1.stream().mapToDouble((codeSparksThread) -> codeSparksThread.getNumericalMetricValue(metricIdentifier)).sum();
                    double sum2 = o2.stream().mapToDouble((codeSparksThread) -> codeSparksThread.getNumericalMetricValue(metricIdentifier)).sum();
                    return Double.compare(sum2, sum1);
//                    if (sum1 > sum2) return -1;
//                    if (sum1 < sum2) return 1;
//                    return 0;
                }
        ));

        for (Map.Entry<String, List<AThreadArtifact>> entry : entries)
        {
            if (entry.getValue().isEmpty()) continue;
            List<AThreadArtifact> codeSparksThreads = entry.getValue();
            codeSparksThreads.sort(new ThreadArtifactComparator(metricIdentifier));
            ThreadTreeInnerNode innerNode = new ThreadTreeInnerNode(entry.getKey(), codeSparksThreads, metricIdentifier);
            boolean isInnerNodeSelected = true;
            for (AThreadArtifact codeSparksThread : codeSparksThreads)
            {
                ThreadTreeLeafNode threadTreeLeafNode = new ThreadTreeLeafNode(codeSparksThread, metricIdentifier);
                boolean filtered = codeSparksThread.isFiltered();
                ThreeStateCheckBox.State state = filtered ? ThreeStateCheckBox.State.NOT_SELECTED : ThreeStateCheckBox.State.SELECTED;
                threadTreeLeafNode.setState(state);
                isInnerNodeSelected = isInnerNodeSelected && !filtered;
                leafNodes.add(threadTreeLeafNode);
                innerNode.add(threadTreeLeafNode);
            }
            innerNode.setState(retrieveInnerNodeState(innerNode));
            innerNodes.put(codeSparksThreads, innerNode);
            root.add(innerNode);
        }

        tree.setModel(new DefaultTreeModel(root));
        tree.setToggleClickCount(Integer.MAX_VALUE);

        TreeSelectionModel selectionModel = tree.getSelectionModel();
        selectionModel.setSelectionMode(TreeSelectionModel.SINGLE_TREE_SELECTION);
        tree.setRootVisible(false);

        tree.setCellRenderer(new ThreadTreeCellRenderer());

        tree.addMouseListener(new MouseAdapter()
        {
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
//                ApplicationManager.getApplication().invokeLater(() -> {
//                    component.repaint();
//                });
//                ApplicationManager.getApplication().invokeLater(tree::updateUI);

                final boolean selected = lastPathComponent.isSelected();
                final String s = !selected ? "disabled" : "enabled";
                if (lastPathComponent.isLeaf())
                {
                    UserActivityLogger.getInstance().log(UserActivityEnum.ThreadTreeNodeToggled, s,
                            ((ThreadTreeLeafNode) lastPathComponent).getThreadArtifact().getIdentifier());
                } else
                {
                    UserActivityLogger.getInstance().log(UserActivityEnum.ThreadTreeNodeToggled, s,
                            lastPathComponent.toString());
                }

                propagateSelection();

                ApplicationManager.getApplication().invokeLater(() -> {
                    component.repaint();

                    tree.updateUI();
                });

                //tree.updateUI();
            }
        });
    }

//    private String retrieveSelectedChildrenString(ThreadTreeInnerNode innerNode/*, String str*/)
//    {
//        int childCount = innerNode.getChildCount();
//        int selectedCount = 0;
//        for (int i = 0; i < childCount; i++)
//        {
//            ColoredSelectableTreeNode childAt = (ColoredSelectableTreeNode) innerNode.getChildAt(i);
//            if (childAt.isSelected()) selectedCount++;
//        }
//        return /*str + */"(" + selectedCount + "/" + childCount + ")";
//    }

    private ThreeStateCheckBox.State retrieveInnerNodeState(ThreadTreeInnerNode innerNode)
    {
        int childCount = innerNode.getChildCount();
        boolean allSelected = true;
        boolean allDeselected = true;
        for (int i = 0; i < childCount; i++)
        {
            ColoredSelectableTreeNode childAt = (ColoredSelectableTreeNode) innerNode.getChildAt(i);
            allSelected = allSelected && childAt.isSelected();
            allDeselected = allDeselected && !childAt.isSelected();
        }
        if (!allSelected && !allDeselected)
        {
            return ThreeStateCheckBox.State.DONT_CARE;
        }
        if (allSelected) return ThreeStateCheckBox.State.SELECTED;
        return ThreeStateCheckBox.State.NOT_SELECTED;
    }

    @Override
    public void syncSelection(AThreadSelectable threadSelectable)
    {
        Set<String> selectedThreadArtifactIdentifiers = threadSelectable.getSelectedThreadArtifactIdentifiers();
        Set<String> filteredThreadArtifactIdentifiers = threadSelectable.getFilteredThreadArtifactIdentifiers();
        for (ThreadTreeLeafNode leafNode : leafNodes)
        {
            String identifier = leafNode.getThreadArtifact().getIdentifier();
            if (selectedThreadArtifactIdentifiers.contains(identifier))
            {
                leafNode.setState(ThreeStateCheckBox.State.SELECTED);
            }
            if (filteredThreadArtifactIdentifiers.contains(identifier))
            {
                leafNode.setState(ThreeStateCheckBox.State.NOT_SELECTED);
            }
        }
    }

    @Override
    protected Set<AThreadArtifact> getThreadArtifacts(final boolean isSelected)
    {
        return leafNodes.
                stream()
                .filter(threadTreeLeafNode -> threadTreeLeafNode.isSelected() == isSelected)
                .map(ThreadTreeLeafNode::getThreadArtifact)
                .collect(Collectors.toSet());
    }

    @Override
    public void deselectAll()
    {
        leafNodes.forEach(threadTreeLeafNode -> threadTreeLeafNode.setState(ThreeStateCheckBox.State.NOT_SELECTED));
        innerNodes.values()
                .forEach(threadTreeInnerNode -> threadTreeInnerNode.setState(ThreeStateCheckBox.State.NOT_SELECTED));
        repaint();
    }

    @Override
    public void selectAll()
    {
        leafNodes.forEach(threadTreeLeafNode -> threadTreeLeafNode.setState(ThreeStateCheckBox.State.SELECTED));
        innerNodes.values()
                .forEach(threadTreeInnerNode -> threadTreeInnerNode.setState(ThreeStateCheckBox.State.SELECTED));
        repaint();
    }

    @Override
    public void invertAll()
    {
        leafNodes.forEach(ColoredSelectableTreeNode::toggleSelected);
        repaint();
    }

    @Override
    public void toggleCluster(ThreadArtifactCluster cluster)
    {
        if (cluster == null) return;
        if (innerNodes == null) return;
        final ThreadTreeInnerNode threadTreeInnerNode = innerNodes.get(cluster);
        if (threadTreeInnerNode == null) return;
        threadTreeInnerNode.toggleSelected();
        repaint();
    }
}