package de.unitrier.st.codesparks.core.visualization.popup;

import com.intellij.ui.JBColor;
import de.unitrier.st.codesparks.core.data.ACodeSparksThread;

public class ThreadTreeLeafNode extends ColoredSelectableTreeNode
{
    private final ACodeSparksThread codeSparksThread;

    ThreadTreeLeafNode(ACodeSparksThread codeSparksThread, JBColor color)
    {
        super(codeSparksThread.getDisplayString(), color);
        this.codeSparksThread = codeSparksThread;
    }

    ThreadTreeLeafNode(ACodeSparksThread codeSparksThread)
    {
        this(codeSparksThread, null);
    }

    public ACodeSparksThread getThreadArtifact()
    {
        return codeSparksThread;
    }
}
