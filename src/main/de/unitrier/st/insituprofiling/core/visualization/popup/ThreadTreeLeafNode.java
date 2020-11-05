package de.unitrier.st.insituprofiling.core.visualization.popup;

import com.intellij.ui.JBColor;
import de.unitrier.st.insituprofiling.core.data.ThreadArtifact;

public class ThreadTreeLeafNode extends ColoredSelectableTreeNode
{
    private final ThreadArtifact threadArtifact;

    ThreadTreeLeafNode(ThreadArtifact threadArtifact, JBColor color)
    {
        super(threadArtifact.getDisplayString(), color);
        this.threadArtifact = threadArtifact;
    }

    ThreadTreeLeafNode(ThreadArtifact threadArtifact)
    {
        this(threadArtifact, null);
    }

    public ThreadArtifact getThreadArtifact()
    {
        return threadArtifact;
    }
}
