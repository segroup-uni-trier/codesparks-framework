/*
 * Copyright (c) 2021. Oliver Moseler
 */
package de.unitrier.st.codesparks.core.visualization.popup;

import com.intellij.ui.JBColor;
import com.intellij.ui.components.JBCheckBox;
import com.intellij.ui.components.JBTextArea;
import de.unitrier.st.codesparks.core.data.*;
import de.unitrier.st.codesparks.core.visualization.thread.VisualThreadClusterProperties;
import de.unitrier.st.codesparks.core.visualization.thread.VisualThreadClusterPropertiesManager;

import javax.swing.*;
import java.awt.*;
import java.util.*;
import java.util.List;

public final class ThreadListCellRenderer implements ListCellRenderer<JBCheckBox>
{
    private final Color[] displayColors;
    private final Color[] selectedColors;
    private final boolean[] selected;
    private final Map<String, Integer> threadIdIndex;

    public ThreadListCellRenderer(final AArtifact artifact, final AMetricIdentifier metricIdentifier, final boolean applyClusterColors)
    {
        int numberOfThreads = artifact.getNumberOfThreads();
        this.displayColors = new Color[numberOfThreads];
        this.selectedColors = new Color[numberOfThreads];
        this.selected = new boolean[numberOfThreads];
        this.threadIdIndex = new HashMap<>();

        final ThreadArtifactClustering clustering =
                artifact.clusterThreadArtifacts(ConstraintKMeansWithAMaximumOfThreeClusters.getInstance(metricIdentifier));

        final VisualThreadClusterPropertiesManager propertiesManager = VisualThreadClusterPropertiesManager.getInstance(clustering);

        int colArrayIndex = 0;
        int clusterNum = 0;
        for (final ThreadArtifactCluster threadArtifactCluster : clustering)
        {
            JBColor color = JBColor.BLACK;
            if (applyClusterColors)
            {
                final VisualThreadClusterProperties properties = propertiesManager.getOrDefault(threadArtifactCluster, clusterNum);
                color = properties.getColor();
            }
            for (final AThreadArtifact threadArtifact : threadArtifactCluster)
            {
                final boolean filtered = threadArtifact.isFiltered();
                this.displayColors[colArrayIndex] = filtered ? JBColor.GRAY : color;
                this.selectedColors[colArrayIndex] = color;
                this.selected[colArrayIndex] = !filtered;
                threadIdIndex.put(threadArtifact.getIdentifier(), colArrayIndex);
                colArrayIndex += 1;
            }
            clusterNum += 1;
        }
    }

    @Override
    public Component getListCellRendererComponent(JList<? extends JBCheckBox> list, JBCheckBox value, int index, boolean isSelected,
                                                  boolean cellHasFocus)
    {
        value.setForeground(displayColors[index]);
        value.setSelected(selected[index]);
        value.setFont(new JBTextArea().getFont());
        return value;
    }

    public boolean[] getSelected()
    {
        synchronized (selected)
        {
            return selected;
        }
    }

    public void toggleSelected(int index)
    {
        synchronized (selected)
        {
            updateColor(index);
            selected[index] = !selected[index];
        }
    }

    public void deselectAll()
    {
        synchronized (selected)
        {
            for (int i = 0; i < selected.length; i++)
            {
                disableColor();
                selected[i] = false;
            }
        }
    }

    public void selectAll()
    {
        synchronized (selected)
        {
            for (int i = 0; i < selected.length; i++)
            {
                enableColor();
                selected[i] = true;
            }
        }
    }

    public void toggleCluster(ThreadArtifactCluster cluster)
    {
        synchronized (selected)
        {
            for (final AThreadArtifact threadArtifact : cluster)
            {
                final int index = threadIdIndex.get(threadArtifact.getIdentifier());
                updateColor(index);
                selected[index] = !selected[index];
            }
        }
    }

    @SuppressWarnings("unused")
    public void selectCluster(ThreadArtifactCluster cluster)
    {
        synchronized (selected)
        {
            final List<Integer> indices = new ArrayList<>();
            for (final AThreadArtifact threadArtifact : cluster)
            {
                final int index = threadIdIndex.get(threadArtifact.getIdentifier());
                selected[index] = true;
                indices.add(index);
            }
            enableColors(indices);
        }
    }

    private void updateColor(int index)
    {
        if (selected[index])
        {
            displayColors[index] = JBColor.GRAY;
        } else
        {
            displayColors[index] = selectedColors[index];
        }
    }

    private void disableColor()
    {
        Arrays.fill(displayColors, JBColor.GRAY);
    }

    private void enableColor()
    {
        System.arraycopy(selectedColors, 0, displayColors, 0, displayColors.length);
    }

    private void enableColors(List<Integer> indices)
    {
        for (int index : indices)
        {
            displayColors[index] = selectedColors[index];
        }
    }

}