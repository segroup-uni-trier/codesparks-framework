/*
 * Copyright (c) 2021. Oliver Moseler
 */
package de.unitrier.st.codesparks.core.visualization.popup;

import com.intellij.ui.JBColor;
import de.unitrier.st.codesparks.core.data.AThreadArtifact;
import de.unitrier.st.codesparks.core.CoreUtil;
import de.unitrier.st.codesparks.core.data.AMetricIdentifier;

import java.util.List;

public class ThreadTreeInnerNode extends ColoredSelectableTreeNode
{
    private final List<AThreadArtifact> threadArtifacts;
    private final String name;
    private final AMetricIdentifier metricIdentifier;

    public ThreadTreeInnerNode(final String name, final List<AThreadArtifact> threadArtifacts, final AMetricIdentifier metricIdentifier)
    {
        this(name, threadArtifacts, metricIdentifier, null);
    }

    public ThreadTreeInnerNode(String name, List<AThreadArtifact> threadArtifacts, final AMetricIdentifier metricIdentifier, JBColor color)
    {
        super(color);
        this.name = name;
        this.threadArtifacts = threadArtifacts;
        this.metricIdentifier = metricIdentifier;
    }

    public String getFullDisplayString()
    {
        return getPercentageString() + " " + name + " " + getSelectedChildrenString();
    }

    private String getPercentageString()
    {
        double metricValueSum = 0d;
        int childCount = getChildCount();
        for (int i = 0; i < childCount; i++)
        {
            ThreadTreeLeafNode childAt = (ThreadTreeLeafNode) getChildAt(i);
            if (childAt.isSelected())
            {
                metricValueSum += childAt.getThreadArtifact().getNumericalMetricValue(metricIdentifier);
            }
        }
        return CoreUtil.formatPercentageWithLeadingWhitespace(metricValueSum);
    }

    private String getSelectedChildrenString()
    {
        final int childCount = getChildCount();
        int selectedCount = 0;
        for (int i = 0; i < childCount; i++)
        {
            ColoredSelectableTreeNode childAt = (ColoredSelectableTreeNode) getChildAt(i);
            if (childAt.isSelected())
            {
                selectedCount++;
            }
        }
        return "(" + selectedCount + "/" + childCount + ")";
    }

    public List<AThreadArtifact> getThreadArtifacts()
    {
        return threadArtifacts;
    }
}
