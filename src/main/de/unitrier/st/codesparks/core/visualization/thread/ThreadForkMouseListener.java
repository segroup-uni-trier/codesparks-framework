/*
 * Copyright (c) 2021. Oliver Moseler
 */
package de.unitrier.st.codesparks.core.visualization.thread;

import com.intellij.openapi.ui.ComboBox;
import com.intellij.ui.JBColor;
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
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import java.awt.*;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.util.List;
import java.util.*;

public class ThreadForkMouseListener extends AArtifactVisualizationMouseListener implements IClusterHoverable, IClusterMouseClickable
{
    private IThreadSelectableIndexProvider selectableIndexProvider;
    private final List<IThreadSelectable> threadSelectables;
    private final IThreadArtifactsDisplayDataProvider threadArtifactsDisplayDataProvider;
    private final JLabel[] leftHoverLabels;
    private final List<JPanel> hoverPanels;
    private final List<JPanel> selectionPanels;
    private final JLabel[] rightHoverLabels;

    ThreadForkMouseListener(
            final JComponent component
            , final AArtifact artifact
            , final AMetricIdentifier primaryMetricIdentifier
            , final IThreadArtifactsDisplayDataProvider threadArtifactsDisplayDataProvider
    )
    {
        super(component, new Dimension(500, 170), artifact, primaryMetricIdentifier);
        this.threadSelectables = new ArrayList<>(2);
        this.threadArtifactsDisplayDataProvider = threadArtifactsDisplayDataProvider;
        this.leftHoverLabels = new JLabel[2];
        this.rightHoverLabels = new JLabel[2];
        hoverPanels = new ArrayList<>(2);
        selectionPanels = new ArrayList<>(2);
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

        // Objects that need to be declared already because they will be used in inner classes.
        final JBTabbedPane selectablesTabbedPane = new JBTabbedPane();
        selectableIndexProvider = selectablesTabbedPane::getSelectedIndex;

        /*
         * The zoomed viz tabbed pane
         */
        final JBTabbedPane zoomedVizTabbedPane = new JBTabbedPane();
        KernelBasedDensityEstimationPanel kernelBasedDensityEstimationPanel;
        ZoomedThreadFork zoomedThreadFork;

        final AThreadArtifactClusteringStrategy kbdeClusteringStrategy = KernelBasedDensityEstimationClustering.getInstance(primaryMetricIdentifier);

        final ThreadArtifactClustering kbdeClustering = artifact.clusterThreadArtifacts(kbdeClusteringStrategy);
        final int numberOfEstimatedClusters = kbdeClustering.size();

        ThreadArtifactClustering selectedClustering = artifact.getSelectedClusteringOrApplyAndSelect(kbdeClusteringStrategy);

        final int numberOfNonEmptyThreadClusters = selectedClustering.sizeAccordingToCurrentThreadSelection();
        if (numberOfNonEmptyThreadClusters > 3)
        { // Because the in-situ viz has changed to a k=3 clustering in that case, this will be the clustering we show first.
            selectedClustering = artifact.getClusteringAndSelect(ApacheKMeansPlusPlus.getInstance(primaryMetricIdentifier, 3));
        }
        final ZoomedThreadFork finalZoomedThreadFork = new ZoomedThreadFork(
                artifact
                , primaryMetricIdentifier
                , selectedClustering
                , selectableIndexProvider
                , threadSelectables
                , this
                , this
        ); // the final variable is for us in lambdas
        zoomedThreadFork = finalZoomedThreadFork;

        final KernelBasedDensityEstimationPanel finalKernelBasedDensityEstimationPanel = new KernelBasedDensityEstimationPanel(
                selectableIndexProvider
                , threadSelectables
                , primaryMetricIdentifier
                , selectedClustering
        ); // the final variable is for use in lambdas
        kernelBasedDensityEstimationPanel = finalKernelBasedDensityEstimationPanel;

        final JPanel forkCenterPanel = new JPanel(new BorderLayout());

        /*
         * ComboBox
         */

        int selectedK = 0; // k = 0 determines the density estimation
        final AThreadArtifactClusteringStrategy selectedClusteringStrategy = selectedClustering.getStrategy();
        if (selectedClusteringStrategy instanceof KThreadArtifactClusteringStrategy)
        {
            selectedK = ((KThreadArtifactClusteringStrategy) selectedClusteringStrategy).getK();
        }
        final ComboBox<ComboBoxItem> numberOfClustersComboBox = new ComboBox<>();
        if (numberOfEstimatedClusters > 6)
        {
            for (int i = 1; i <= 6; i++)
            {
                final ComboBoxItem comboBoxItem = new ComboBoxItem(i, String.valueOf(i));
                numberOfClustersComboBox.addItem(comboBoxItem);
            }
            numberOfClustersComboBox.addItem(new ComboBoxItem(0, numberOfEstimatedClusters + " (switch to histogram)"));
        } else
        {
            for (int i = 1; i < numberOfEstimatedClusters; i++)
            {
                final ComboBoxItem comboBoxItem = new ComboBoxItem(i, String.valueOf(i));
                numberOfClustersComboBox.addItem(comboBoxItem);
            }
            numberOfClustersComboBox.addItem(new ComboBoxItem(0, String.valueOf(numberOfEstimatedClusters)));
        }
        if (selectedK > 0)
        {
            numberOfClustersComboBox.setSelectedIndex(selectedK - 1);
        } else
        {
            numberOfClustersComboBox.setSelectedIndex(numberOfEstimatedClusters - 1);
        }

        numberOfClustersComboBox.addItemListener(new ItemListener()
        {
            private ComboBoxItem previousItem;
            private int ignoreStateChanges = 0;

            @Override
            public void itemStateChanged(final ItemEvent e)
            {
                final int stateChange = e.getStateChange();
                if (stateChange == ItemEvent.SELECTED)
                {
                    if (ignoreStateChanges > 0)
                    {
                        ignoreStateChanges -= 1;
                        return;
                    }
                    AThreadArtifactClusteringStrategy strategy;
                    ThreadArtifactClustering clustering;
                    final ComboBoxItem item = (ComboBoxItem) e.getItem();
                    final int k = item.k;
                    if (k > 0)
                    {
                        strategy = ApacheKMeansPlusPlus.getInstance(primaryMetricIdentifier, k);
                    } else
                    {
                        strategy = kbdeClusteringStrategy;
                    }
                    clustering = artifact.getClusteringAndSelect(strategy);

                    final VisualThreadClusterPropertiesManager propertiesManager = VisualThreadClusterPropertiesManager.getInstance(clustering);
                    propertiesManager.buildDefaultProperties();

                    finalKernelBasedDensityEstimationPanel.setThreadArtifactClustering(clustering);

                    if (numberOfEstimatedClusters > 6 && k == 0)
                    {
                        zoomedVizTabbedPane.setSelectedIndex(1); // Switch to histogram tab
                        ignoreStateChanges = 2; // deselect and select
                        numberOfClustersComboBox.setSelectedItem(previousItem);
                    } else
                    {
                        finalZoomedThreadFork.setThreadArtifactClustering(clustering);
                        for (final IThreadSelectable threadSelectable : threadSelectables)
                        {
                            threadSelectable.setThreadArtifactClustering(clustering, true);
                        }
                    }
                }
                if (stateChange == ItemEvent.DESELECTED)
                {
                    if (ignoreStateChanges > 0)
                    {
                        ignoreStateChanges -= 1;
                        return;
                    }
                    previousItem = (ComboBoxItem) e.getItem();
                }
            }
        });
        final JLabel numberOfClustersLabel = new JLabel("Recompute with a maximum of clusters k = ");
        final JPanel numberOfClustersPanel = new JPanel();
        numberOfClustersPanel.setLayout(new BoxLayout(numberOfClustersPanel, BoxLayout.X_AXIS));
        numberOfClustersPanel.add(numberOfClustersLabel);
        numberOfClustersPanel.add(numberOfClustersComboBox);

        forkCenterPanel.add(numberOfClustersPanel, BorderLayout.NORTH);

        // ---------------------------------------------

        final Dimension sidePanelDimension = new Dimension(145, 50);

        final JPanel leftPanel = new JPanel();
        leftPanel.setLayout(new BoxLayout(leftPanel, BoxLayout.Y_AXIS));
        leftPanel.setMinimumSize(sidePanelDimension);
        leftPanel.setPreferredSize(sidePanelDimension);
        final JPanel leftSelectedThreadsPanel = new JPanel();
        leftSelectedThreadsPanel.setLayout(new BoxLayout(leftSelectedThreadsPanel, BoxLayout.Y_AXIS));
        leftSelectedThreadsPanel.setBorder(BorderFactory.createTitledBorder(BorderFactory.createEtchedBorder(),
                "Selected threads"));

        // ---------------------------------------------

        final String metricString = LocalizationUtil.getLocalizedString("codesparks.ui.popup.thread.metric");
        final JPanel leftSelectedMetricLabelWrapper = new JPanel(new BorderLayout());
        final JLabel leftSelectedMetricLabel = new JLabel()
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
                    final String percentage = CoreUtil.formatPercentage(metricValueSum, true);
                    setText(metricString + ": " + percentage);
                }
            }
        };
        leftSelectedMetricLabelWrapper.add(leftSelectedMetricLabel, BorderLayout.CENTER);
        leftSelectedThreadsPanel.add(leftSelectedMetricLabelWrapper);
        selectionPanels.add(leftSelectedThreadsPanel);
        componentsToRegisterToTheThreadSelectables.add(leftSelectedMetricLabel);

        final JPanel leftHoveredClusterPanel = new JPanel();
        leftHoveredClusterPanel.setLayout(new BoxLayout(leftHoveredClusterPanel, BoxLayout.Y_AXIS));
        leftHoveredClusterPanel.setBorder(titledHoverBorder);

        final JLabel leftMetricSumLabel = new JLabel();
        final JPanel leftMetricSumLabelWrapper = new JPanel(new BorderLayout());
        leftMetricSumLabelWrapper.add(leftMetricSumLabel);
        leftHoveredClusterPanel.add(leftMetricSumLabelWrapper);

        final JLabel leftMetricAvgLabel = new JLabel();
        final JPanel leftMetricAvgLabelWrapper = new JPanel(new BorderLayout());
        leftMetricAvgLabelWrapper.add(leftMetricAvgLabel);
        leftHoveredClusterPanel.add(leftMetricAvgLabelWrapper);

        leftHoverLabels[0] = leftMetricSumLabel;
        leftHoverLabels[0].setText("Metric (sum): n/a");
        leftHoverLabels[1] = leftMetricAvgLabel;
        leftHoverLabels[1].setText("Metric (avg): n/a");

        leftPanel.add(leftSelectedThreadsPanel);
        leftPanel.add(leftHoveredClusterPanel);

        hoverPanels.add(leftHoveredClusterPanel);

        // -------------------------------------------------

        final JPanel rightPanel = new JPanel();
        rightPanel.setLayout(new BoxLayout(rightPanel, BoxLayout.Y_AXIS));
        rightPanel.setMinimumSize(sidePanelDimension);
        rightPanel.setPreferredSize(sidePanelDimension);

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

        final JPanel rightSelectedNumberOfThreadsLabelWrapper = new JPanel(new BorderLayout());
        rightSelectedNumberOfThreadsLabelWrapper.add(rightSelectedNumberOfThreadsLabel);
        rightSelectedThreadsPanel.add(rightSelectedNumberOfThreadsLabelWrapper);
        selectionPanels.add(rightSelectedThreadsPanel);
        componentsToRegisterToTheThreadSelectables.add(rightSelectedNumberOfThreadsLabel);

        final JPanel rightSelectedTypesLabelWrapper = new JPanel(new BorderLayout());
        rightSelectedTypesLabelWrapper.add(rightSelectedTypesLabel);
        rightSelectedThreadsPanel.add(rightSelectedTypesLabelWrapper);
        componentsToRegisterToTheThreadSelectables.add(rightSelectedTypesLabel);

        final JPanel rightHoveredClusterPanel = new JPanel();
        rightHoveredClusterPanel.setLayout(new BoxLayout(rightHoveredClusterPanel, BoxLayout.Y_AXIS));
        rightHoveredClusterPanel.setBorder(titledHoverBorder);

        final JLabel rightHoverThreadsLabel = new JLabel();
        final JPanel rightHoverThreadsLabelWrapper = new JPanel(new BorderLayout());
        rightHoverThreadsLabelWrapper.add(rightHoverThreadsLabel);
        rightHoveredClusterPanel.add(rightHoverThreadsLabelWrapper);

        final JLabel rightHoverTypesLabel = new JLabel();
        final JPanel rightHoverTypesLabelWrapper = new JPanel(new BorderLayout());
        rightHoverTypesLabelWrapper.add(rightHoverTypesLabel);
        rightHoveredClusterPanel.add(rightHoverTypesLabelWrapper);

        rightHoverLabels[0] = rightHoverThreadsLabel;
        rightHoverLabels[0].setText("#Threads : n/a");
        rightHoverLabels[1] = rightHoverTypesLabel;
        rightHoverLabels[1].setText("#Types: n/a");

        rightPanel.add(rightSelectedThreadsPanel);
        rightPanel.add(rightHoveredClusterPanel);

        hoverPanels.add(rightHoveredClusterPanel);

        // From left to right
        forkCenterPanel.add(leftPanel, BorderLayout.WEST);
        forkCenterPanel.add(zoomedThreadFork, BorderLayout.CENTER);
        forkCenterPanel.add(rightPanel, BorderLayout.EAST);

        zoomedVizTabbedPane.addTab("ThreadFork", forkCenterPanel);

        /*
         * The thread metric density estimation / histogram
         */

        final JPanel kbdeCenterPanel = new JPanel(new BorderLayout());
        final JCheckBox showDensityEstimation = new JCheckBox("Show the kernel based density estimation.");
        showDensityEstimation.setSelected(false);
        showDensityEstimation.addActionListener((event)
                -> kernelBasedDensityEstimationPanel.setShowKernelBasedDensityEstimation(showDensityEstimation.isSelected()));
        kbdeCenterPanel.add(showDensityEstimation, BorderLayout.NORTH);
        kbdeCenterPanel.add(kernelBasedDensityEstimationPanel, BorderLayout.CENTER);

        zoomedVizTabbedPane.addTab("Histogram", kbdeCenterPanel);
        popupPanel.add(zoomedVizTabbedPane);

        // The following change listener is necessary for the case that k > 6 is selected in the combo box, i.e. the histogram tab will be selected
        // automatically but the selection of the combo box is reset (to the previous value lower than or equal to 6). Therefore, the histogram shows
        // something different from the threadfork. Now the idea is to sync the kbde histogram again with the actual selection,
        // when the user goes back to the ThreadFork tab. It is placed here to prevent the trigger of change events
        // when new tabs are added to the tabbed pane.

        //noinspection Convert2Lambda
        zoomedVizTabbedPane.addChangeListener(new ChangeListener()
        {
            @Override
            public void stateChanged(final ChangeEvent e)
            {
                final JBTabbedPane source = (JBTabbedPane) e.getSource();
                final int selectedIndex = source.getSelectedIndex();
                if (selectedIndex == 0)
                {
                    final ThreadArtifactClustering threadArtifactClustering = finalZoomedThreadFork.getThreadArtifactClustering();
                    finalKernelBasedDensityEstimationPanel.setThreadArtifactClustering(threadArtifactClustering);
                }
            }
        });

        /*
         * The selectables tabbed pane
         */
        final AThreadSelectable threadClustersTree = new ThreadClusterTree(selectedClustering, primaryMetricIdentifier);
        threadSelectables.add(threadClustersTree);
        selectablesTabbedPane.addTab("Clusters", new JBScrollPane(threadClustersTree.getComponent()));

        final AThreadSelectable threadTypesTree = new ThreadTypeTree(artifact, selectedClustering, primaryMetricIdentifier);
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
            threadSelectable.registerComponentToRepaintOnSelection(zoomedThreadFork);
            for (final JComponent component : componentsToRegisterToTheThreadSelectables)
            {
                threadSelectable.registerComponentToRepaintOnSelection(component);
            }
        }
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
        final Set<AThreadArtifact> selectedThreadArtifactsOfCluster = threadSelectables.get(index).getSelectedThreadArtifactsOfCluster(cluster);
        ThreadArtifactDisplayData hoveredThreadData =
                threadArtifactsDisplayDataProvider.getDisplayDataOfHoveredThreads(artifact, selectedThreadArtifactsOfCluster);
        if (hoveredThreadData == null)
        {
            hoveredThreadData = new ThreadArtifactDisplayData();
        }
        final String metricString = LocalizationUtil.getLocalizedString("codesparks.ui.popup.thread.metric");
        if (leftHoverLabels[0] != null)
        {
            leftHoverLabels[0].setText(metricString + " (sum): " + CoreUtil.formatPercentage(hoveredThreadData.getMetricValueSum(), true));
        }
        if (leftHoverLabels[1] != null)
        {
            leftHoverLabels[1].setText(metricString + " (avg): " + CoreUtil.formatPercentage(hoveredThreadData.getMetricValueAvg(), true));
        }
        if (rightHoverLabels[0] != null)
        {
            rightHoverLabels[0].setText("#Threads: " + hoveredThreadData.getNumberOfThreads());
        }
        if (rightHoverLabels[1] != null)
        {
            rightHoverLabels[1].setText("#Types: " + hoveredThreadData.getNumberOfThreadTypes());
        }
        for (final JPanel hoverPanel : hoverPanels)
        {
            hoverPanel.setBorder(coloredTitledHoverBorder);
        }
    }

    private static final String hoverBorderTitle = "Hovered cluster";
    private static final Border coloredEtchedHoverBorder = BorderFactory.createEtchedBorder(VisConstants.ORANGE, VisConstants.ORANGE);
    private static final Border coloredTitledHoverBorder = BorderFactory.createTitledBorder(coloredEtchedHoverBorder, hoverBorderTitle);
    private static final Border titledHoverBorder = BorderFactory.createTitledBorder(BorderFactory.createEtchedBorder(), hoverBorderTitle);

    @Override
    public void onExit()
    {
        final String metricString = LocalizationUtil.getLocalizedString("codesparks.ui.popup.thread.metric");
        if (leftHoverLabels[0] != null)
        {
            leftHoverLabels[0].setText(metricString + " (sum): n/a");
        }
        if (leftHoverLabels[1] != null)
        {
            leftHoverLabels[1].setText(metricString + " (avg): n/a");
        }
        if (rightHoverLabels[0] != null)
        {
            rightHoverLabels[0].setText("#Threads: n/a");
        }
        if (rightHoverLabels[1] != null)
        {
            rightHoverLabels[1].setText("#Types: n/a");
        }
        for (final JPanel hoverPanel : hoverPanels)
        {
            hoverPanel.setBorder(titledHoverBorder);
        }
    }

    private static final Border coloredEtchedMouseClickedBorder = BorderFactory.createEtchedBorder(JBColor.CYAN, JBColor.CYAN);
    private static final Border coloredTitledMouseClickedBorder = BorderFactory.createTitledBorder(coloredEtchedMouseClickedBorder, "Selected threads");

    private static final Object mouseClickedLock = new Object();

    @Override
    public void onMouseClicked()
    {
        final Thread thread = new Thread(() -> {
            final List<Border> formerBorders = new ArrayList<>(selectionPanels.size());
            synchronized (mouseClickedLock)
            {
                for (final JPanel selectionPanel : selectionPanels)
                {
                    formerBorders.add(selectionPanel.getBorder());
                    selectionPanel.setBorder(coloredTitledMouseClickedBorder);
                }
                try
                {
                    Thread.sleep(500);
                } catch (InterruptedException e)
                {
                    // ignored
                }
                int i = 0;
                for (final JPanel selectionPanel : selectionPanels)
                {
                    selectionPanel.setBorder(formerBorders.get(i++));
                }
            }
        });
        thread.setDaemon(true);
        thread.start();
    }
}
