package de.unitrier.st.codesparks.core.logging;

import javax.swing.JPanel;

/*
 * Copyright (c), Oliver Moseler, 2020
 */
public interface ITextView
{
    void addText(final String str);

    JPanel getRootPanel();

    void clear();
}
