/*
 * Copyright (c) 2021. Oliver Moseler
 */
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
import java.util.List;
import java.util.Map;

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
    protected PopupPanel createPopupContent(final AArtifact artifact)
    {
        final PopupPanel popupPanel = new PopupPanel(new BorderLayout(), "DefaultThreadVisualizationPopup");

        threadSelectables.clear();

        final JBPanel<BorderLayoutPanel> centerPanel = new JBPanel<>();
        centerPanel.setLayout(new BoxLayout(centerPanel, BoxLayout.Y_AXIS));

        final JBTabbedPane tabbedPane = new JBTabbedPane();
        final IThreadSelectableIndexProvider indexProvider = tabbedPane::getSelectedIndex;

        ThreadArtifactClustering clustering =
                artifact.clusterThreadArtifacts(SmileKernelDensityClustering.getInstance(primaryMetricIdentifier));
        if (clustering.size() > 3)
        {
            clustering = artifact.clusterThreadArtifacts(ApacheKMeansPlusPlus.getInstance(primaryMetricIdentifier, 3));
        }

        final AThreadSelectable threadClustersTree = new ThreadClusterTree(clustering, primaryMetricIdentifier);
        threadSelectables.add(threadClustersTree);
        tabbedPane.addTab("Clusters", new JBScrollPane(threadClustersTree.getComponent()));


        //final Map<String, List<AThreadArtifact>> threadTypeLists = artifact.getThreadTypeLists();
        final AThreadSelectable threadTypesTree = new ThreadTypeTree(artifact, clustering, primaryMetricIdentifier);
        threadSelectables.add(threadTypesTree);
        // Register the observers -> they observe each other, i.e. a selection in one will be adopted to all other in the list
        threadClustersTree.setNext(threadTypesTree);
        threadTypesTree.setNext(threadClustersTree);

        tabbedPane.addTab("Types", new JBScrollPane(threadTypesTree.getComponent()));
        tabbedPane.setMinimumSize(new Dimension(400, 150));

        centerPanel.add(tabbedPane);

        final JBPanel<BorderLayoutPanel> buttonsPanel = new JBPanel<>();
        buttonsPanel.setLayout(new BoxLayout(buttonsPanel, BoxLayout.Y_AXIS));
        final JBPanel<BorderLayoutPanel> buttonsPanelWrapper = new JBPanel<>(new BorderLayout());

        /*
         ************************* At first the cluster buttons
         */

        final JBPanel<BorderLayoutPanel> clusterButtonsPanel = new JBPanel<>();
        clusterButtonsPanel.setLayout(new BoxLayout(clusterButtonsPanel, BoxLayout.X_AXIS));

        final VisualThreadClusterPropertiesManager propertiesManager = VisualThreadClusterPropertiesManager.getInstance(clustering);

        // Toggle cluster buttons.
        int clusterNum = 0;
        for (final ThreadArtifactCluster cluster : clustering)
        {
            if (cluster.isEmpty())
            {
                continue;
            }
            final VisualThreadClusterProperties properties = propertiesManager.getOrDefault(cluster, clusterNum);
            final JBColor foregroundColor = properties.getColor();
            final JButton clusterToggle =
                    new JButton(LocalizationUtil.getLocalizedString("codesparks.ui.overview.button.threads.togglecluster"));
            clusterToggle.setForeground(foregroundColor);
            clusterToggle.addActionListener(e -> {
                for (final IThreadSelectable threadSelectable : threadSelectables)
                {
                    threadSelectable.toggleCluster(cluster);
                }
            });
            final JBPanel<BorderLayoutPanel> clusterButtonWrapper = new JBPanel<>(new BorderLayout());
            clusterButtonWrapper.add(clusterToggle, BorderLayout.CENTER);
            clusterButtonsPanel.add(clusterButtonWrapper);
            clusterNum += 1;
        }

        // Add the cluster buttons panel
        final JBPanel<BorderLayoutPanel> clusterButtonsPanelWrapper = new JBPanel<>(new BorderLayout());
        clusterButtonsPanelWrapper.add(clusterButtonsPanel, BorderLayout.CENTER);
        buttonsPanel.add(clusterButtonsPanelWrapper);

        /*
         ***************** The selection buttons
         */

        final JBPanel<BorderLayoutPanel> selectionButtonsPanel = new JBPanel<>();
        selectionButtonsPanel.setLayout(new BoxLayout(selectionButtonsPanel, BoxLayout.X_AXIS));

        // Deselect all button
        final JButton deselectAll = new JButton(LocalizationUtil.getLocalizedString("codesparks.ui.popup.deselectallbutton"));
        deselectAll.addActionListener(e -> {
            for (IThreadSelectable threadSelectable : this.threadSelectables)
            {
                threadSelectable.deselectAll();
            }

        });
        final JBPanel<BorderLayoutPanel> deselectAllButtonWrapper = new JBPanel<>(new BorderLayout());
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
        final JBPanel<BorderLayoutPanel> selectAllButtonWrapper = new JBPanel<>(new BorderLayout());
        selectAllButtonWrapper.add(selectAll, BorderLayout.CENTER);
        selectionButtonsPanel.add(selectAllButtonWrapper);

        final JButton invert = new JButton(LocalizationUtil.getLocalizedString("codesparks.ui.popup.invertallbutton"));
        invert.addActionListener(e -> {
            for (IThreadSelectable threadSelectable : this.threadSelectables)
            {
                threadSelectable.invertAll();
            }
        });
        final JBPanel<BorderLayoutPanel> invertButtonWrapper = new JBPanel<>(new BorderLayout());
        invertButtonWrapper.add(invert, BorderLayout.CENTER);
        selectionButtonsPanel.add(invertButtonWrapper);

        final JBPanel<BorderLayoutPanel> selectionButtonsPanelWrapper = new JBPanel<>(new BorderLayout());
        selectionButtonsPanelWrapper.add(selectionButtonsPanel, BorderLayout.CENTER);
        buttonsPanel.add(selectionButtonsPanelWrapper);

        /*
         ******************** The control buttons
         */

        final JBPanel<BorderLayoutPanel> controlButtonsPanel = new JBPanel<>();
        controlButtonsPanel.setLayout(new BoxLayout(controlButtonsPanel, BoxLayout.X_AXIS));

        // Reset thread filter global button
        final JButton resetThreadFilterGlobal = new JButton(
                LocalizationUtil.getLocalizedString("codesparks.ui.button.reset.thread.filter.global"));
        resetThreadFilterGlobal.addActionListener(e -> {
            popupPanel.cancelPopup();
            CodeSparksFlowManager.getInstance().getCurrentCodeSparksFlow().applyThreadArtifactFilter(GlobalResetThreadArtifactFilter.getInstance());
        });
        final JBPanel<BorderLayoutPanel> resetThreadFilterGlobalButtonWrapper = new JBPanel<>(new BorderLayout());
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
        final JBPanel<BorderLayoutPanel> applyThreadFilterButtonWrapper = new JBPanel<>(new BorderLayout());
        applyThreadFilterButtonWrapper.add(applyThreadFilter, BorderLayout.CENTER);
        controlButtonsPanel.add(applyThreadFilterButtonWrapper);

        /*
         **************
         */

        // Add the control buttons panel to the parent buttons panel
        final JBPanel<BorderLayoutPanel> controlButtonsPanelWrapper = new JBPanel<>(new BorderLayout());
        controlButtonsPanelWrapper.add(controlButtonsPanel, BorderLayout.CENTER);
        buttonsPanel.add(controlButtonsPanelWrapper);

        buttonsPanelWrapper.add(buttonsPanel, BorderLayout.CENTER);
        centerPanel.add(buttonsPanelWrapper);
        popupPanel.add(centerPanel, BorderLayout.CENTER);

        //popupPanel.add(applyThreadFilter, BorderLayout.SOUTH);
        return popupPanel;
    }

    @Override
    protected String createPopupTitle(final AArtifact artifact)
    {
        final Map<String, List<AThreadArtifact>> threadTypeLists = artifact.getThreadTypeLists();
        return "Total number of threads: " + artifact.getNumberOfThreads() +
                " | Different thread types: " + (threadTypeLists == null ? 0 : threadTypeLists.size());
    }
}
