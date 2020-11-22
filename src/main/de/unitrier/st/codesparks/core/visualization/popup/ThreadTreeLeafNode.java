package de.unitrier.st.codesparks.core.visualization.popup;

import com.intellij.ui.JBColor;
import de.unitrier.st.codesparks.core.data.ACodeSparksThread;

public class ThreadTreeLeafNode extends ColoredSelectableTreeNode
{
    private final ACodeSparksThread codeSparksThread;

    ThreadTreeLeafNode(final ACodeSparksThread codeSparksThread, final String metricIdentifier, final JBColor color)
    {
        super(codeSparksThread.getDisplayString(metricIdentifier), color);
        this.codeSparksThread = codeSparksThread;
    }

    ThreadTreeLeafNode(final ACodeSparksThread codeSparksThread, final String metricIdentifier)
    {
        this(codeSparksThread, metricIdentifier, null);
    }

    public ACodeSparksThread getThreadArtifact()
    {
        return codeSparksThread;
    }
}
