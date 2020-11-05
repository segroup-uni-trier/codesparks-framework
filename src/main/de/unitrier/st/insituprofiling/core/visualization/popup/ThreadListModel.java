package de.unitrier.st.insituprofiling.core.visualization.popup;

import com.intellij.ui.components.JBCheckBox;
import de.unitrier.st.insituprofiling.core.data.AProfilingArtifact;
import de.unitrier.st.insituprofiling.core.data.ThreadArtifact;
import de.unitrier.st.insituprofiling.core.data.ThreadArtifactCluster;
import de.unitrier.st.insituprofiling.core.data.ThreadArtifactComparator;

import javax.swing.*;
import java.util.ArrayList;
import java.util.List;

public final class ThreadListModel extends DefaultListModel<JBCheckBox>
{
    private final int totalSize;
    private final String[] threadStrings;
    private final List<ThreadArtifact> threadArtifacts;

    public ThreadListModel(AProfilingArtifact artifact)
    {
        totalSize = artifact.getNumberOfThreads();
        threadStrings = new String[totalSize];
        threadArtifacts = new ArrayList<>(totalSize);
        int artifactCnt = 0;

        List<ThreadArtifactCluster> threadArtifactClusters = artifact.getSortedDefaultThreadArtifactClustering();

        for (ThreadArtifactCluster threadArtifactCluster : threadArtifactClusters)
        {
            threadArtifactCluster.sort(new ThreadArtifactComparator());
            for (ThreadArtifact threadArtifact : threadArtifactCluster)
            {
                String threadArtifactToString = threadArtifact.getDisplayString();

                threadArtifacts.add(threadArtifact);
                //threadArtifacts.set(artifactCnt, threadArtifact);
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
    public JBCheckBox getElementAt(int index)
    {
        JBCheckBox jbCheckBox = new JBCheckBox(threadStrings[index]);
        jbCheckBox.setFont(new JBCheckBox().getFont());
        return jbCheckBox;
    }

    public ThreadArtifact getThreadArtifactAt(int index)
    {
        if (index < 0 || index > threadArtifacts.size() - 1)
        {
            return null;
        }
        return threadArtifacts.get(index);
    }
}
