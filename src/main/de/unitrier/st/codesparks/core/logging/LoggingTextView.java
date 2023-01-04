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
package de.unitrier.st.codesparks.core.logging;

import com.intellij.openapi.application.ApplicationManager;
import com.intellij.ui.components.JBScrollPane;

import javax.swing.*;
import javax.swing.text.Document;
import java.awt.*;

public class LoggingTextView implements ITextView
{
    private final JPanel rootPanel;
    private final JTextArea textArea;

    LoggingTextView()
    {
        rootPanel = new JPanel();
        rootPanel.setLayout(new BorderLayout(0, 0));
        JBScrollPane scrollPane = new JBScrollPane();
        rootPanel.add(scrollPane, BorderLayout.CENTER);
        textArea = new JTextArea();
        textArea.setEditable(false);
        textArea.setLineWrap(true);
        scrollPane.setViewportView(textArea);
    }

    @Override
    public void addText(final String str)
    {
        if (textArea == null || str == null || str.isEmpty())
        {
            return;
        }
        ApplicationManager.getApplication().invokeLater(() -> {
            textArea.append("\n" + str);
            final Document document = textArea.getDocument();
            final int length = document.getLength();
            textArea.setCaretPosition(length);
        });
    }

    @Override
    public JPanel getRootPanel()
    {
        return rootPanel;
    }

    @Override
    public void clear()
    {
        if (textArea == null)
        {
            return;
        }
        textArea.setText("");
    }

}
