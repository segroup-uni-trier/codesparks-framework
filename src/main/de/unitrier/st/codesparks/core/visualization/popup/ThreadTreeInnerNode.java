package de.unitrier.st.codesparks.core.visualization.popup;

import com.intellij.ui.JBColor;
import de.unitrier.st.codesparks.core.data.ACodeSparksThread;
import de.unitrier.st.codesparks.core.CoreUtil;

import java.util.List;

public class ThreadTreeInnerNode extends ColoredSelectableTreeNode
{
    private final List<ACodeSparksThread> codeSparksThreads;
    private final String name;
    private final String metricIdentifier;

    public ThreadTreeInnerNode(String name, List<ACodeSparksThread> codeSparksThreads, final String metricIdentifier)
    {
        this(name, codeSparksThreads, metricIdentifier, null);
    }

    public ThreadTreeInnerNode(String name, List<ACodeSparksThread> codeSparksThreads, final String metricIdentifier, JBColor color)
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
