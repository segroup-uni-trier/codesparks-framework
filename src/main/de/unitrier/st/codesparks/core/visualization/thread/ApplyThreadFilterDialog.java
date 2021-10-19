/*
 * Copyright (c) 2021. Oliver Moseler
 */

package de.unitrier.st.codesparks.core.visualization.thread;

import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.DialogWrapper;
import com.intellij.ui.components.JBLabel;
import com.intellij.ui.components.JBPanel;
import com.intellij.util.ui.components.BorderLayoutPanel;
import org.jetbrains.annotations.NonNls;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;
import java.awt.*;

public class ApplyThreadFilterDialog extends DialogWrapper
{

    private Point popupLocation;

    protected ApplyThreadFilterDialog(final Project project, final Component parent)
    {
        super(parent, true);
//        super(project, parent, false, IdeModalityType.PROJECT);
        //this.myPreferredFocusedComponent = component;
        //this.popupLocation = popupLocation;
        setTitle("Apply Thread Filter");
        init();
        //setLocation(popupLocation);

    }

    @Override
    public @Nullable
    JComponent getPreferredFocusedComponent()
    {
        return super.getPreferredFocusedComponent();
    }

    @Override
    protected @Nullable
    @NonNls
    String getDimensionServiceKey()
    {
        return super.getDimensionServiceKey();
    }

    @Override
    protected @Nullable
    JComponent createCenterPanel()
    {
        JBPanel<BorderLayoutPanel> dialogPanel = new BorderLayoutPanel();

        final JBLabel label = new JBLabel("Test");
        label.setPreferredSize(new Dimension(100, 100));
        dialogPanel.add(label, BorderLayout.CENTER);

        return dialogPanel;
    }
}
