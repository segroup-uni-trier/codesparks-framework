/*
 * Copyright (c) 2021. Oliver Moseler
 */
package de.unitrier.st.codesparks.core.visualization.popup;

import com.intellij.ui.components.JBCheckBox;
import de.unitrier.st.codesparks.core.data.*;

import javax.swing.*;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public final class ThreadListModel extends DefaultListModel<JBCheckBox>
{
    private final int totalSize;
    private final String[] threadStrings;
    private final List<AThreadArtifact> threadArtifacts;

    public ThreadListModel(final AArtifact artifact, final AMetricIdentifier metricIdentifier)
    {
        totalSize = artifact.getNumberOfThreads();
        threadStrings = new String[totalSize];
        threadArtifacts = new ArrayList<>(totalSize);
        int artifactCnt = 0;

        final ThreadArtifactClustering clustering =
                artifact.getSelectedClusteringOrApplyAndSelect(ConstraintKMeansWithAMaximumOfThreeClusters.getInstance(metricIdentifier));

        final Comparator<AThreadArtifact> threadArtifactComparator = ThreadArtifactComparator.getInstance(metricIdentifier);

        for (final ThreadArtifactCluster threadArtifactCluster : clustering)
        {
            threadArtifactCluster.sort(threadArtifactComparator);
            for (final AThreadArtifact threadArtifact : threadArtifactCluster)
            {
                final String threadArtifactToString = threadArtifact.getDisplayString(metricIdentifier);
                threadArtifacts.add(threadArtifact);
                threadStrings[artifactCnt] = threadArtifactToString;
                artifactCnt++;
            }
        }
    }

    @Override
    public int getSize()
    {
        return totalSize;
    }

    @Override
    public JBCheckBox getElementAt(final int index)
    {
        final JBCheckBox jbCheckBox = new JBCheckBox(threadStrings[index]);
        jbCheckBox.setFont(new JBCheckBox().getFont());
        return jbCheckBox;
    }

    public AThreadArtifact getThreadArtifactAt(final int index)
    {
        if (index < 0 || index > threadArtifacts.size() - 1)
        {
            return null;
        }
        return threadArtifacts.get(index);
    }
}
