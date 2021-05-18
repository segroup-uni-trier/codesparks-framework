/*
 * Copyright (c), Oliver Moseler, 2021
 */
package de.unitrier.st.codesparks.core.visualization.thread;

import com.intellij.ui.JBColor;
import com.intellij.ui.components.JBScrollPane;
import com.intellij.ui.components.JBTabbedPane;
import de.unitrier.st.codesparks.core.ACodeSparksFlow;
import de.unitrier.st.codesparks.core.CodeSparksFlowManager;
import de.unitrier.st.codesparks.core.CoreUtil;
import de.unitrier.st.codesparks.core.data.*;
import de.unitrier.st.codesparks.core.localization.LocalizationUtil;
import de.unitrier.st.codesparks.core.logging.UserActivityEnum;
import de.unitrier.st.codesparks.core.logging.UserActivityLogger;
import de.unitrier.st.codesparks.core.visualization.AArtifactVisualizationMouseListener;
import de.unitrier.st.codesparks.core.visualization.popup.*;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ThreadRadarMouseListener extends AArtifactVisualizationMouseListener implements IClusterHoverable
{
    private ZoomedThreadRadar zoomedThreadRadar;
    private final List<IThreadSelectable> threadSelectables;
    private final IThreadArtifactsDisplayData threadArtifactsDisplayData;
    private final JLabel[] hoverLabels;
    private final AMetricIdentifier secondaryMetricIdentifier;

    public ThreadRadarMouseListener(
            final JComponent component
            , final AArtifact artifact
            , final IThreadArtifactsDisplayData threadArtifactsDisplayData
            , final AMetricIdentifier primaryMetricIdentifier
            , final AMetricIdentifier secondaryMetricIdentifier
    )
    {
        super(component, new Dimension(500, 340), artifact, primaryMetricIdentifier);
        this.component = component;
        this.hoverLabels = new JLabel[4];
        this.threadSelectables = new ArrayList<>();
        this.threadArtifactsDisplayData = threadArtifactsDisplayData;
        this.secondaryMetricIdentifier = secondaryMetricIdentifier;
        component.addMouseMotionListener(this);
    }

    @Override
    protected PopupPanel createPopupContent(AArtifact artifact)
    {
        final PopupPanel popupPanel = new PopupPanel(new BorderLayout(), "ThreadRadarPopup");

        threadSelectables.clear();
        final JBTabbedPane tabbedPane = new JBTabbedPane();

        final ThreadArtifactClustering sortedDefaultThreadArtifactClustering =
                artifact.getSortedConstraintKMeansWithAMaximumOfThreeClustersThreadArtifactClustering(primaryMetricIdentifier);
        final Map<String, List<AThreadArtifact>> map = new HashMap<>();
        int clusterId = 1;
        for (final ThreadArtifactCluster threadArtifacts : sortedDefaultThreadArtifactClustering)
        {
            map.put("Cluster:" + clusterId++, threadArtifacts);
        }

        final AThreadSelectable threadClustersTree = new ThreadClusterTree(map, primaryMetricIdentifier);
        threadSelectables.add(threadClustersTree);
        tabbedPane.addTab("Clusters", new JBScrollPane(threadClustersTree.getComponent()));

        // -------------

        final Map<String, List<AThreadArtifact>> threadTypeLists = artifact.getThreadTypeListsOfThreadsWithNumericMetricValue(primaryMetricIdentifier);
        final AThreadSelectable threadTypesTree = new ThreadTypeTree(threadTypeLists, primaryMetricIdentifier, sortedDefaultThreadArtifactClustering);
        threadSelectables.add(threadTypesTree);
        tabbedPane.addTab("Types", new JBScrollPane(threadTypesTree.getComponent()));
        tabbedPane.setMinimumSize(new Dimension(400, 150));

        threadClustersTree.setNext(threadTypesTree);
        threadTypesTree.setNext(threadClustersTree);

        final IThreadSelectableIndexProvider indexProvider = tabbedPane::getSelectedIndex;

        // -------------------------

        popupPanel.add(tabbedPane, BorderLayout.CENTER);

        // User activity logging
        tabbedPane.addChangeListener(e -> {

            int threadSelectableIndex = indexProvider.getThreadSelectableIndex();
            if (threadSelectableIndex == 0)
            {
                UserActivityLogger.getInstance().log(UserActivityEnum.ThreadRadarPopupClustersTabEntered);
            } else
            {
                UserActivityLogger.getInstance().log(UserActivityEnum.ThreadRadarPopupTypesTabEntered);
            }
        });


        // ControlButtonsPanel!
        final JPanel controlButtonsWrapper = new JPanel(new BorderLayout());
        final JPanel controlButtonsBox = new JPanel();
        controlButtonsBox.setLayout(new BoxLayout(controlButtonsBox, BoxLayout.X_AXIS));
        final JButton selectAll = new JButton(LocalizationUtil.getLocalizedString("codesparks.ui.popup.selectallbutton"));
        selectAll.addActionListener(e -> {

            UserActivityLogger.getInstance().log(UserActivityEnum.ThreadRadarDetailsViewSelectAllButtonClicked);

            for (final IThreadSelectable threadSelectable : this.threadSelectables)
            {
                threadSelectable.selectAll();
            }
        });
        final JButton deselectAll = new JButton(LocalizationUtil.getLocalizedString("codesparks.ui.popup.deselectallbutton"));
        deselectAll.addActionListener(e -> {

            UserActivityLogger.getInstance().log(UserActivityEnum.ThreadRadarDetailsViewDeselectAllButtonClicked);

            for (final IThreadSelectable threadSelectable : this.threadSelectables)
            {
                threadSelectable.deselectAll();
            }
        });

        final JButton invert = new JButton(LocalizationUtil.getLocalizedString("codesparks.ui.popup.invertallbutton"));
        invert.addActionListener(e -> {

            UserActivityLogger.getInstance().log(UserActivityEnum.ThreadRadarDetailsViewInvertSelectionButtonClicked);

            for (final IThreadSelectable threadSelectable : this.threadSelectables)
            {
                threadSelectable.invertAll();
            }
        });

        final JButton reset = new JButton(LocalizationUtil.getLocalizedString("codesparks.ui.button.reset.thread.filter.global"));
        reset.addActionListener(e -> {

            UserActivityLogger.getInstance().log(UserActivityEnum.ThreadRadarDetailsViewResetThreadFilterButtonClicked);

            popupPanel.cancelPopup();
            threadSelectables.forEach(IThreadSelectable::selectAll);
            final ACodeSparksFlow currentCodeSparksFlow = CodeSparksFlowManager.getInstance().getCurrentCodeSparksFlow();
            currentCodeSparksFlow.applyThreadArtifactFilter(GlobalResetThreadArtifactFilter.getInstance());
        });

        final JButton apply = new JButton(LocalizationUtil.getLocalizedString("codesparks.ui.popup.button.apply.thread.filter"));
        apply.addActionListener(e -> {

            UserActivityLogger.getInstance().log(UserActivityEnum.ThreadRadarDetailsViewApplyThreadFilterButtonClicked);

            popupPanel.cancelPopup();
            final int index = indexProvider.getThreadSelectableIndex();
            final IThreadSelectable iThreadSelectable = threadSelectables.get(index);
            final IThreadArtifactFilter iThreadArtifactFilter = new DefaultThreadArtifactFilter(iThreadSelectable);
            CodeSparksFlowManager.getInstance().getCurrentCodeSparksFlow().applyThreadArtifactFilter(iThreadArtifactFilter);
        });

        controlButtonsBox.add(selectAll);
        controlButtonsBox.add(deselectAll);
        controlButtonsBox.add(invert);
        controlButtonsBox.add(reset);
        controlButtonsBox.add(apply);
        controlButtonsWrapper.add(controlButtonsBox, BorderLayout.CENTER);
        popupPanel.add(controlButtonsWrapper, BorderLayout.SOUTH);

        // buildPopup(popupPanel); // Inlined this method!

        final float labelFontSize = 14.0f;
        final JPanel northWrapper = new JPanel(new BorderLayout());
        final JPanel northBox = new JPanel();
        northBox.setLayout(new BoxLayout(northBox, BoxLayout.X_AXIS));
        final JPanel northLeftWrapper = new JPanel(new BorderLayout());
        final JPanel northLeftBox = new JPanel();
        northLeftBox.setLayout(new BoxLayout(northLeftBox, BoxLayout.Y_AXIS));
        northLeftBox.setPreferredSize(new Dimension(150, 160));
        final JPanel radialVisualizationWrapper = new JPanel(new BorderLayout());
        final JPanel radialVisualizationBox = new JPanel();
        radialVisualizationBox.setLayout(new BoxLayout(radialVisualizationBox, BoxLayout.Y_AXIS));
        final JPanel radialVisualizationButtonsWrapper = new JPanel(new BorderLayout());
        final JPanel radialVisualizationButtonsBox = new JPanel();
        radialVisualizationButtonsBox.setLayout(new BoxLayout(radialVisualizationButtonsBox, BoxLayout.X_AXIS));
        final JPanel northRightWrapper = new JPanel(new BorderLayout());
        final JPanel northRightBox = new JPanel();
        northRightBox.setLayout(new BoxLayout(northRightBox, BoxLayout.Y_AXIS));
        northRightBox.setPreferredSize(new Dimension(170, 200));
        final JPanel selectedDataDisplayWrapper = new JPanel(new BorderLayout());
        final JPanel selectedDataDisplayBox = new JPanel();
        selectedDataDisplayBox.setLayout(new BoxLayout(selectedDataDisplayBox, BoxLayout.Y_AXIS));
        selectedDataDisplayBox.setPreferredSize(new Dimension(170, 50));
        final JPanel hoveredDataDisplayWrapper = new JPanel(new BorderLayout());
        final JPanel hoveredDataDisplayBox = new JPanel();
        hoveredDataDisplayBox.setLayout(new BoxLayout(hoveredDataDisplayBox, BoxLayout.Y_AXIS));
        hoveredDataDisplayBox.setPreferredSize(new Dimension(170, 50));

        //Radial Visualization

        zoomedThreadRadar = new ZoomedThreadRadar(artifact, indexProvider, threadSelectables, primaryMetricIdentifier);
        zoomedThreadRadar.setBorder(BorderFactory.createEmptyBorder(0, 50, 0, 0));
        final ZoomedThreadRadarMouseAdapter mouseAdapter =
                new ZoomedThreadRadarMouseAdapter(zoomedThreadRadar, artifact, primaryMetricIdentifier, this, northLeftWrapper);
        zoomedThreadRadar.addMouseMotionListener(mouseAdapter);
        zoomedThreadRadar.addMouseListener(mouseAdapter);


        final int numberOfClusters = sortedDefaultThreadArtifactClustering.size();

        // Cluster selection buttons

        final JButton[] threadClusterSelectionButtons =
                {
                        new JButton("C1"), new JButton("C2"), new JButton("C3")
                };

        final VisualThreadClusterPropertiesManager manager = VisualThreadClusterPropertiesManager.getInstance();

        for (int i = 0; i < threadClusterSelectionButtons.length; i++)
        {
            final int indexToUse = i;
            threadClusterSelectionButtons[i].addActionListener(e ->
                    {
                        final ThreadArtifactCluster cluster = sortedDefaultThreadArtifactClustering.get(indexToUse);
                        threadSelectables.forEach(iThreadSelectable -> iThreadSelectable.toggleCluster(cluster));
                        UserActivityLogger.getInstance().log(UserActivityEnum.ThreadClusterToggleButtonClicked,
                                "buttonIndex=" + indexToUse, "clusterId=" + cluster.getId(),
                                String.valueOf(cluster));
                    }
            );

            if (i < numberOfClusters)
            {
                final ThreadArtifactCluster cluster = sortedDefaultThreadArtifactClustering.get(i);

                if (cluster.size() < 1)
                {
                    threadClusterSelectionButtons[i].setEnabled(false);
                    continue;
                }
                final VisualThreadClusterProperties properties = manager.getProperties(cluster);
                final JBColor color = properties.getColor();
                threadClusterSelectionButtons[i].setForeground(color);
            } else
            {
                threadClusterSelectionButtons[i].setEnabled(false);
            }
        }

        final JLabel radialButtonsToggleLabel =
                new JLabel(LocalizationUtil.getLocalizedString("codesparks.ui.overview.button.threads.togglecluster"));
        radialButtonsToggleLabel.setFont(radialButtonsToggleLabel.getFont().deriveFont(12.0f));
        radialButtonsToggleLabel.setAlignmentX(JLabel.CENTER_ALIGNMENT);
        radialVisualizationButtonsBox.add(radialButtonsToggleLabel);
        for (int i = 0; i < 3; i++)
        {
            threadClusterSelectionButtons[i].setPreferredSize(new Dimension((ThreadRadarConstants.FRAME_ZOOMED) / 3, 20));
            radialVisualizationButtonsBox.add(threadClusterSelectionButtons[i]);
        }

        // ---------------------------------------------
        ThreadArtifactDisplayData selectedData =
                threadArtifactsDisplayData.getDisplayDataOfSelectedThreads(artifact, threadClustersTree.getSelectedThreadArtifacts());
        if (selectedData == null)
        {
            selectedData = new ThreadArtifactDisplayData();
        }
        //final ThreadArtifactDisplayData finalSelectedData = selectedData;

        final String metricString = LocalizationUtil.getLocalizedString("codesparks.ui.popup.thread.metric");
        final JLabel selectedMetricLabel = new JLabel(metricString + " : " + CoreUtil.formatPercentage(selectedData.getMetricValueSum()))
        {
            @Override
            public void repaint()
            {
                int index = indexProvider.getThreadSelectableIndex();
                IThreadSelectable iThreadSelectable = threadSelectables.get(index);
                ThreadArtifactDisplayData selectedThreadData =
                        threadArtifactsDisplayData.getDisplayDataOfSelectedThreads(artifact, iThreadSelectable.getSelectedThreadArtifacts());
                setText(metricString + " : " + CoreUtil.formatPercentage(selectedThreadData.getMetricValueSum()));
                super.repaint();
            }
        };
        selectedMetricLabel.setFont(selectedMetricLabel.getFont().deriveFont(labelFontSize));
        selectedDataDisplayBox.add(selectedMetricLabel);

        // -------------------------------

        final String numberOfThreadTypesString = LocalizationUtil.getLocalizedString("codesparks.ui.popup.thread.numberoftypes");
        final JLabel selectedNumberOfThreadTypesLabel = new JLabel(numberOfThreadTypesString + " : " + selectedData.getNumberOfThreadTypes())
        {
            @Override
            public void repaint()
            {
                int index = indexProvider.getThreadSelectableIndex();
                IThreadSelectable iThreadSelectable = threadSelectables.get(index);
                ThreadArtifactDisplayData selectedThreadData =
                        threadArtifactsDisplayData.getDisplayDataOfSelectedThreads(artifact, iThreadSelectable.getSelectedThreadArtifacts());
                setText(numberOfThreadTypesString + " : " + selectedThreadData.getNumberOfThreadTypes());
                super.repaint();
            }
        };
        selectedNumberOfThreadTypesLabel.setFont(selectedNumberOfThreadTypesLabel.getFont().deriveFont(labelFontSize));
        selectedDataDisplayBox.add(selectedNumberOfThreadTypesLabel);

        // -------------------------------

        final String numberOfThreadsString = LocalizationUtil.getLocalizedString("codesparks.ui.popup.thread.numberofthreads");
        final JLabel selectedNumberOfThreadsLabel = new JLabel(numberOfThreadsString + " : " + selectedData.getNumberOfThreads())
        {
            @Override
            public void repaint()
            {
                int index = indexProvider.getThreadSelectableIndex();
                IThreadSelectable iThreadSelectable = threadSelectables.get(index);
                ThreadArtifactDisplayData selectedThreadData =
                        threadArtifactsDisplayData.getDisplayDataOfSelectedThreads(artifact, iThreadSelectable.getSelectedThreadArtifacts());
                setText(numberOfThreadsString + " : " + selectedThreadData.getNumberOfThreads());
                super.repaint();
            }
        };
        selectedNumberOfThreadsLabel.setFont(selectedNumberOfThreadsLabel.getFont().deriveFont(labelFontSize));
        selectedDataDisplayBox.add(selectedNumberOfThreadsLabel);

        /*
         * Register the selection
         */

        selectedDataDisplayWrapper.add(selectedDataDisplayBox, BorderLayout.CENTER);
        selectedDataDisplayWrapper.setBorder(BorderFactory.createTitledBorder(BorderFactory.createEtchedBorder(),
                "Selected threads"));

        for (int i = 0; i < 4; i++)
        {
            final JLabel label = new JLabel("");
            label.setFont(label.getFont().deriveFont(labelFontSize));
            hoverLabels[i] = label;
            hoveredDataDisplayBox.add(label);
        }

        hoverLabels[0].setText("Hover over a thread cluster for more information");
        hoveredDataDisplayWrapper.add(hoveredDataDisplayBox, BorderLayout.CENTER);
        hoveredDataDisplayWrapper.setBorder(BorderFactory.createTitledBorder(BorderFactory.createEtchedBorder(),
                "Hovered cluster"));

        radialVisualizationBox.add(zoomedThreadRadar);
        radialVisualizationWrapper.add(radialVisualizationBox, BorderLayout.CENTER);
        radialVisualizationButtonsWrapper.add(radialVisualizationButtonsBox, BorderLayout.CENTER);
        northLeftBox.add(radialVisualizationBox);
        northLeftBox.add(radialVisualizationButtonsBox);
        northLeftWrapper.add(northLeftBox, BorderLayout.CENTER);

        northRightBox.add(selectedDataDisplayWrapper);
        northRightBox.add(hoveredDataDisplayWrapper);
        northRightWrapper.add(northRightBox, BorderLayout.CENTER);

        northBox.add(northLeftWrapper);
        northBox.add(northRightWrapper);
        northWrapper.add(northBox, BorderLayout.CENTER);

        popupPanel.add(northWrapper, BorderLayout.NORTH);

        /*
         *
         */

        for (final IThreadSelectable threadSelectable : threadSelectables)
        {
            threadSelectable.registerComponentToRepaintOnSelection(zoomedThreadRadar);
            threadSelectable.registerComponentToRepaintOnSelection(selectedMetricLabel);
            threadSelectable.registerComponentToRepaintOnSelection(selectedNumberOfThreadsLabel);
            threadSelectable.registerComponentToRepaintOnSelection(selectedNumberOfThreadTypesLabel);
        }

        return popupPanel;
    }

    @Override
    protected String createPopupTitle(AArtifact artifact)
    {
        final StringBuilder titleStringBuilder = new StringBuilder();
        titleStringBuilder.append(artifact.getName());
        titleStringBuilder.append(" - ");
        titleStringBuilder.append(primaryMetricIdentifier.getDisplayString());
        titleStringBuilder.append(": ");
        final double metricValue = artifact.getNumericalMetricValue(primaryMetricIdentifier);
        final String percentage = CoreUtil.formatPercentage(metricValue);
        titleStringBuilder.append(percentage);

        if (secondaryMetricIdentifier != null)
        {
            titleStringBuilder.append(" ");
            titleStringBuilder.append(secondaryMetricIdentifier.getDisplayString());
            titleStringBuilder.append(": ");
            final double secondaryMetricValue = artifact.getNumericalMetricValue(secondaryMetricIdentifier);
            titleStringBuilder.append(CoreUtil.formatPercentage(secondaryMetricValue));
            if (metricValue > 0)
            {
                titleStringBuilder.append(" (");
                titleStringBuilder.append(CoreUtil.formatPercentage(secondaryMetricValue / metricValue));
                titleStringBuilder.append(" of ");
                titleStringBuilder.append(percentage);
                titleStringBuilder.append(")");
            }
        }
        return titleStringBuilder.toString();
    }

    @Override
    public void onHover(final ThreadArtifactCluster cluster)
    {
        zoomedThreadRadar.onHoverCluster(cluster.getId());
        updateHoverLabels(cluster);
    }

    @Override
    public void onExit()
    {
        for (JLabel hoverLabel : hoverLabels)
        {
            hoverLabel.setText("");
        }
        hoverLabels[0].setText("Hover over a thread cluster for more information");
        zoomedThreadRadar.unHoverCluster();
    }

    private void updateHoverLabels(ThreadArtifactCluster cluster)
    {
        if (threadSelectables.size() < 1)
        {
            return;
        }
        ThreadArtifactDisplayData hoveredThreadData =
                threadArtifactsDisplayData.getDisplayDataOfHoveredThreads(artifact,
                        threadSelectables.get(0).getSelectedThreadArtifactsOfCluster(cluster));
        if (hoveredThreadData == null)
        {
            hoveredThreadData = new ThreadArtifactDisplayData();
        }

        hoverLabels[0].setText("Metric (sum): " + CoreUtil.formatPercentage(hoveredThreadData.getMetricValueSum()));
        hoverLabels[1].setText("Metric (avg): " + CoreUtil.formatPercentage(hoveredThreadData.getMetricValueAvg()));

        String numberOfThreadsString = LocalizationUtil.getLocalizedString("codesparks.ui.popup.thread.numberofthreads");
        String numberOfClassesString = LocalizationUtil.getLocalizedString("codesparks.ui.popup.thread.numberoftypes");

        hoverLabels[2].setText(numberOfClassesString + " : " + hoveredThreadData.getNumberOfThreadTypes());
        hoverLabels[3].setText(numberOfThreadsString + " : " + hoveredThreadData.getNumberOfThreads());
    }
}