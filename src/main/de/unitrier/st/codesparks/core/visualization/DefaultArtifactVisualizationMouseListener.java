package de.unitrier.st.codesparks.core.visualization;

import com.intellij.ui.components.JBScrollPane;
import com.intellij.ui.components.JBTabbedPane;
import de.unitrier.st.codesparks.core.CoreUtil;
import de.unitrier.st.codesparks.core.data.AArtifact;
import de.unitrier.st.codesparks.core.data.ANeighborArtifact;
import de.unitrier.st.codesparks.core.data.Metric;
import de.unitrier.st.codesparks.core.logging.UserActivityEnum;
import de.unitrier.st.codesparks.core.logging.UserActivityLogger;
import de.unitrier.st.codesparks.core.visualization.popup.*;

import javax.swing.*;
import java.awt.*;
import java.util.List;
import java.util.stream.Collectors;

public class DefaultArtifactVisualizationMouseListener extends AArtifactVisualizationMouseListener
{
    private final String secondaryMetricIdentifier;

    DefaultArtifactVisualizationMouseListener(
            JComponent component
            , AArtifact artifact
            , String primaryMetricIdentifier
            , final String secondaryMetricIdentifier
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

        tabbedPane.add("Callers", new JBScrollPane(predecessorList)); // TODO: this is JPT code
        tabbedPane.add("Callees", new JBScrollPane(successorsList)); // TODO: this is JPT code

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
        // TODO: Move this to CodeSparks-JPT! It makes use of the term 'self' etc.

        final Metric primaryMetric = artifact.getMetric(primaryMetricIdentifier);
        final Metric secondaryMetric = artifact.getMetric(secondaryMetricIdentifier);

        final String primaryMetricName = primaryMetric.getName();

        final StringBuilder titleStringBuilder = new StringBuilder();
        titleStringBuilder.append(artifact.getTitleName());
        titleStringBuilder.append(": ");
        titleStringBuilder.append(primaryMetricName);
        titleStringBuilder.append(": ");
        titleStringBuilder.append(primaryMetric.getMetricValueString());
        titleStringBuilder.append(" - ");

        final String secondaryMetricName = secondaryMetric.getName();
        titleStringBuilder.append(secondaryMetricName);
        titleStringBuilder.append(": ");
        titleStringBuilder.append(secondaryMetric.getMetricValueString()); // Secondary = self here
        final double numericalMetricValue = (double) primaryMetric.getValue();
        if (numericalMetricValue > 0)
        {
            titleStringBuilder.append(" (");
            double secondary = (double) secondaryMetric.getValue();
            titleStringBuilder.append(CoreUtil.formatPercentage(secondary / numericalMetricValue));
            titleStringBuilder.append(" of ");
            titleStringBuilder.append(primaryMetricName);
            titleStringBuilder.append(" )");
        }
        return titleStringBuilder.toString();
    }
}
