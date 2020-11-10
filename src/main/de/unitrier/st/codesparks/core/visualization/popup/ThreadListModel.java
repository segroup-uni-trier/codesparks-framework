package de.unitrier.st.codesparks.core.visualization.popup;

import com.intellij.ui.components.JBCheckBox;
import de.unitrier.st.codesparks.core.data.AArtifact;
import de.unitrier.st.codesparks.core.data.CodeSparksThread;
import de.unitrier.st.codesparks.core.data.CodeSparksThreadCluster;
import de.unitrier.st.codesparks.core.data.CodeSparksThreadComparator;

import javax.swing.*;
import java.util.ArrayList;
import java.util.List;

public final class ThreadListModel extends DefaultListModel<JBCheckBox>
{
    private final int totalSize;
    private final String[] threadStrings;
    private final List<CodeSparksThread> codeSparksThreads;

    public ThreadListModel(AArtifact artifact)
    {
        totalSize = artifact.getNumberOfThreads();
        threadStrings = new String[totalSize];
        codeSparksThreads = new ArrayList<>(totalSize);
        int artifactCnt = 0;

        List<CodeSparksThreadCluster> codeSparksThreadClusters = artifact.getSortedDefaultThreadArtifactClustering();

        for (CodeSparksThreadCluster codeSparksThreadCluster : codeSparksThreadClusters)
        {
            codeSparksThreadCluster.sort(new CodeSparksThreadComparator());
            for (CodeSparksThread codeSparksThread : codeSparksThreadCluster)
            {
                String threadArtifactToString = codeSparksThread.getDisplayString();

                codeSparksThreads.add(codeSparksThread);
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

    public CodeSparksThread getThreadArtifactAt(int index)
    {
        if (index < 0 || index > codeSparksThreads.size() - 1)
        {
            return null;
        }
        return codeSparksThreads.get(index);
    }
}
