package de.unitrier.st.codesparks.core.visualization.popup;

import com.intellij.ui.JBColor;
import de.unitrier.st.codesparks.core.data.ThreadArtifact;
import de.unitrier.st.codesparks.core.CoreUtil;

import java.util.List;

public class ThreadTreeInnerNode extends ColoredSelectableTreeNode
{
    private final List<ThreadArtifact> threadArtifacts;
    private final String name;

    public ThreadTreeInnerNode(String name, List<ThreadArtifact> threadArtifacts)
    {
        this(name, threadArtifacts, null);
    }

    public ThreadTreeInnerNode(String name, List<ThreadArtifact> threadArtifacts, JBColor color)
    {
        super(color);
        this.name = name;
        this.threadArtifacts = threadArtifacts;
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
                metricValueSum += childAt.getThreadArtifact().getMetricValue();
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

    public List<ThreadArtifact> getThreadArtifacts()
    {
        return threadArtifacts;
    }
}
