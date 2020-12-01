package de.unitrier.st.codesparks.core.visualization.popup;

import com.intellij.ui.JBColor;
import de.unitrier.st.codesparks.core.data.ACodeSparksThread;
import de.unitrier.st.codesparks.core.CoreUtil;
import de.unitrier.st.codesparks.core.data.IMetricIdentifier;

import java.util.List;

/*
 * Copyright (c), Oliver Moseler, 2020
 */
public class ThreadTreeInnerNode extends ColoredSelectableTreeNode
{
    private final List<ACodeSparksThread> codeSparksThreads;
    private final String name;
    private final IMetricIdentifier metricIdentifier;

    public ThreadTreeInnerNode(String name, List<ACodeSparksThread> codeSparksThreads, final IMetricIdentifier metricIdentifier)
    {
        this(name, codeSparksThreads, metricIdentifier, null);
    }

    public ThreadTreeInnerNode(String name, List<ACodeSparksThread> codeSparksThreads, final IMetricIdentifier metricIdentifier, JBColor color)
    {
        super(color);
        this.name = name;
        this.codeSparksThreads = codeSparksThreads;
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
        int childCount = getChildCount();
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

    public List<ACodeSparksThread> getThreadArtifacts()
    {
        return codeSparksThreads;
    }
}
