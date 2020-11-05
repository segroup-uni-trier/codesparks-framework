package de.unitrier.st.insituprofiling.core.visualization.popup;

import com.intellij.ui.JBColor;
import com.intellij.util.ui.ThreeStateCheckBox;

import javax.swing.tree.DefaultMutableTreeNode;

import static com.intellij.util.ui.ThreeStateCheckBox.State.NOT_SELECTED;
import static com.intellij.util.ui.ThreeStateCheckBox.State.SELECTED;

public class ColoredSelectableTreeNode extends DefaultMutableTreeNode
{
    private JBColor color;
    //    private boolean isSelected;
    private ThreeStateCheckBox.State state;
    private ColoredSelectableTreeNode parent;

    public ColoredSelectableTreeNode(Object userObject, JBColor color)
    {
        super(userObject);
        this.color = color;
        this.state = SELECTED;
    }

    public ColoredSelectableTreeNode(JBColor color)
    {
        this(null, color);
    }

    public void setParent(ColoredSelectableTreeNode parent)
    {
        this.parent = parent;
    }

    @Override
    public ColoredSelectableTreeNode getParent()
    {
        return parent;
    }

    public JBColor getColor()
    {
        return color;
    }

    public boolean isSelected()
    {
        return state == SELECTED;
    }

    public ThreeStateCheckBox.State getState()
    {
        return state;
    }

    public void toggleSelected()
    {
        ThreeStateCheckBox.State s;
        if (state == SELECTED)
        {
            s = NOT_SELECTED;
        } else
        {
            s = SELECTED;
        }
        //boolean s = !this.isSelected;
        setState(s, true);
    }

    public void setState(ThreeStateCheckBox.State state, boolean applyRecursive)
    {
        //this.isSelected = b;
        this.state = state;

        if (isLeaf())
        {
            return;
        }
        if (!applyRecursive) return;
        for (int i = 0; i < getChildCount(); i++)
        {
            final ThreadTreeLeafNode childAt = (ThreadTreeLeafNode) getChildAt(i);
            childAt.setState(state, applyRecursive);
        }
    }

    public void setState(ThreeStateCheckBox.State state)
    {
        setState(state, false);
    }

    public void setColor(JBColor color)
    {
        this.color = color;
        for (int i = 0; i < getChildCount(); i++)
        {
            final ThreadTreeLeafNode childAt = (ThreadTreeLeafNode) getChildAt(i);
            childAt.setColor(color);
        }
    }
}
