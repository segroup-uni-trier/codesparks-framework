/*
 *  Copyright 2023 Oliver Moseler
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */
package de.unitrier.st.codesparks.core.settings;

import com.intellij.ui.components.JBCheckBox;
import com.intellij.ui.components.JBPanel;
import com.intellij.util.ui.components.BorderLayoutPanel;

import javax.swing.*;
import javax.swing.border.TitledBorder;
import java.awt.*;

public class CheckBoxComponentWrapper extends AConfigurableComponentWrapper
{
    private final String borderTitle;
    private final String checkBoxText;

    protected CheckBoxComponentWrapper(String borderTitle, String checkBoxText, boolean selected)
    {
        this.borderTitle = borderTitle;
        this.checkBoxText = checkBoxText;
        if (this.checkBox != null)
        {
            this.checkBox.setText(checkBoxText);
            this.checkBox.setSelected(selected);
            this.formerCheckBoxValue = selected;
        }
        if (titledBorder != null)
        {
            titledBorder.setTitle(borderTitle);
        }
    }

    public boolean getCheckBoxValue()
    {
        return checkBox.isSelected();
    }

    private boolean formerCheckBoxValue;

    public boolean getFormerCheckBoxValue()
    {
        return formerCheckBoxValue;
    }

    public void setFormerCheckBoxValue(boolean value)
    {
        formerCheckBoxValue = value;
    }

    private TitledBorder titledBorder;
    private JBCheckBox checkBox;

    @Override
    protected void setupUI()
    {
        rootPanel = new JBPanel<>();
        rootPanel.setLayout(new BorderLayout(0, 0));

        JBPanel<BorderLayoutPanel> wrapper = new JBPanel<>();
        wrapper.setLayout(new BoxLayout(wrapper, BoxLayout.Y_AXIS));


        JBPanel<BorderLayoutPanel> enablePanel = new JBPanel<>();
        enablePanel.setLayout(new BorderLayout(0, 0));
        titledBorder = BorderFactory.createTitledBorder(BorderFactory.createEtchedBorder(), borderTitle);
        enablePanel.setBorder(titledBorder);

        JBPanel<BorderLayoutPanel> innerEnablePanel = new JBPanel<>();
        innerEnablePanel.setLayout(new BoxLayout(innerEnablePanel, BoxLayout.X_AXIS));

        checkBox = new JBCheckBox(checkBoxText);

        enablePanel.setMaximumSize(new Dimension(Integer.MAX_VALUE, 60));

        innerEnablePanel.add(checkBox);

        enablePanel.add(innerEnablePanel, BorderLayout.CENTER);

        wrapper.add(enablePanel);

        rootPanel.add(wrapper, BorderLayout.CENTER);
    }
}
