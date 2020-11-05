package de.unitrier.st.insituprofiling.core.visualization.popup;

import com.intellij.ui.components.JBTextArea;

import javax.swing.*;
import java.awt.*;

public class MetricListCellRenderer implements ListCellRenderer<JBTextArea>
{
    @Override
    public Component getListCellRendererComponent(JList<? extends JBTextArea> list, JBTextArea value, int index, boolean isSelected,
                                                  boolean cellHasFocus)
    {
        return value;
    }
}
