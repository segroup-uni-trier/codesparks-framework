package de.unitrier.st.codesparks.core.logging;

import javax.swing.JPanel;

public interface ITextView
{
    void addText(final String str);

    JPanel getRootPanel();

    void clear();
}
