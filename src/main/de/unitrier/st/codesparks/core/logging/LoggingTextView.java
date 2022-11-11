/*
 * Copyright (c) 2022. Oliver Moseler
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
