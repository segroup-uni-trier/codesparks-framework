package de.unitrier.st.insituprofiling.core.settings;

import com.intellij.ui.components.JBPanel;
import com.intellij.util.ui.components.BorderLayoutPanel;

import javax.swing.*;

public abstract class AConfigurableComponentWrapper
{
    protected AConfigurableComponentWrapper()
    {
        setupUI();
    }

    protected JBPanel<BorderLayoutPanel> rootPanel;

    public JPanel getRootPanel()
    {
        return rootPanel;
    }

    protected abstract void setupUI();
}
