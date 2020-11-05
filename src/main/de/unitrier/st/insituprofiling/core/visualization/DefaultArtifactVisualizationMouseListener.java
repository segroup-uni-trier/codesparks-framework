package de.unitrier.st.insituprofiling.core.visualization;

import com.intellij.ui.components.JBScrollPane;
import com.intellij.ui.components.JBTabbedPane;
import de.unitrier.st.insituprofiling.core.CoreUtil;
import de.unitrier.st.insituprofiling.core.data.ANeighborProfilingArtifact;
import de.unitrier.st.insituprofiling.core.data.AProfilingArtifact;
import de.unitrier.st.insituprofiling.core.logging.UserActivityEnum;
import de.unitrier.st.insituprofiling.core.logging.UserActivityLogger;
import de.unitrier.st.insituprofiling.core.visualization.popup.*;

import javax.swing.*;
import java.awt.*;
import java.util.List;
import java.util.stream.Collectors;

public class DefaultArtifactVisualizationMouseListener extends AArtifactVisualizationMouseListener
{
    DefaultArtifactVisualizationMouseListener(JComponent component, AProfilingArtifact artifact)
    {
        super(component, new Dimension(500, 175), artifact);
    }

    @Override
    protected PopupPanel createPopupContent(AProfilingArtifact artifact)
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

        List<ANeighborProfilingArtifact> artifactSuccessorsList = artifact.getSuccessorsList()
                .stream()
                .filter(npa -> !npa.getName().toLowerCase().startsWith("self"))
                .filter(npa -> npa.getThreadArtifacts()
                        .stream()
                        .anyMatch(threadArtifact -> !threadArtifact.isFiltered()))
                .collect(Collectors.toList());

        MetricList successorsList = new MetricList(new MetricListModel(artifact, artifactSuccessorsList));
        successorsList.addMouseMotionListener(new MetricListMouseMotionAdapter(successorsList));
        successorsList.setCellRenderer(new MetricListCellRenderer());

        List<ANeighborProfilingArtifact> artifactPredecessorsList =
                artifact.getPredecessorsList()
                        .stream()
                        .filter(npa -> npa.getThreadArtifacts()
                                .stream()
                                .anyMatch(threadArtifact -> !threadArtifact.isFiltered()))
                        .collect(Collectors.toList());

        MetricList predecessorList = new MetricList(new MetricListModel(artifact, artifactPredecessorsList));
        predecessorList.addMouseMotionListener(new MetricListMouseMotionAdapter(predecessorList));
        predecessorList.setCellRenderer(new MetricListCellRenderer());

        tabbedPane.add("Callers", new JBScrollPane(predecessorList));
        tabbedPane.add("Callees", new JBScrollPane(successorsList));

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
    protected String createPopupTitle(AProfilingArtifact artifact)
    {
        String metricKind = "total";
        StringBuilder titleStringBuilder = new StringBuilder();
        titleStringBuilder.append(artifact.getTitleName());
        titleStringBuilder.append(": ");
        titleStringBuilder.append(metricKind);
        titleStringBuilder.append(": ");
        //titleStringBuilder.append(CoreUtil.formatPercentage(artifact.getRuntime()));
        titleStringBuilder.append(artifact.getMetricValueText());
        titleStringBuilder.append(" - ");
        titleStringBuilder.append("self: ");
//        titleStringBuilder.append(CoreUtil.formatPercentage(artifact.getRuntimeSelf()));
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
}
