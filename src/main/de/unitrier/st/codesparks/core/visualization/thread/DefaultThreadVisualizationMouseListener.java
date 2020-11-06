package de.unitrier.st.codesparks.core.visualization.thread;

import com.intellij.ui.JBColor;
import com.intellij.ui.components.JBPanel;
import com.intellij.ui.components.JBScrollPane;
import com.intellij.util.ui.components.BorderLayoutPanel;
import de.unitrier.st.codesparks.core.ProfilingFlowManager;
import de.unitrier.st.codesparks.core.data.*;
import de.unitrier.st.codesparks.core.localization.LocalizationUtil;
import de.unitrier.st.codesparks.core.visualization.AArtifactVisualizationMouseListener;
import de.unitrier.st.codesparks.core.visualization.popup.AThreadSelectable;
import de.unitrier.st.codesparks.core.visualization.popup.PopupPanel;
import de.unitrier.st.codesparks.core.visualization.popup.ThreadList;

import javax.swing.*;
import java.awt.*;

public class DefaultThreadVisualizationMouseListener extends AArtifactVisualizationMouseListener
{
    DefaultThreadVisualizationMouseListener(JComponent component, AProfilingArtifact artifact)
    {
        super(component, new Dimension(520, 170), artifact);
    }

    @Override
    protected PopupPanel createPopupContent(AProfilingArtifact artifact)
    {
        final PopupPanel popupPanel = new PopupPanel(new BorderLayout(), "DefaultThreadVisualizationPopup");
        JBPanel<BorderLayoutPanel> centerPanel = new JBPanel<>();
        centerPanel.setLayout(new BoxLayout(centerPanel, BoxLayout.X_AXIS));

        JBPanel<BorderLayoutPanel> threadPanel = new JBPanel<>(new BorderLayout());
        AThreadSelectable threadSelectable;

        /*
         * Switch between thread tree and thread list
         */

        threadSelectable = new ThreadList(artifact);
//        threadSelectable = new ThreadTree(artifact);

        final JBScrollPane threadScrollPane = new JBScrollPane(threadSelectable.getComponent());

        threadScrollPane.setPreferredSize(new Dimension(400, 50));
        threadPanel.add(threadScrollPane, BorderLayout.CENTER);
        centerPanel.add(threadPanel);

        JBPanel<BorderLayoutPanel> buttonsPanel = new JBPanel<>();
        buttonsPanel.setLayout(new BoxLayout(buttonsPanel, BoxLayout.Y_AXIS));
        JBPanel<BorderLayoutPanel> buttonsPanelWrapper = new JBPanel<>(new BorderLayout());

        // Reset thread filter global button
        final JButton resetThreadFilterGlobal = new JButton(
                LocalizationUtil.getLocalizedString("profiling.ui.button.reset.thread.filter.global"));
        resetThreadFilterGlobal.addActionListener(e -> {
            popupPanel.cancelPopup();
            ProfilingFlowManager.getInstance().getCurrentProfilingFlow().applyThreadArtifactFilter(GlobalResetThreadArtifactFilter.getInstance());
        });
        JBPanel<BorderLayoutPanel> resetThreadFilterGlobalButtonWrapper = new JBPanel<>(new BorderLayout());
        resetThreadFilterGlobalButtonWrapper.add(resetThreadFilterGlobal, BorderLayout.CENTER);
        buttonsPanel.add(resetThreadFilterGlobalButtonWrapper);

        // Deselect all button
        final JButton deselectAll = new JButton(LocalizationUtil.getLocalizedString("profiling.ui.popup.deselectallbutton"));
        deselectAll.addActionListener(e -> threadSelectable.deselectAll());
        JBPanel<BorderLayoutPanel> deselectAllButtonWrapper = new JBPanel<>(new BorderLayout());
        deselectAllButtonWrapper.add(deselectAll, BorderLayout.CENTER);
        buttonsPanel.add(deselectAllButtonWrapper);

        // Select all button
        final JButton selectAll = new JButton(LocalizationUtil.getLocalizedString("profiling.ui.popup.selectallbutton"));
        selectAll.addActionListener(e -> threadSelectable.selectAll());
        JBPanel<BorderLayoutPanel> selectAllButtonWrapper = new JBPanel<>(new BorderLayout());
        selectAllButtonWrapper.add(selectAll, BorderLayout.CENTER);
        buttonsPanel.add(selectAllButtonWrapper);

        // Toggle cluster buttons.
        ThreadArtifactClustering sortedDefaultThreadArtifactClustering = artifact.getSortedDefaultThreadArtifactClustering();
        for (ThreadArtifactCluster cluster : sortedDefaultThreadArtifactClustering)
        {
            if (cluster.isEmpty())
            {
                continue;
            }
            VisualThreadArtifactClusterProperties properties =
                    VisualThreadArtifactClusterPropertiesManager.getInstance().getProperties(cluster);
            Color foregroundColor;
            if (properties == null)
            {
                foregroundColor = JBColor.BLACK;
            } else
            {
                foregroundColor = properties.getColor();
            }
            final JButton clusterToggle =
                    new JButton(LocalizationUtil.getLocalizedString("profiling.ui.overview.button.threads.togglecluster"));
            clusterToggle.setForeground(foregroundColor);
            clusterToggle.addActionListener(e ->
                    threadSelectable.toggleCluster(cluster));
            JBPanel<BorderLayoutPanel> clusterButtonWrapper = new JBPanel<>(new BorderLayout());
            clusterButtonWrapper.add(clusterToggle, BorderLayout.CENTER);
            buttonsPanel.add(clusterButtonWrapper);
        }

        // Apply thread filter button.
        final JButton applyThreadFilter =
                new JButton(LocalizationUtil.getLocalizedString("profiling.ui.popup.button.apply.thread.filter"));
        applyThreadFilter.addActionListener(e -> {
            popupPanel.cancelPopup();
            final IThreadArtifactFilter threadArtifactFilter = new DefaultThreadArtifactFilter(threadSelectable);
            ProfilingFlowManager.getInstance().getCurrentProfilingFlow().applyThreadArtifactFilter(threadArtifactFilter);
        });
        JBPanel<BorderLayoutPanel> applyThreadFilterButtonWrapper = new JBPanel<>(new BorderLayout());
        applyThreadFilterButtonWrapper.add(applyThreadFilter, BorderLayout.CENTER);
        buttonsPanel.add(applyThreadFilterButtonWrapper);

        buttonsPanelWrapper.add(buttonsPanel, BorderLayout.CENTER);
        centerPanel.add(buttonsPanelWrapper);
        popupPanel.add(centerPanel, BorderLayout.CENTER);

        //popupPanel.add(applyThreadFilter, BorderLayout.SOUTH);
        return popupPanel;
    }

    @Override
    protected String createPopupTitle(AProfilingArtifact artifact)
    {
        return "Total number of threads: " + artifact.getNumberOfThreads() +
                " | Different thread types: " + artifact.getThreadTypeLists().size();
    }
}
