/*
 * Copyright (c) 2021. Oliver Moseler
 */
package de.unitrier.st.codesparks.core.visualization.popup;

import com.intellij.ui.JBColor;
import de.unitrier.st.codesparks.core.data.AThreadArtifact;
import de.unitrier.st.codesparks.core.data.AMetricIdentifier;

public class ThreadTreeLeafNode extends ColoredSelectableTreeNode
{
    private final AThreadArtifact threadArtifact;

    ThreadTreeLeafNode(final AThreadArtifact threadArtifact, final AMetricIdentifier metricIdentifier, final JBColor color)
    {
        super(threadArtifact.getDisplayString(metricIdentifier), color);
        this.threadArtifact = threadArtifact;
    }

    ThreadTreeLeafNode(final AThreadArtifact threadArtifact, final AMetricIdentifier metricIdentifier)
    {
        this(threadArtifact, metricIdentifier, null);
    }

    public AThreadArtifact getThreadArtifact()
    {
        return threadArtifact;
    }
}
