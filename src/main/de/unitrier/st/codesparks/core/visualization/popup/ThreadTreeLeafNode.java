package de.unitrier.st.codesparks.core.visualization.popup;

import com.intellij.ui.JBColor;
import de.unitrier.st.codesparks.core.data.ACodeSparksThread;
import de.unitrier.st.codesparks.core.data.IMetricIdentifier;

/*
 * Copyright (c), Oliver Moseler, 2020
 */
public class ThreadTreeLeafNode extends ColoredSelectableTreeNode
{
    private final ACodeSparksThread codeSparksThread;

    ThreadTreeLeafNode(final ACodeSparksThread codeSparksThread, final IMetricIdentifier metricIdentifier, final JBColor color)
    {
        super(codeSparksThread.getDisplayString(metricIdentifier), color);
        this.codeSparksThread = codeSparksThread;
    }

    ThreadTreeLeafNode(final ACodeSparksThread codeSparksThread, final IMetricIdentifier metricIdentifier)
    {
        this(codeSparksThread, metricIdentifier, null);
    }

    public ACodeSparksThread getThreadArtifact()
    {
        return codeSparksThread;
    }
}
