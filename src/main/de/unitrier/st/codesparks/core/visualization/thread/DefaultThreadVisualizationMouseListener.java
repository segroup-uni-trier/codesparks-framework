package de.unitrier.st.codesparks.core.visualization.thread;

import com.intellij.ui.JBColor;
import com.intellij.ui.components.JBPanel;
import com.intellij.ui.components.JBScrollPane;
import com.intellij.ui.components.JBTabbedPane;
import com.intellij.util.ui.components.BorderLayoutPanel;
import de.unitrier.st.codesparks.core.CodeSparksFlowManager;
import de.unitrier.st.codesparks.core.data.*;
import de.unitrier.st.codesparks.core.localization.LocalizationUtil;
import de.unitrier.st.codesparks.core.visualization.AArtifactVisualizationMouseListener;
import de.unitrier.st.codesparks.core.visualization.popup.*;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/*
 * Copyright (c), Oliver Moseler, 2020
 */
public class DefaultThreadVisualizationMouseListener extends AArtifactVisualizationMouseListener
{
    private final List<IThreadSelectable> threadSelectables;

    DefaultThreadVisualizationMouseListener(
            final JComponent component
            , final AArtifact artifact
            , final AMetricIdentifier primaryMetricIdentifier
    )
    {
        super(component, new Dimension(520, 170), artifact, primaryMetricIdentifier);
        this.threadSelectables = new ArrayList<>();
    }

    @Override
    protected PopupPanel createPopupContent(AArtifact artifact)
    {
        final PopupPanel popupPanel = new PopupPanel(new BorderLayout(), "DefaultThreadVisualizationPopup");

        threadSelectables.clear();

        JBPanel<BorderLayoutPanel> centerPanel = new JBPanel<>();
        centerPanel.setLayout(new BoxLayout(centerPanel, BoxLayout.Y_AXIS));

        final JBTabbedPane tabbedPane = new JBTabbedPane();
        final IThreadSelectableIndexProvider indexProvider = tabbedPane::getSelectedIndex;

        final ThreadArtifactClustering sortedDefaultThreadArtifactClustering = artifact.getSortedDefaultThreadArtifactClustering(primaryMetricIdentifier);
        Map<String, List<AThreadArtifact>> map = new HashMap<>();
        int clusterId = 1;
        for (ThreadArtifactCluster threadArtifacts : sortedDefaultThreadArtifactClustering)
        {
            map.put("Cluster:" + clusterId++, threadArtifacts);
        }

        AThreadSelectable threadClustersTree = new ThreadClusterTree(map, primaryMetricIdentifier);
        threadSelectables.add(threadClustersTree);
        tabbedPane.addTab("Clusters", new JBScrollPane(threadClustersTree.getComponent()));


        final Map<String, List<AThreadArtifact>> threadTypeLists = artifact.getThreadTypeLists();
        AThreadSelectable threadTypesTree = new ThreadTypeTree(threadTypeLists, primaryMetricIdentifier,
                sortedDefaultThreadArtifactClustering);
        threadSelectables.add(threadTypesTree);
        // Register the observers -> they observe each other, i.e. a selection in one will be adopted to all other in the list
        threadClustersTree.setNext(threadTypesTree);
        threadTypesTree.setNext(threadClustersTree);

        tabbedPane.addTab("Types", new JBScrollPane(threadTypesTree.getComponent()));
        tabbedPane.setMinimumSize(new Dimension(400, 150));

        centerPanel.add(tabbedPane);

        JBPanel<BorderLayoutPanel> buttonsPanel = new JBPanel<>();
        buttonsPanel.setLayout(new BoxLayout(buttonsPanel, BoxLayout.Y_AXIS));
        JBPanel<BorderLayoutPanel> buttonsPanelWrapper = new JBPanel<>(new BorderLayout());

        /*
         ************************* At first the cluster buttons
         */

        JBPanel<BorderLayoutPanel> clusterButtonsPanel = new JBPanel<>();
        clusterButtonsPanel.setLayout(new BoxLayout(clusterButtonsPanel, BoxLayout.X_AXIS));

        // Toggle cluster buttons.
        for (ThreadArtifactCluster cluster : sortedDefaultThreadArtifactClustering)
        {
            if (cluster.isEmpty())
            {
                continue;
            }
            VisualThreadClusterProperties properties =
                    VisualThreadClusterPropertiesManager.getInstance().getProperties(cluster);
            Color foregroundColor;
            if (properties == null)
            {
                foregroundColor = JBColor.BLACK;
            } else
            {
                foregroundColor = properties.getColor();
            }
            final JButton clusterToggle =
                    new JButton(LocalizationUtil.getLocalizedString("codesparks.ui.overview.button.threads.togglecluster"));
            clusterToggle.setForeground(foregroundColor);
            clusterToggle.addActionListener(e -> {
                for (final IThreadSelectable threadSelectable : threadSelectables)
                {
                    threadSelectable.toggleCluster(cluster);
                }

            });
            JBPanel<BorderLayoutPanel> clusterButtonWrapper = new JBPanel<>(new BorderLayout());
            clusterButtonWrapper.add(clusterToggle, BorderLayout.CENTER);
            clusterButtonsPanel.add(clusterButtonWrapper);
        }

        // Add the cluster buttons panel
        JBPanel<BorderLayoutPanel> clusterButtonsPanelWrapper = new JBPanel<>(new BorderLayout());
        clusterButtonsPanelWrapper.add(clusterButtonsPanel, BorderLayout.CENTER);
        buttonsPanel.add(clusterButtonsPanelWrapper);

        /*
         ***************** The selection buttons
         */

        JBPanel<BorderLayoutPanel> selectionButtonsPanel = new JBPanel<>();
        selectionButtonsPanel.setLayout(new BoxLayout(selectionButtonsPanel, BoxLayout.X_AXIS));

        // Deselect all button
        final JButton deselectAll = new JButton(LocalizationUtil.getLocalizedString("codesparks.ui.popup.deselectallbutton"));
        deselectAll.addActionListener(e -> {
            for (IThreadSelectable threadSelectable : this.threadSelectables)
            {
                threadSelectable.deselectAll();
            }

        });
        JBPanel<BorderLayoutPanel> deselectAllButtonWrapper = new JBPanel<>(new BorderLayout());
        deselectAllButtonWrapper.add(deselectAll, BorderLayout.CENTER);
        selectionButtonsPanel.add(deselectAllButtonWrapper);

        // Select all button
        final JButton selectAll = new JButton(LocalizationUtil.getLocalizedString("codesparks.ui.popup.selectallbutton"));
        selectAll.addActionListener(e -> {
            for (IThreadSelectable threadSelectable : this.threadSelectables)
            {
                threadSelectable.selectAll();
            }
        });
        JBPanel<BorderLayoutPanel> selectAllButtonWrapper = new JBPanel<>(new BorderLayout());
        selectAllButtonWrapper.add(selectAll, BorderLayout.CENTER);
        selectionButtonsPanel.add(selectAllButtonWrapper);

        JButton invert = new JButton(LocalizationUtil.getLocalizedString("codesparks.ui.popup.invertallbutton"));
        invert.addActionListener(e -> {
            for (IThreadSelectable threadSelectable : this.threadSelectables)
            {
                threadSelectable.invertAll();
            }
        });
        JBPanel<BorderLayoutPanel> invertButtonWrapper = new JBPanel<>(new BorderLayout());
        invertButtonWrapper.add(invert, BorderLayout.CENTER);
        selectionButtonsPanel.add(invertButtonWrapper);

        JBPanel<BorderLayoutPanel> selectionButtonsPanelWrapper = new JBPanel<>(new BorderLayout());
        selectionButtonsPanelWrapper.add(selectionButtonsPanel, BorderLayout.CENTER);
        buttonsPanel.add(selectionButtonsPanelWrapper);

        /*
         ******************** The control buttons
         */

        JBPanel<BorderLayoutPanel> controlButtonsPanel = new JBPanel<>();
        controlButtonsPanel.setLayout(new BoxLayout(controlButtonsPanel, BoxLayout.X_AXIS));

        // Reset thread filter global button
        final JButton resetThreadFilterGlobal = new JButton(
                LocalizationUtil.getLocalizedString("codesparks.ui.button.reset.thread.filter.global"));
        resetThreadFilterGlobal.addActionListener(e -> {
            popupPanel.cancelPopup();
            CodeSparksFlowManager.getInstance().getCurrentCodeSparksFlow().applyThreadArtifactFilter(GlobalResetThreadArtifactFilter.getInstance());
        });
        JBPanel<BorderLayoutPanel> resetThreadFilterGlobalButtonWrapper = new JBPanel<>(new BorderLayout());
        resetThreadFilterGlobalButtonWrapper.add(resetThreadFilterGlobal, BorderLayout.CENTER);
        controlButtonsPanel.add(resetThreadFilterGlobalButtonWrapper);

        // Apply thread filter button.
        final JButton applyThreadFilter =
                new JButton(LocalizationUtil.getLocalizedString("codesparks.ui.popup.button.apply.thread.filter"));
        applyThreadFilter.addActionListener(e -> {
            popupPanel.cancelPopup();
            int index = indexProvider.getThreadSelectableIndex();
            IThreadSelectable iThreadSelectable = threadSelectables.get(index);
            final IThreadArtifactFilter threadArtifactFilter = new DefaultThreadArtifactFilter(iThreadSelectable);
            CodeSparksFlowManager.getInstance().getCurrentCodeSparksFlow().applyThreadArtifactFilter(threadArtifactFilter);
        });
        JBPanel<BorderLayoutPanel> applyThreadFilterButtonWrapper = new JBPanel<>(new BorderLayout());
        applyThreadFilterButtonWrapper.add(applyThreadFilter, BorderLayout.CENTER);
        controlButtonsPanel.add(applyThreadFilterButtonWrapper);

        /*
        **************
         */

        // Add the control buttons panel to the parent buttons panel
        JBPanel<BorderLayoutPanel> controlButtonsPanelWrapper = new JBPanel<>(new BorderLayout());
        controlButtonsPanelWrapper.add(controlButtonsPanel, BorderLayout.CENTER);
        buttonsPanel.add(controlButtonsPanelWrapper);

        buttonsPanelWrapper.add(buttonsPanel, BorderLayout.CENTER);
        centerPanel.add(buttonsPanelWrapper);
        popupPanel.add(centerPanel, BorderLayout.CENTER);

        //popupPanel.add(applyThreadFilter, BorderLayout.SOUTH);
        return popupPanel;
    }

    @Override
    protected String createPopupTitle(AArtifact artifact)
    {
        Map<String, List<AThreadArtifact>> threadTypeLists = artifact.getThreadTypeLists();
        return "Total number of threads: " + artifact.getNumberOfThreads() +
                " | Different thread types: " + (threadTypeLists == null ? 0 : threadTypeLists.size());
    }

//    @Override
//    protected String createPopupTitle(AArtifact artifact)
//    {
//        return createPopupTitle(artifact);
////        Map<String, List<AThreadArtifact>> threadTypeLists = artifact.getThreadTypeLists();
////        return "Total number of threads: " + artifact.getNumberOfThreads() +
////                " | Different thread types: " + (threadTypeLists == null ? 0 : threadTypeLists.size());
//    }
}
