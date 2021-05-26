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

/*
 * Copyright (c), Oliver Moseler, 2021
 */
public class SimpleThreadVisualizationMouseListener extends AArtifactVisualizationMouseListener
{
    //private final List<IThreadSelectable> threadSelectables;

    SimpleThreadVisualizationMouseListener(
            final JComponent component
            , final AArtifact artifact
            , final AMetricIdentifier primaryMetricIdentifier
    )
    {
        super(component, new Dimension(520, 170), artifact, primaryMetricIdentifier);
        //  this.threadSelectables = new ArrayList<>();
    }

    @Override
    protected PopupPanel createPopupContent(AArtifact artifact)
    {
        final PopupPanel popupPanel = new PopupPanel(new BorderLayout(), "DefaultThreadVisualizationPopup");

        final JBPanel<BorderLayoutPanel> centerPanel = new JBPanel<>();
        centerPanel.setLayout(new BoxLayout(centerPanel, BoxLayout.Y_AXIS));

        final JBTabbedPane tabbedPane = new JBTabbedPane();

        final ThreadArtifactClustering sortedDefaultThreadArtifactClustering =
                artifact.clusterThreadArtifacts(ConstraintKMeansWithAMaximumOfThreeClusters.getInstance(primaryMetricIdentifier), true);
//                artifact.getSortedConstraintKMeansWithAMaximumOfThreeClustersThreadArtifactClustering(primaryMetricIdentifier);

        final ThreadList threadList = new ThreadList(artifact, primaryMetricIdentifier);
        tabbedPane.addTab("Threads", new JBScrollPane(threadList.getComponent()));

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
