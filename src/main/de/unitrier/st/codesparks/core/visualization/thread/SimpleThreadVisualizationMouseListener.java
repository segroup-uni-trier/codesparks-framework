/*
 * Copyright (c) 2021. Oliver Moseler
 */
package de.unitrier.st.codesparks.core.visualization.thread;

import com.intellij.ui.components.JBPanel;
import com.intellij.ui.components.JBScrollPane;
import com.intellij.ui.components.JBTabbedPane;
import com.intellij.util.ui.components.BorderLayoutPanel;
import de.unitrier.st.codesparks.core.data.*;
import de.unitrier.st.codesparks.core.visualization.AArtifactVisualizationMouseListener;
import de.unitrier.st.codesparks.core.visualization.popup.AThreadSelectable;
import de.unitrier.st.codesparks.core.visualization.popup.PopupPanel;
import de.unitrier.st.codesparks.core.visualization.popup.ThreadList;
import de.unitrier.st.codesparks.core.visualization.popup.ThreadTypeTree;

import javax.swing.*;
import java.awt.*;
import java.util.List;
import java.util.Map;

public class SimpleThreadVisualizationMouseListener extends AArtifactVisualizationMouseListener
{
    SimpleThreadVisualizationMouseListener(
            final JComponent component
            , final AArtifact artifact
            , final AMetricIdentifier primaryMetricIdentifier
    )
    {
        super(component, new Dimension(520, 170), artifact, primaryMetricIdentifier);
    }

    @Override
    protected PopupPanel createPopupContent(AArtifact artifact)
    {
        final PopupPanel popupPanel = new PopupPanel("SimpleThreadVisualizationPopup");

        final JBPanel<BorderLayoutPanel> centerPanel = new JBPanel<>();
        centerPanel.setLayout(new BoxLayout(centerPanel, BoxLayout.Y_AXIS));

        final JBTabbedPane tabbedPane = new JBTabbedPane();

        final ThreadArtifactClustering sortedDefaultThreadArtifactClustering =
                artifact.getSelectedClusteringOrApplyAndSelect(ConstraintKMeansWithAMaximumOfThreeClusters.getInstance(primaryMetricIdentifier));

        final ThreadList threadList = new ThreadList(artifact, primaryMetricIdentifier, false);
        tabbedPane.addTab("Threads", new JBScrollPane(threadList.getComponent()));

        final ThreadList threadListCluster = new ThreadList(artifact, primaryMetricIdentifier);
        tabbedPane.addTab("Threads-Colored-by-Cluster", new JBScrollPane(threadListCluster.getComponent()));

        //final Map<String, List<AThreadArtifact>> threadTypeLists = artifact.getThreadTypeLists();
        final AThreadSelectable threadTypesTree = new ThreadTypeTree(artifact, sortedDefaultThreadArtifactClustering, primaryMetricIdentifier);

        tabbedPane.addTab("Types", new JBScrollPane(threadTypesTree.getComponent()));
        tabbedPane.setMinimumSize(new Dimension(400, 150));

        centerPanel.add(tabbedPane);

        popupPanel.add(centerPanel, BorderLayout.CENTER);

        return popupPanel;
    }

    @Override
    protected String createPopupTitle(AArtifact artifact)
    {
        Map<String, List<AThreadArtifact>> threadTypeLists = artifact.getThreadTypeLists();
        return "Total number of threads: " + artifact.getNumberOfThreads() +
                " | Different thread types: " + (threadTypeLists == null ? 0 : threadTypeLists.size());
    }
}
