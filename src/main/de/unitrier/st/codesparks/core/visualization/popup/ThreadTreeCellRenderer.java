package de.unitrier.st.codesparks.core.visualization.popup;

import com.intellij.ui.JBColor;
import com.intellij.ui.components.JBTextArea;
import com.intellij.util.ui.ThreeStateCheckBox;

import javax.swing.*;
import javax.swing.tree.TreeCellRenderer;
import java.awt.*;

/*
 * Copyright (c), Oliver Moseler, 2020
 */
public class ThreadTreeCellRenderer implements TreeCellRenderer
{
    @Override
    public Component getTreeCellRendererComponent(JTree tree, Object value, boolean selected, boolean expanded, boolean leaf, int row,
                                                  boolean hasFocus)
    {
        ThreeStateCheckBox checkBox = new ThreeStateCheckBox(value.toString());
        if (value instanceof ColoredSelectableTreeNode)
        {
            ColoredSelectableTreeNode treeNode = (ColoredSelectableTreeNode) value;
            boolean isSelected = treeNode.isSelected();
            if (isSelected)
            {
                checkBox.setForeground(treeNode.getColor());
            } else
            {
                checkBox.setForeground(JBColor.GRAY);
            }
            checkBox.setState(treeNode.getState());
        }
        checkBox.setFont(new JBTextArea().getFont());
        return checkBox;
    }
}
