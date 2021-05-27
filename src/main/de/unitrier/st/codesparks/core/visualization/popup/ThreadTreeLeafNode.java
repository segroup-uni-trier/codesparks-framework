/*
 * Copyright (c) 2021. Oliver Moseler
 */
package de.unitrier.st.codesparks.core.visualization.popup;

import com.intellij.ui.JBColor;
import de.unitrier.st.codesparks.core.data.AThreadArtifact;
import de.unitrier.st.codesparks.core.data.AMetricIdentifier;

public class ThreadTreeLeafNode extends ColoredSelectableTreeNode
{
    private final AThreadArtifact codeSparksThread;

    ThreadTreeLeafNode(final AThreadArtifact threadArtifact, final AMetricIdentifier metricIdentifier, final JBColor color)
    {
        super(threadArtifact.getDisplayString(metricIdentifier), color);
        this.codeSparksThread = threadArtifact;
    }

    ThreadTreeLeafNode(final AThreadArtifact codeSparksThread, final AMetricIdentifier metricIdentifier)
    {
        this(codeSparksThread, metricIdentifier, null);
    }

    public AThreadArtifact getThreadArtifact()
    {
        return codeSparksThread;
    }
}
