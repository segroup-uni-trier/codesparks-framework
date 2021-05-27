/*
 * Copyright (c), Oliver Moseler, 2021
 */
package de.unitrier.st.codesparks.core.visualization.popup;

import com.intellij.openapi.application.ApplicationManager;
import com.intellij.util.ui.ThreeStateCheckBox;
import de.unitrier.st.codesparks.core.data.*;
import de.unitrier.st.codesparks.core.logging.UserActivityEnum;
import de.unitrier.st.codesparks.core.logging.UserActivityLogger;

import javax.swing.*;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreePath;
import javax.swing.tree.TreeSelectionModel;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.*;
import java.util.stream.Collectors;

public abstract class ThreadTree extends AThreadSelectable implements IThreadArtifactClusteringToMapTransformer
{
    protected final AArtifact artifact;
    //protected ThreadArtifactClustering threadArtifactClustering;
    protected final AMetricIdentifier metricIdentifier;
    protected final List<ThreadTreeLeafNode> leafNodes;
    protected final Map<List<AThreadArtifact>, ThreadTreeInnerNode> innerNodes;
    protected final DefaultMutableTreeNode root;

    protected ThreadTree(
            final AArtifact artifact
            , final ThreadArtifactClustering threadArtifactClustering
            , final AMetricIdentifier metricIdentifier
    )
    {
        this.artifact = artifact;
        //this.threadArtifactClustering = threadArtifactClustering;
        this.metricIdentifier = metricIdentifier;
        leafNodes = new ArrayList<>();
        innerNodes = new HashMap<>();
        root = new DefaultMutableTreeNode("Root");
        final JTree tree = new JTree()
        {
            @Override
            public void repaint()
            {
                super.repaint();
                innerNodes.values().forEach(innerNode -> {
                    innerNode.setState(retrieveInnerNodeState(innerNode));
                    innerNode.setUserObject(innerNode.getFullDisplayString());
                });
                updateAndRepaintRegisteredComponents();
            }
        };
        component = tree;

        // root has to be set before setting up the clustering
        setThreadArtifactClustering(threadArtifactClustering, false);

        tree.setModel(new DefaultTreeModel(root));
        tree.setToggleClickCount(Integer.MAX_VALUE);

        final TreeSelectionModel selectionModel = tree.getSelectionModel();
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
                final ColoredSelectableTreeNode lastPathComponent = (ColoredSelectableTreeNode) pathForLocation.getLastPathComponent();
                if (lastPathComponent == null)
                {
                    return;
                }
                lastPathComponent.toggleSelected();

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
                    // component equals tree
                    component.repaint();
                    component.updateUI();
                });
            }
        });
    }

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

    @Override
    public void setThreadArtifactClustering(final ThreadArtifactClustering threadArtifactClustering, final boolean retainCurrentSelection)
    {
        if (root == null)
        {
            return;
        }
        final Set<AThreadArtifact> currentSelection = getSelectedThreadArtifacts();

        root.removeAllChildren();
        innerNodes.clear();
        leafNodes.clear();

        final Map<String, List<AThreadArtifact>> threadTreeContent = transformClusteringToMap(artifact, threadArtifactClustering);
        final List<Map.Entry<String, List<AThreadArtifact>>> entries = new ArrayList<>(threadTreeContent.entrySet());
        entries.sort(Map.Entry.comparingByValue((o1, o2) -> {
                    double sum1 = o1.stream().mapToDouble((threadArtifact) -> threadArtifact.getNumericalMetricValue(metricIdentifier)).sum();
                    double sum2 = o2.stream().mapToDouble((threadArtifact) -> threadArtifact.getNumericalMetricValue(metricIdentifier)).sum();
                    return Double.compare(sum2, sum1);
                }
        ));


        for (final Map.Entry<String, List<AThreadArtifact>> entry : entries)
        {
            final List<AThreadArtifact> threadArtifacts = entry.getValue();
            if (threadArtifacts.isEmpty())
            {
                continue;
            }
            threadArtifacts.sort(ThreadArtifactComparator.getInstance(metricIdentifier));
            final ThreadTreeInnerNode innerNode = new ThreadTreeInnerNode(entry.getKey(), threadArtifacts, metricIdentifier);
            boolean isInnerNodeSelected = true;
            for (final AThreadArtifact threadArtifact : threadArtifacts)
            {
                final ThreadTreeLeafNode threadTreeLeafNode = new ThreadTreeLeafNode(threadArtifact, metricIdentifier);
                final boolean selected;
                if (retainCurrentSelection)
                {
                    selected = currentSelection.contains(threadArtifact);
                } else
                {
                    selected = threadArtifact.isSelected();
                }
                final ThreeStateCheckBox.State state = selected ? ThreeStateCheckBox.State.SELECTED : ThreeStateCheckBox.State.NOT_SELECTED;
                threadTreeLeafNode.setState(state);
                isInnerNodeSelected = isInnerNodeSelected && selected;
                leafNodes.add(threadTreeLeafNode);
                innerNode.add(threadTreeLeafNode);
            }
            innerNode.setState(retrieveInnerNodeState(innerNode));
            innerNodes.put(threadArtifacts, innerNode);
            root.add(innerNode);
        }

        component.updateUI();
        component.repaint();
    }

}