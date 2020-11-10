package de.unitrier.st.codesparks.core.visualization.thread;

import com.intellij.ui.JBColor;
import com.intellij.ui.components.JBScrollPane;
import com.intellij.ui.components.JBTabbedPane;
import de.unitrier.st.codesparks.core.ACodeSparksFlow;
import de.unitrier.st.codesparks.core.CoreUtil;
import de.unitrier.st.codesparks.core.CodeSparksFlowManager;
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

public class RadialThreadVisualizationMouseListener extends AArtifactVisualizationMouseListener implements IClusterHoverable
{
    private RadialZoomedThreadVisualization radialVisualization;
    private final List<IThreadSelectable> threadSelectables;
    private final IRadialThreadVisualizationDisplayData radialThreadVisualizationPopupData;
    private final JLabel[] hoverLabels;

    public RadialThreadVisualizationMouseListener(JComponent component,
                                                  AArtifact artifact,
                                                  IRadialThreadVisualizationDisplayData radialThreadVisualizationPopupData)
    {
        super(component, new Dimension(500, 340), artifact);
        this.component = component;
        this.hoverLabels = new JLabel[4];
        this.threadSelectables = new ArrayList<>();
        this.radialThreadVisualizationPopupData = radialThreadVisualizationPopupData;
        component.addMouseMotionListener(this);
    }

    @Override
    protected PopupPanel createPopupContent(AArtifact artifact)
    {
        PopupPanel popupPanel = new PopupPanel(new BorderLayout(), "ThreadRadarPopup");

        threadSelectables.clear();
        final JBTabbedPane tabbedPane = new JBTabbedPane();

        CodeSparksThreadClustering sortedDefaultCodeSparksThreadClustering = artifact.getSortedDefaultThreadArtifactClustering();
        Map<String, List<CodeSparksThread>> map = new HashMap<>();
        int clusterId = 1;
        for (CodeSparksThreadCluster threadArtifacts : sortedDefaultCodeSparksThreadClustering)
        {
            map.put("Cluster:" + clusterId++, threadArtifacts);
        }

        AThreadSelectable threadClustersTree = new ThreadClusterTree(map);
        threadSelectables.add(threadClustersTree);
        tabbedPane.addTab("Clusters", new JBScrollPane(threadClustersTree.getComponent()));

        // -------------

        final Map<String, List<CodeSparksThread>> threadTypeLists = artifact.getThreadTypeLists();
        AThreadSelectable threadTypesTree = new ThreadTypeTree(threadTypeLists,
                sortedDefaultCodeSparksThreadClustering);
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
        JPanel controlButtonsWrapper = new JPanel(new BorderLayout());
        JPanel controlButtonsBox = new JPanel();
        controlButtonsBox.setLayout(new BoxLayout(controlButtonsBox, BoxLayout.X_AXIS));
        JButton selectAll = new JButton(LocalizationUtil.getLocalizedString("profiling.ui.popup.selectallbutton"));
        selectAll.addActionListener(e -> {

            UserActivityLogger.getInstance().log(UserActivityEnum.ThreadRadarDetailsViewSelectAllButtonClicked);

            for (IThreadSelectable threadSelectable : this.threadSelectables)
            {
                threadSelectable.selectAll();
            }
        });
        JButton deselectAll = new JButton(LocalizationUtil.getLocalizedString("profiling.ui.popup.deselectallbutton"));
        deselectAll.addActionListener(e -> {

            UserActivityLogger.getInstance().log(UserActivityEnum.ThreadRadarDetailsViewDeselectAllButtonClicked);

            for (IThreadSelectable threadSelectable : this.threadSelectables)
            {
                threadSelectable.deselectAll();
            }
        });

        JButton invert = new JButton(LocalizationUtil.getLocalizedString("profiling.ui.popup.invertallbutton"));
        invert.addActionListener(e -> {

            UserActivityLogger.getInstance().log(UserActivityEnum.ThreadRadarDetailsViewInvertSelectionButtonClicked);

            for (IThreadSelectable threadSelectable : this.threadSelectables)
            {
                threadSelectable.invertAll();
            }
        });

        JButton reset = new JButton(LocalizationUtil.getLocalizedString("profiling.ui.button.reset.thread.filter.global"));
        reset.addActionListener(e -> {

            UserActivityLogger.getInstance().log(UserActivityEnum.ThreadRadarDetailsViewResetThreadFilterButtonClicked);

            popupPanel.cancelPopup();
            threadSelectables.forEach(IThreadSelectable::selectAll);
            ACodeSparksFlow currentProfilingFlow = CodeSparksFlowManager.getInstance().getCurrentCodeSparksFlow();
            currentProfilingFlow.applyThreadArtifactFilter(GlobalResetThreadFilter.getInstance());
        });

        JButton apply = new JButton(LocalizationUtil.getLocalizedString("profiling.ui.popup.button.apply.thread.filter"));
        apply.addActionListener(e -> {

            UserActivityLogger.getInstance().log(UserActivityEnum.ThreadRadarDetailsViewApplyThreadFilterButtonClicked);

            popupPanel.cancelPopup();
            int index = indexProvider.getThreadSelectableIndex();
            IThreadSelectable iThreadSelectable = threadSelectables.get(index);
            final ICodeSparksThreadFilter iCodeSparksThreadFilter = new DefaultThreadFilter(iThreadSelectable);
            CodeSparksFlowManager.getInstance().getCurrentCodeSparksFlow().applyThreadArtifactFilter(iCodeSparksThreadFilter);
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
        JPanel northWrapper = new JPanel(new BorderLayout());
        JPanel northBox = new JPanel();
        northBox.setLayout(new BoxLayout(northBox, BoxLayout.X_AXIS));
        JPanel northLeftWrapper = new JPanel(new BorderLayout());
        JPanel northLeftBox = new JPanel();
        northLeftBox.setLayout(new BoxLayout(northLeftBox, BoxLayout.Y_AXIS));
        northLeftBox.setPreferredSize(new Dimension(150, 160));
        JPanel radialVisualizationWrapper = new JPanel(new BorderLayout());
        JPanel radialVisualizationBox = new JPanel();
        radialVisualizationBox.setLayout(new BoxLayout(radialVisualizationBox, BoxLayout.Y_AXIS));
        JPanel radialVisualizationButtonsWrapper = new JPanel(new BorderLayout());
        JPanel radialVisualizationButtonsBox = new JPanel();
        radialVisualizationButtonsBox.setLayout(new BoxLayout(radialVisualizationButtonsBox, BoxLayout.X_AXIS));
        JPanel northRightWrapper = new JPanel(new BorderLayout());
        JPanel northRightBox = new JPanel();
        northRightBox.setLayout(new BoxLayout(northRightBox, BoxLayout.Y_AXIS));
        northRightBox.setPreferredSize(new Dimension(170, 200));
        JPanel selectedDataDisplayWrapper = new JPanel(new BorderLayout());
        JPanel selectedDataDisplayBox = new JPanel();
        selectedDataDisplayBox.setLayout(new BoxLayout(selectedDataDisplayBox, BoxLayout.Y_AXIS));
        selectedDataDisplayBox.setPreferredSize(new Dimension(170, 50));
        JPanel hoveredDataDisplayWrapper = new JPanel(new BorderLayout());
        JPanel hoveredDataDisplayBox = new JPanel();
        hoveredDataDisplayBox.setLayout(new BoxLayout(hoveredDataDisplayBox, BoxLayout.Y_AXIS));
        hoveredDataDisplayBox.setPreferredSize(new Dimension(170, 50));

        //Radial Visualization

        radialVisualization = new RadialZoomedThreadVisualization(artifact,
                indexProvider,
                threadSelectables);
        radialVisualization.setBorder(BorderFactory.createEmptyBorder(0, 50, 0, 0));
        RadialZoomedThreadVisualizationMouseAdapter mouseAdapter =
                new RadialZoomedThreadVisualizationMouseAdapter(radialVisualization, artifact, this, northLeftWrapper);
        radialVisualization.addMouseMotionListener(mouseAdapter);
        radialVisualization.addMouseListener(mouseAdapter);


        final int numberOfClusters = sortedDefaultCodeSparksThreadClustering.size();

        // Cluster selection buttons

        JButton[] threadClusterSelectionButtons =
                {
                        new JButton("C1"), new JButton("C2"), new JButton("C3")
                };

        VisualThreadClusterPropertiesManager manager = VisualThreadClusterPropertiesManager.getInstance();

        for (int i = 0; i < threadClusterSelectionButtons.length; i++)
        {
            final int indexToUse = i;
            threadClusterSelectionButtons[i].addActionListener(e ->
                    {
                        final CodeSparksThreadCluster cluster = sortedDefaultCodeSparksThreadClustering.get(indexToUse);
                        threadSelectables.forEach(iThreadSelectable -> iThreadSelectable.toggleCluster(cluster));
                        UserActivityLogger.getInstance().log(UserActivityEnum.ThreadClusterToggleButtonClicked,
                                "buttonIndex=" + indexToUse, "clusterId=" + cluster.getId(),
                                String.valueOf(cluster));
                    }
            );

            if (i < numberOfClusters)
            {
                CodeSparksThreadCluster cluster = sortedDefaultCodeSparksThreadClustering.get(i);

                if (cluster.size() < 1)
                {
                    threadClusterSelectionButtons[i].setEnabled(false);
                    continue;
                }
                VisualThreadClusterProperties properties = manager.getProperties(cluster);
                JBColor color = properties.getColor();
                threadClusterSelectionButtons[i].setForeground(color);
            } else
            {
                threadClusterSelectionButtons[i].setEnabled(false);
            }
        }

        JLabel radialButtonsToggleLabel =
                new JLabel(LocalizationUtil.getLocalizedString("profiling.ui.overview.button.threads.togglecluster"));
        radialButtonsToggleLabel.setFont(radialButtonsToggleLabel.getFont().deriveFont(12.0f));
        radialButtonsToggleLabel.setAlignmentX(JLabel.CENTER_ALIGNMENT);
        radialVisualizationButtonsBox.add(radialButtonsToggleLabel);
        for (int i = 0; i < 3; i++)
        {
            threadClusterSelectionButtons[i].setPreferredSize(new Dimension((RadialThreadVisualizationConstants.FRAME_ZOOMED) / 3, 20));
            radialVisualizationButtonsBox.add(threadClusterSelectionButtons[i]);
        }

        // ---------------------------------------------
        CodeSparksThreadDisplayData selectedData =
                radialThreadVisualizationPopupData.getSelectedThreadData(artifact, threadClustersTree.getSelectedThreadArtifacts());
        if (selectedData == null)
        {
            selectedData = new CodeSparksThreadDisplayData();
        }
        //final ThreadArtifactDisplayData finalSelectedData = selectedData;

        final String metricString = LocalizationUtil.getLocalizedString("profiling.ui.popup.thread.metric");
        JLabel selectedMetricLabel = new JLabel(metricString + " : " + CoreUtil.formatPercentage(selectedData.getMetricValueSum()))
        {
            @Override
            public void repaint()
            {
                int index = indexProvider.getThreadSelectableIndex();
                IThreadSelectable iThreadSelectable = threadSelectables.get(index);
                CodeSparksThreadDisplayData selectedThreadData =
                        radialThreadVisualizationPopupData.getSelectedThreadData(artifact, iThreadSelectable.getSelectedThreadArtifacts());
                setText(metricString + " : " + CoreUtil.formatPercentage(selectedThreadData.getMetricValueSum()));
                super.repaint();
            }
        };
        selectedMetricLabel.setFont(selectedMetricLabel.getFont().deriveFont(labelFontSize));
        selectedDataDisplayBox.add(selectedMetricLabel);

        // -------------------------------

        final String numberOfThreadTypesString = LocalizationUtil.getLocalizedString("profiling.ui.popup.thread.numberoftypes");
        JLabel selectedNumberOfThreadTypesLabel = new JLabel(numberOfThreadTypesString + " : " + selectedData.getNumberOfThreadTypes())
        {
            @Override
            public void repaint()
            {
                int index = indexProvider.getThreadSelectableIndex();
                IThreadSelectable iThreadSelectable = threadSelectables.get(index);
                CodeSparksThreadDisplayData selectedThreadData =
                        radialThreadVisualizationPopupData.getSelectedThreadData(artifact, iThreadSelectable.getSelectedThreadArtifacts());
                setText(numberOfThreadTypesString + " : " + selectedThreadData.getNumberOfThreadTypes());
                super.repaint();
            }
        };
        selectedNumberOfThreadTypesLabel.setFont(selectedNumberOfThreadTypesLabel.getFont().deriveFont(labelFontSize));
        selectedDataDisplayBox.add(selectedNumberOfThreadTypesLabel);

        // -------------------------------

        final String numberOfThreadsString = LocalizationUtil.getLocalizedString("profiling.ui.popup.thread.numberofthreads");
        JLabel selectedNumberOfThreadsLabel = new JLabel(numberOfThreadsString + " : " + selectedData.getNumberOfThreads())
        {
            @Override
            public void repaint()
            {
                int index = indexProvider.getThreadSelectableIndex();
                IThreadSelectable iThreadSelectable = threadSelectables.get(index);
                CodeSparksThreadDisplayData selectedThreadData =
                        radialThreadVisualizationPopupData.getSelectedThreadData(artifact, iThreadSelectable.getSelectedThreadArtifacts());
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
            JLabel label = new JLabel("");
            label.setFont(label.getFont().deriveFont(labelFontSize));
            hoverLabels[i] = label;
            hoveredDataDisplayBox.add(label);
        }

        hoverLabels[0].setText("Hover over a thread cluster for more information");
        hoveredDataDisplayWrapper.add(hoveredDataDisplayBox, BorderLayout.CENTER);
        hoveredDataDisplayWrapper.setBorder(BorderFactory.createTitledBorder(BorderFactory.createEtchedBorder(),
                "Hovered cluster"));

        radialVisualizationBox.add(radialVisualization);
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

        for (IThreadSelectable threadSelectable : threadSelectables)
        {
            threadSelectable.registerComponentToRepaintOnSelection(radialVisualization);
            threadSelectable.registerComponentToRepaintOnSelection(selectedMetricLabel);
            threadSelectable.registerComponentToRepaintOnSelection(selectedNumberOfThreadsLabel);
            threadSelectable.registerComponentToRepaintOnSelection(selectedNumberOfThreadTypesLabel);
        }

        return popupPanel;
    }

    @Override
    protected String createPopupTitle(AArtifact artifact)
    {
        String metricKind = "total";
        StringBuilder titleStringBuilder = new StringBuilder();
        titleStringBuilder.append(artifact.getTitleName());
        titleStringBuilder.append(": ");
        titleStringBuilder.append(metricKind);
        titleStringBuilder.append(": ");
        titleStringBuilder.append(artifact.getMetricValueText());
        titleStringBuilder.append(" - ");
        titleStringBuilder.append("self: ");
        titleStringBuilder.append(artifact.getMetricValueSelfText());
        if (artifact.getMetricValue() > 0)
        {
            titleStringBuilder.append(" (");
            titleStringBuilder.append(CoreUtil.formatPercentage(artifact.getMetricValueSelf() / artifact.getMetricValue()));
            titleStringBuilder.append(" of ");
            titleStringBuilder.append(metricKind);
            titleStringBuilder.append(" )");
        }
        return titleStringBuilder.toString();
    }

    @Override
    public void onHover(CodeSparksThreadCluster cluster)
    {
        radialVisualization.onHoverCluster(cluster.getId());
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
        radialVisualization.unHoverCluster();
    }

    private void updateHoverLabels(CodeSparksThreadCluster cluster)
    {
        if (threadSelectables.size() < 1)
        {
            return;
        }
        CodeSparksThreadDisplayData hoveredThreadData =
                radialThreadVisualizationPopupData.getHoveredThreadData(artifact,
                        threadSelectables.get(0).getSelectedThreadArtifactsOfCluster(cluster));
        if (hoveredThreadData == null)
        {
            hoveredThreadData = new CodeSparksThreadDisplayData();
        }
//        hoverLabels[0].setText("Metric (sum): " + formatter.format(hoveredThreadData.getMetricValueSum() * 100.0f) + "%");
//        hoverLabels[1].setText("Metric (avg): " + formatter.format(hoveredThreadData.getMetricValueAvg() * 100.0f) + "%");

        hoverLabels[0].setText("Metric (sum): " + CoreUtil.formatPercentage(hoveredThreadData.getMetricValueSum()));
        hoverLabels[1].setText("Metric (avg): " + CoreUtil.formatPercentage(hoveredThreadData.getMetricValueAvg()));

        String numberOfThreadsString = LocalizationUtil.getLocalizedString("profiling.ui.popup.thread.numberofthreads");
        String numberOfClassesString = LocalizationUtil.getLocalizedString("profiling.ui.popup.thread.numberoftypes");

        hoverLabels[2].setText(numberOfClassesString + " : " + hoveredThreadData.getNumberOfThreadTypes());
        hoverLabels[3].setText(numberOfThreadsString + " : " + hoveredThreadData.getNumberOfThreads());
    }
}