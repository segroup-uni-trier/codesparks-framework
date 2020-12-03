package de.unitrier.st.codesparks.core.visualization.popup;

import com.intellij.ui.JBColor;
import de.unitrier.st.codesparks.core.data.AThreadArtifact;
import de.unitrier.st.codesparks.core.data.IMetricIdentifier;

/*
 * Copyright (c), Oliver Moseler, 2020
 */
public class ThreadTreeLeafNode extends ColoredSelectableTreeNode
{
    private final AThreadArtifact codeSparksThread;

    ThreadTreeLeafNode(final AThreadArtifact codeSparksThread, final IMetricIdentifier metricIdentifier, final JBColor color)
    {
        super(codeSparksThread.getDisplayString(metricIdentifier), color);
        this.codeSparksThread = codeSparksThread;
    }

    ThreadTreeLeafNode(final AThreadArtifact codeSparksThread, final IMetricIdentifier metricIdentifier)
    {
        this(codeSparksThread, metricIdentifier, null);
    }

    public AThreadArtifact getThreadArtifact()
    {
        return codeSparksThread;
    }
}
