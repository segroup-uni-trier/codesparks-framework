package de.unitrier.st.codesparks.core.visualization.popup;

import com.intellij.ui.JBColor;
import de.unitrier.st.codesparks.core.data.CodeSparksThread;

public class ThreadTreeLeafNode extends ColoredSelectableTreeNode
{
    private final CodeSparksThread codeSparksThread;

    ThreadTreeLeafNode(CodeSparksThread codeSparksThread, JBColor color)
    {
        super(codeSparksThread.getDisplayString(), color);
        this.codeSparksThread = codeSparksThread;
    }

    ThreadTreeLeafNode(CodeSparksThread codeSparksThread)
    {
        this(codeSparksThread, null);
    }

    public CodeSparksThread getThreadArtifact()
    {
        return codeSparksThread;
    }
}
