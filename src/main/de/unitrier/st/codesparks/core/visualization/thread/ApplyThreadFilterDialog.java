/*
 * Copyright (c) 2021. Oliver Moseler
 */

package de.unitrier.st.codesparks.core.visualization.thread;

import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.DialogWrapper;
import com.intellij.ui.components.JBLabel;
import com.intellij.ui.components.JBPanel;
import com.intellij.util.ui.components.BorderLayoutPanel;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;
import java.awt.*;

public class ApplyThreadFilterDialog extends DialogWrapper
{
    protected ApplyThreadFilterDialog(Component component)
    {
        super((Project) null, component, false, IdeModalityType.MODELESS);


        setTitle("Apply Thread Filter");
        init();


    }

    @Override
    protected @Nullable JComponent createCenterPanel()
    {
        JBPanel<BorderLayoutPanel> dialogPanel = new BorderLayoutPanel();

        final JBLabel label = new JBLabel("Test");
        label.setPreferredSize(new Dimension(100, 100));
        dialogPanel.add(label, BorderLayout.CENTER);

        return dialogPanel;
    }
}
