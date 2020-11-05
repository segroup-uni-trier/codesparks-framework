package de.unitrier.st.insituprofiling.core.logging;

import javax.swing.JPanel;

public interface ITextView
{
    void addText(final String str);

    JPanel getRootPanel();

    void clear();
}
