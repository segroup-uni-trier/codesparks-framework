/*
 * Copyright (c) 2021. Oliver Moseler
 */
package de.unitrier.st.codesparks.core.visualization.popup;

import com.intellij.ui.components.JBTextArea;

import javax.swing.*;
import java.awt.*;

public class MetricListCellRenderer implements ListCellRenderer<JBTextArea>
{
    @Override
    public Component getListCellRendererComponent(
            final JList<? extends JBTextArea> list
            , final JBTextArea value
            , final int index, final boolean isSelected
            , final boolean cellHasFocus
    )
    {
        return value;
    }
}
