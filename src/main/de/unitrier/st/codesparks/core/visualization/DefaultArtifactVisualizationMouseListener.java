package de.unitrier.st.codesparks.core.visualization;

import com.intellij.ui.components.JBScrollPane;
import com.intellij.ui.components.JBTabbedPane;
import de.unitrier.st.codesparks.core.CoreUtil;
import de.unitrier.st.codesparks.core.data.AArtifact;
import de.unitrier.st.codesparks.core.data.ANeighborArtifact;
import de.unitrier.st.codesparks.core.data.IMetricIdentifier;
import de.unitrier.st.codesparks.core.logging.UserActivityEnum;
import de.unitrier.st.codesparks.core.logging.UserActivityLogger;
import de.unitrier.st.codesparks.core.visualization.popup.*;

import javax.swing.*;
import java.awt.*;
import java.util.List;
import java.util.stream.Collectors;

/*
 * Copyright (c), Oliver Moseler, 2020
 */
public class DefaultArtifactVisualizationMouseListener extends AArtifactVisualizationMouseListener
{
    private final IMetricIdentifier secondaryMetricIdentifier;

    public DefaultArtifactVisualizationMouseListener(
            final JComponent component
            , final AArtifact artifact
            , final IMetricIdentifier primaryMetricIdentifier
            , final IMetricIdentifier secondaryMetricIdentifier
    )
    {
        super(component, new Dimension(500, 175), artifact, primaryMetricIdentifier);
        this.secondaryMetricIdentifier = secondaryMetricIdentifier;
    }

    @Override
    protected PopupPanel createPopupContent(AArtifact artifact)
    {
        final PopupPanel popupPanel = new PopupPanel(new BorderLayout(), "MethodPopup");

        /*
         * Do not remove the following disabled code!
         */
//        final MetricTableModel metricTableModel = new MetricTableModel(artifact);
//        final MetricTable metricTable = new MetricTable(metricTableModel)
//        {
//            @Override
//            public String getToolTipText(@NotNull MouseEvent e)
//            {
//                Point p = e.getPoint();
//                int rowIndex = rowAtPoint(p);
//                int colIndex = columnAtPoint(p);
//                ANeighborProfilingArtifact neighborArtifactAt = metricTableModel.getNeighborArtifactAt(rowIndex, colIndex);
//                return neighborArtifactAt == null ? "" : neighborArtifactAt.getIdentifier();
//            }
//        };
//        metricTable.setExpandableItemsEnabled(false);
//        metricTable.setDefaultRenderer(Object.class, new MetricTableCellRenderer());
//        metricTable.addMouseMotionListener(new MetricTableMouseMotionAdapter(metricTable));
//        metricTable.setDoubleBuffered(true);
//        JBScrollPane runtimeScrollPane = new JBScrollPane(metricTable);

        JBTabbedPane tabbedPane = new JBTabbedPane();

        List<ANeighborArtifact> artifactSuccessorsList = artifact.getSuccessorsList()
                .stream()
                .filter(npa -> !npa.getName().toLowerCase().startsWith("self"))
                .filter(npa -> npa.getThreadArtifacts()
                        .stream()
                        .anyMatch(threadArtifact -> !threadArtifact.isFiltered()))
                .collect(Collectors.toList());

        MetricList successorsList = new MetricList(new NumericalMetricListModel(artifact, primaryMetricIdentifier, artifactSuccessorsList));
        successorsList.addMouseMotionListener(new MetricListMouseMotionAdapter(successorsList));
        successorsList.setCellRenderer(new MetricListCellRenderer());

        List<ANeighborArtifact> artifactPredecessorsList =
                artifact.getPredecessorsList()
                        .stream()
                        .filter(npa -> npa.getThreadArtifacts()
                                .stream()
                                .anyMatch(threadArtifact -> !threadArtifact.isFiltered()))
                        .collect(Collectors.toList());

        MetricList predecessorList = new MetricList(new NumericalMetricListModel(artifact, primaryMetricIdentifier, artifactPredecessorsList));
        predecessorList.addMouseMotionListener(new MetricListMouseMotionAdapter(predecessorList));
        predecessorList.setCellRenderer(new MetricListCellRenderer());

        tabbedPane.add("Callers", new JBScrollPane(predecessorList)); // TODO: this is JPT code because of the term callers
        tabbedPane.add("Callees", new JBScrollPane(successorsList)); // TODO: this is JPT code because of the term callees

        final int selectedIndex = 1;

        tabbedPane.setSelectedIndex(selectedIndex);

//        if (selectedIndex == 0)
//        {
//            UserActivityLogger.getInstance().log(UserActivityEnum.MethodPopupCallersTabSelected, artifact.getIdentifier());
//        } else
//        {
//            UserActivityLogger.getInstance().log(UserActivityEnum.MethodPopupCalleesTabSelected, artifact.getIdentifier());
//        }

        MetricListPopupMouseAdapter metricListPopupMouseAdapter = new MetricListPopupMouseAdapter(popupPanel, successorsList);
        successorsList.addMouseListener(metricListPopupMouseAdapter);
        MetricListPopupMouseAdapter metricListPopupMouseAdapter2 = new MetricListPopupMouseAdapter(popupPanel, predecessorList);
        predecessorList.addMouseListener(metricListPopupMouseAdapter2);

//        MetricTablePopupMouseAdapter metricTablePopupMouseAdapter = new MetricTablePopupMouseAdapter(popupPanel, metricTable);
//        popupPanel.add(runtimeScrollPane, BorderLayout.CENTER);
//        metricTable.addMouseListener(metricTablePopupMouseAdapter);

        popupPanel.add(tabbedPane, BorderLayout.CENTER);

        tabbedPane.addChangeListener(e -> {

            int tabSelected = tabbedPane.getSelectedIndex();

            if (tabSelected == 0)
            {
                UserActivityLogger.getInstance().log(UserActivityEnum.MethodPopupCallersTabEntered);
            } else
            {
                UserActivityLogger.getInstance().log(UserActivityEnum.MethodPopupCalleesTabEntered);
            }
        });

        //popupPanel.add(new JPanel(), BorderLayout.SOUTH);
        return popupPanel;
    }

    @Override
    protected String createPopupTitle(final AArtifact artifact)
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
}
