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

/*
 * Copyright (c), Oliver Moseler, 2020
 */
public final class ThreadListCellRenderer implements ListCellRenderer<JBCheckBox>
{
    private final Color[] displayColors;
    private final Color[] selectedColors;
    private final boolean[] selected;
    private final Map<String, Integer> threadIdIndex;

    public ThreadListCellRenderer(final ASourceCodeArtifact artifact, final IMetricIdentifier metricIdentifier)
    {
        int numberOfThreads = artifact.getNumberOfThreads();
        this.displayColors = new Color[numberOfThreads];
        this.selectedColors = new Color[numberOfThreads];
        this.selected = new boolean[numberOfThreads];
        this.threadIdIndex = new HashMap<>();

        List<ThreadArtifactCluster> threadArtifactClusters = artifact.getSortedDefaultThreadArtifactClustering(metricIdentifier);

        VisualThreadClusterPropertiesManager propertiesManager = VisualThreadClusterPropertiesManager.getInstance();

        int colArrayIndex = 0;

        for (ThreadArtifactCluster threadArtifactCluster : threadArtifactClusters)
        {
            VisualThreadClusterProperties properties = propertiesManager.getProperties(threadArtifactCluster);
            JBColor color;
            if (properties == null)
            {
                color = JBColor.BLACK;
            } else
            {
                color = properties.getColor();
            }
            for (AThreadArtifact codeSparksThread : threadArtifactCluster)
            {
                boolean filtered = codeSparksThread.isFiltered();
                this.displayColors[colArrayIndex] = filtered ? JBColor.GRAY : color;
                this.selectedColors[colArrayIndex] = color;
                this.selected[colArrayIndex] = !filtered;
                threadIdIndex.put(codeSparksThread.getIdentifier(), colArrayIndex);
                colArrayIndex += 1;
            }
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
            for (AThreadArtifact codeSparksThread : cluster)
            {
                Integer index = threadIdIndex.get(codeSparksThread.getIdentifier());
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
            List<Integer> indices = new ArrayList<>();
            for (AThreadArtifact codeSparksThread : cluster)
            {
                Integer index = threadIdIndex.get(codeSparksThread.getIdentifier());
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

    private void enableColors(List<Integer> ints)
    {
        for (int index : ints)
        {
            displayColors[index] = selectedColors[index];
        }
    }

}