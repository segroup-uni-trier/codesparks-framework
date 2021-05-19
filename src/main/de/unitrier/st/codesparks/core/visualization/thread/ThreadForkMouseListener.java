/*
 * Copyright (c) 2021. Oliver Moseler
 */
package de.unitrier.st.codesparks.core.visualization.thread;

import com.intellij.openapi.ui.ComboBox;
import com.intellij.ui.components.JBLabel;
import com.intellij.ui.components.JBPanel;
import com.intellij.ui.components.JBScrollPane;
import com.intellij.ui.components.JBTabbedPane;
import com.intellij.util.ui.components.BorderLayoutPanel;
import de.unitrier.st.codesparks.core.CodeSparksFlowManager;
import de.unitrier.st.codesparks.core.CoreUtil;
import de.unitrier.st.codesparks.core.data.*;
import de.unitrier.st.codesparks.core.localization.LocalizationUtil;
import de.unitrier.st.codesparks.core.visualization.AArtifactVisualizationMouseListener;
import de.unitrier.st.codesparks.core.visualization.VisConstants;
import de.unitrier.st.codesparks.core.visualization.popup.*;

import javax.swing.*;
import javax.swing.border.Border;
import java.awt.*;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.util.List;
import java.util.*;

public class ThreadForkMouseListener extends AArtifactVisualizationMouseListener implements IClusterHoverable
{
    private IThreadSelectableIndexProvider selectableIndexProvider;
    private final List<IThreadSelectable> threadSelectables;
    private final IThreadArtifactsDisplayDataProvider threadArtifactsDisplayDataProvider;
    private final JLabel[] leftHoverLabels;
    private final JLabel[] rightHoverLabels;

    ThreadForkMouseListener(
            final JComponent component
            , final AArtifact artifact
            , final AMetricIdentifier primaryMetricIdentifier
            , final IThreadArtifactsDisplayDataProvider threadArtifactsDisplayDataProvider
    )
    {
        super(component, new Dimension(760, 170), artifact, primaryMetricIdentifier);
        this.threadSelectables = new ArrayList<>(2);
        this.threadArtifactsDisplayDataProvider = threadArtifactsDisplayDataProvider;
        this.leftHoverLabels = new JLabel[2];
        this.rightHoverLabels = new JLabel[2];
    }

    private static class ComboBoxItem
    {
        private final int k;
        private final String text;

        ComboBoxItem(final int k, final String text)
        {
            this.k = k;
            this.text = text;
        }

        @Override
        public String toString()
        {
            return text;
        }
    }

    @Override
    protected PopupPanel createPopupContent(final AArtifact artifact)
    {
        final PopupPanel popupPanel = new PopupPanel();
        popupPanel.setLayout(new BoxLayout(popupPanel, BoxLayout.Y_AXIS));

        threadSelectables.clear();
        final Collection<JComponent> componentsToRegisterToTheThreadSelectables = new ArrayList<>();

        ThreadArtifactClustering threadArtifactClustering =
                artifact.getThreadArtifactClustering(SmileKernelDensityClustering.getInstance(primaryMetricIdentifier));

        final int actualNumberOfClustersOfTheDensityEstimation = threadArtifactClustering.size();

        // Objects that need to be declared already because they will be used in inner classes.
        final JBTabbedPane selectablesTabbedPane = new JBTabbedPane();
        selectableIndexProvider = selectablesTabbedPane::getSelectedIndex;

        /*
         * The zoomed viz tabbed pane
         */
        final JBTabbedPane zoomedVizTabbedPane = new JBTabbedPane();
        KernelBasedDensityEstimationPanel kernelBasedDensityEstimationPanel = null;
        ZoomedThreadFork zoomedThreadFork = null;

        if (actualNumberOfClustersOfTheDensityEstimation <= 6)
        { // Only show a fork when there are up to six thread classifications.
            if (actualNumberOfClustersOfTheDensityEstimation > 3)
            { // Because the in-situ viz has changed to a k=3 clustering in that case, this will be the clustering we show first.
                threadArtifactClustering = artifact.getThreadArtifactClustering(ApacheKMeans.getInstance(primaryMetricIdentifier, 3));
            }
            final ZoomedThreadFork finalZoomedThreadFork = new ZoomedThreadFork(
                    artifact
                    , primaryMetricIdentifier
                    , threadArtifactClustering
                    , selectableIndexProvider
                    , threadSelectables
                    , this
            );
            zoomedThreadFork = finalZoomedThreadFork;
            final KernelBasedDensityEstimationPanel finalKernelBasedDensityEstimationPanel = new KernelBasedDensityEstimationPanel(
                    selectableIndexProvider, threadSelectables, primaryMetricIdentifier, threadArtifactClustering);
            kernelBasedDensityEstimationPanel = finalKernelBasedDensityEstimationPanel;

            final JPanel tabWrapper = new JPanel();
            tabWrapper.setLayout(new GridBagLayout());

            final JBPanel<BorderLayoutPanel> zoomedThreadForkWrapper = new JBPanel<>(new BorderLayout());
            zoomedThreadForkWrapper.add(zoomedThreadFork, BorderLayout.CENTER);

            final JPanel centerPanel = new JPanel(new BorderLayout());

            if (actualNumberOfClustersOfTheDensityEstimation > 3)
            {
                final ComboBox<ComboBoxItem> numberOfClustersComboBox = new ComboBox<>();
                for (int i = 1; i < actualNumberOfClustersOfTheDensityEstimation; i++)
                {
                    numberOfClustersComboBox.addItem(new ComboBoxItem(i, String.valueOf(i)));
                }
                numberOfClustersComboBox.addItem(new ComboBoxItem(0, "Kernel Based Density Estimation"));
                numberOfClustersComboBox.setSelectedIndex(2); // At the index 2 there is the value 3
                final JBPanel<BorderLayoutPanel> numberOfClustersPanel = new JBPanel<>();
                numberOfClustersPanel.setLayout(new BoxLayout(numberOfClustersPanel, BoxLayout.X_AXIS));

                final JBLabel jbLabel = new JBLabel("Select the number of clusters k = ");
                numberOfClustersPanel.add(jbLabel);
                numberOfClustersPanel.add(numberOfClustersComboBox);

                //noinspection Convert2Lambda
                numberOfClustersComboBox.addItemListener(new ItemListener()
                {
                    @Override
                    public void itemStateChanged(final ItemEvent e)
                    {
                        final int stateChange = e.getStateChange();
                        if (stateChange == ItemEvent.SELECTED)
                        {
                            final ComboBoxItem item = (ComboBoxItem) e.getItem();
                            final int k = item.k;
                            AThreadArtifactClusteringStrategy strategy;
                            ThreadArtifactClustering clustering;
                            if (k > 0)
                            {
                                strategy = ApacheKMeans.getInstance(primaryMetricIdentifier, k);
                            } else
                            {
                                strategy = SmileKernelDensityClustering.getInstance(primaryMetricIdentifier);
                            }
                            clustering = artifact.getThreadArtifactClustering(strategy);
                            finalZoomedThreadFork.setThreadArtifactClustering(clustering);
                            finalKernelBasedDensityEstimationPanel.setThreadArtifactClustering(clustering);
                        }
                    }
                });

                centerPanel.add(numberOfClustersPanel, BorderLayout.NORTH);
            }
            final JPanel leftPanel = new JPanel();
            leftPanel.setLayout(new BoxLayout(leftPanel, BoxLayout.Y_AXIS));

            final JPanel leftSelectedThreadsPanel = new JPanel(new BorderLayout());
            leftSelectedThreadsPanel.setBorder(BorderFactory.createTitledBorder(BorderFactory.createEtchedBorder(),
                    "Selected threads"));

            // ---------------------------------------------

            final String metricString = LocalizationUtil.getLocalizedString("codesparks.ui.popup.thread.metric");
            final JBLabel leftSelectedMetricLabel = new JBLabel()
            {
                @Override
                public void paintComponent(final Graphics g)
                {
                    super.paintComponent(g);
                    final int index = selectableIndexProvider.getThreadSelectableIndex();
                    if (index >= 0)
                    {
                        final IThreadSelectable selectable = threadSelectables.get(index);
                        final ThreadArtifactDisplayData selectedThreadData =
                                threadArtifactsDisplayDataProvider.getDisplayDataOfSelectedThreads(artifact, selectable.getSelectedThreadArtifacts());
                        final double metricValueSum = selectedThreadData.getMetricValueSum();
                        final String percentage = CoreUtil.formatPercentage(metricValueSum);
                        setText(metricString + ": " + percentage);
                    }
                }
            };
            leftSelectedThreadsPanel.add(leftSelectedMetricLabel);
            componentsToRegisterToTheThreadSelectables.add(leftSelectedMetricLabel);

            final JPanel leftHoveredClusterPanelWrapper = new JPanel(new BorderLayout());
            final JPanel leftHoveredClusterPanel = new JPanel();
            leftHoveredClusterPanel.setLayout(new BoxLayout(leftHoveredClusterPanel, BoxLayout.Y_AXIS));
            leftHoveredClusterPanel.setBorder(BorderFactory.createTitledBorder(BorderFactory.createEtchedBorder(),
                    "Hovered cluster"));
            final Dimension leftHoverClusterPanelDimension = new Dimension(170, 50);
            //leftHoveredClusterPanel.setMinimumSize(leftHoverClusterPanelDimension);
            leftHoveredClusterPanel.setPreferredSize(leftHoverClusterPanelDimension);
            


            final JLabel leftMetricSumLabel = new JLabel();
//            leftMetricSumLabel.setPreferredSize(preferredSizeDimension);
            final JPanel leftMetricSumLabelWrapper = new JPanel(new BorderLayout());
            leftMetricSumLabelWrapper.add(leftMetricSumLabel);
            leftHoveredClusterPanel.add(leftMetricSumLabelWrapper);

            final JLabel leftMetricAvgLabel = new JLabel();
//            leftMetricAvgLabel.setPreferredSize(preferredSizeDimension);
            final JPanel leftMetricAvgLabelWrapper = new JPanel(new BorderLayout());
            leftMetricAvgLabelWrapper.add(leftMetricAvgLabel);
            leftHoveredClusterPanel.add(leftMetricAvgLabelWrapper);

            leftHoverLabels[0] = leftMetricSumLabel;
            leftHoverLabels[0].setText("Hover over a cluster\nfor more information!");
            leftHoverLabels[1] = leftMetricAvgLabel;

            // TODO
            //leftHoveredClusterPanel.add(new Label("left hovered Test"));

            leftPanel.add(leftSelectedThreadsPanel);
            leftHoveredClusterPanelWrapper.add(leftHoveredClusterPanel, BorderLayout.CENTER);
            leftPanel.add(leftHoveredClusterPanelWrapper);

            final JPanel rightPanel = new JPanel();
            rightPanel.setLayout(new BoxLayout(rightPanel, BoxLayout.Y_AXIS));

            final JPanel rightSelectedThreadsPanelWrapper = new JPanel(new BorderLayout());
            final JPanel rightSelectedThreadsPanel = new JPanel();
            rightSelectedThreadsPanel.setLayout(new BoxLayout(rightSelectedThreadsPanel, BoxLayout.Y_AXIS));
            rightSelectedThreadsPanel.setBorder(BorderFactory.createTitledBorder(BorderFactory.createEtchedBorder(),
                    "Selected threads"));

            final String numberOfThreadTypesString = LocalizationUtil.getLocalizedString("codesparks.ui.popup.thread.numberoftypes");
            final JLabel rightSelectedTypesLabel = new JLabel()
            {
                @Override
                public void paintComponent(final Graphics g)
                {
                    super.paintComponent(g);
                    final int index = selectableIndexProvider.getThreadSelectableIndex();
                    if (index >= 0)
                    {
                        final IThreadSelectable selectable = threadSelectables.get(index);
                        final ThreadArtifactDisplayData selectedThreadData =
                                threadArtifactsDisplayDataProvider.getDisplayDataOfSelectedThreads(artifact, selectable.getSelectedThreadArtifacts());
                        setText(numberOfThreadTypesString + ": " + selectedThreadData.getNumberOfThreadTypes());
                    }
                }
            };
            final String numberOfThreadsString = LocalizationUtil.getLocalizedString("codesparks.ui.popup.thread.numberofthreads");
            final JLabel rightSelectedNumberOfThreadsLabel = new JLabel()
            {
                @Override
                public void paintComponent(final Graphics g)
                {
                    super.paintComponent(g);
                    final int index = selectableIndexProvider.getThreadSelectableIndex();
                    if (index >= 0)
                    {
                        final IThreadSelectable selectable = threadSelectables.get(index);
                        final ThreadArtifactDisplayData selectedThreadData =
                                threadArtifactsDisplayDataProvider.getDisplayDataOfSelectedThreads(artifact, selectable.getSelectedThreadArtifacts());
                        setText(numberOfThreadsString + ": " + selectedThreadData.getNumberOfThreads());
                    }
                }
            };

            final JPanel typeLabelWrapper = new JPanel(new BorderLayout());
            typeLabelWrapper.add(rightSelectedTypesLabel);
            rightSelectedThreadsPanel.add(typeLabelWrapper);
            componentsToRegisterToTheThreadSelectables.add(rightSelectedTypesLabel);

            final JPanel nrLabelWrapper = new JPanel(new BorderLayout());
            nrLabelWrapper.add(rightSelectedNumberOfThreadsLabel);
            rightSelectedThreadsPanel.add(nrLabelWrapper);
            componentsToRegisterToTheThreadSelectables.add(rightSelectedNumberOfThreadsLabel);

            final JBPanel<BorderLayoutPanel> rightHoveredThreadsPanel = new BorderLayoutPanel();
            rightHoveredThreadsPanel.setBorder(BorderFactory.createTitledBorder(BorderFactory.createEtchedBorder(),
                    "Hovered cluster"));

            rightHoveredThreadsPanel.add(new Label("right hovered Test"));
            //TODO


//            rightPanel.add(rightSelectedThreadsPanel);
            rightSelectedThreadsPanelWrapper.add(rightSelectedThreadsPanel, BorderLayout.CENTER);
            rightPanel.add(rightSelectedThreadsPanelWrapper);

            rightPanel.add(rightHoveredThreadsPanel);

            centerPanel.add(leftPanel, BorderLayout.WEST);
            centerPanel.add(rightPanel, BorderLayout.EAST);
            centerPanel.add(zoomedThreadForkWrapper, BorderLayout.CENTER);

            tabWrapper.add(centerPanel);
            zoomedVizTabbedPane.addTab("ThreadFork", tabWrapper);
        }

        /*
         * The thread metric density estimation / histogram
         */
        if (kernelBasedDensityEstimationPanel == null)
        {
            kernelBasedDensityEstimationPanel = new KernelBasedDensityEstimationPanel(
                    selectableIndexProvider, threadSelectables, primaryMetricIdentifier, threadArtifactClustering);
        }

        zoomedVizTabbedPane.addTab("Histogram", kernelBasedDensityEstimationPanel);
        popupPanel.add(zoomedVizTabbedPane);

        /*
         * The selectables tabbed pane
         */
        final Map<String, List<AThreadArtifact>> map = new HashMap<>();
        int clusterId = 1;
        for (final ThreadArtifactCluster threadArtifacts : threadArtifactClustering)
        {
            map.put("Cluster:" + clusterId++, threadArtifacts);
        }
        final AThreadSelectable threadClustersTree = new ThreadClusterTree(map, primaryMetricIdentifier);
        threadSelectables.add(threadClustersTree);

        selectablesTabbedPane.addTab("Clusters", new JBScrollPane(threadClustersTree.getComponent()));

        final Map<String, List<AThreadArtifact>> threadTypeLists = artifact.getThreadTypeListsOfThreadsWithNumericMetricValue(primaryMetricIdentifier);
        final AThreadSelectable threadTypesTree = new ThreadTypeTree(threadTypeLists, primaryMetricIdentifier, threadArtifactClustering);
        threadSelectables.add(threadTypesTree);
        // Register the observers -> they observe each other, i.e. a selection in one will be adopted to all other in the list
        threadClustersTree.setNext(threadTypesTree);
        threadTypesTree.setNext(threadClustersTree);

        selectablesTabbedPane.addTab("Types", new JBScrollPane(threadTypesTree.getComponent()));

        final JBPanel<BorderLayoutPanel> selectablesTabbedPaneWrapper = new BorderLayoutPanel();
        selectablesTabbedPaneWrapper.add(selectablesTabbedPane, BorderLayout.CENTER);
        selectablesTabbedPaneWrapper.setMinimumSize(new Dimension(400, 150));
        selectablesTabbedPaneWrapper.setPreferredSize(new Dimension(400, 150));
        popupPanel.add(selectablesTabbedPaneWrapper);

        /*
         * Beginning with the buttons
         */

        final JBPanel<BorderLayoutPanel> buttonsPanel = new JBPanel<>();
        final Dimension buttonsPanelDimension = new Dimension(400, 70);
        buttonsPanel.setMinimumSize(buttonsPanelDimension);
        buttonsPanel.setPreferredSize(buttonsPanelDimension);
        buttonsPanel.setLayout(new BoxLayout(buttonsPanel, BoxLayout.Y_AXIS));

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
            final int index = selectableIndexProvider.getThreadSelectableIndex();
            if (index < threadSelectables.size())
            {
                final IThreadSelectable iThreadSelectable = threadSelectables.get(index);
                final IThreadArtifactFilter threadArtifactFilter = new DefaultThreadArtifactFilter(iThreadSelectable);
                CodeSparksFlowManager.getInstance().getCurrentCodeSparksFlow().applyThreadArtifactFilter(threadArtifactFilter);
            }
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

        final JBPanel<BorderLayoutPanel> buttonsPanelWrapper = new JBPanel<>(new BorderLayout());
        buttonsPanelWrapper.add(buttonsPanel, BorderLayout.CENTER);
        popupPanel.add(buttonsPanelWrapper);

        for (final IThreadSelectable threadSelectable : threadSelectables)
        {
            threadSelectable.registerComponentToRepaintOnSelection(kernelBasedDensityEstimationPanel);
            if (zoomedThreadFork != null)
            {
                threadSelectable.registerComponentToRepaintOnSelection(zoomedThreadFork);
            }
            for (final JComponent component : componentsToRegisterToTheThreadSelectables)
            {
                threadSelectable.registerComponentToRepaintOnSelection(component);
            }
        }

        //popupPanel.add(applyThreadFilter, BorderLayout.SOUTH);
        return popupPanel;
    }

    @Override
    protected String createPopupTitle(final AArtifact artifact)
    {
        final Map<String, List<AThreadArtifact>> threadTypeLists = artifact.getThreadTypeListsOfThreadsWithNumericMetricValue(primaryMetricIdentifier);
        return "Total number of threads: " + artifact.getNumberOfThreadsWithNumericMetricValue(primaryMetricIdentifier) +
                " | Different thread types: " + (threadTypeLists == null ? 0 : threadTypeLists.size());
    }

    @Override
    public void onHover(final ThreadArtifactCluster cluster)
    {
        final int index = selectableIndexProvider.getThreadSelectableIndex();
        if (index < 0)
        {
            return;
        }

        ThreadArtifactDisplayData hoveredThreadData =
                threadArtifactsDisplayDataProvider.getDisplayDataOfHoveredThreads(artifact,
                        threadSelectables.get(index).getSelectedThreadArtifactsOfCluster(cluster));

        if (hoveredThreadData == null)
        {
            hoveredThreadData = new ThreadArtifactDisplayData();
        }
        final String metricString = LocalizationUtil.getLocalizedString("codesparks.ui.popup.thread.metric");
        leftHoverLabels[0].setText(metricString + " (sum): " + CoreUtil.formatPercentage(hoveredThreadData.getMetricValueSum()));
        leftHoverLabels[0].setForeground(VisConstants.ORANGE);
        leftHoverLabels[1].setText(metricString + " (avg): " + CoreUtil.formatPercentage(hoveredThreadData.getMetricValueAvg()));
        leftHoverLabels[1].setForeground(VisConstants.ORANGE);
    }

    @Override
    public void onExit()
    {
        for (final JLabel hoverLabel : leftHoverLabels)
        {
            hoverLabel.setText("");
        }
        leftHoverLabels[0].setText("Hover over a cluster\nfor more information!");

    }

}
