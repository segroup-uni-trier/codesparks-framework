package de.unitrier.st.codesparks.core;

import de.unitrier.st.codesparks.core.data.ICodeSparksThreadFilter;

/*
 * Copyright (c), Oliver Moseler, 2020
 */
public interface ICodeSparksThreadFilterable
{
    void applyThreadFilter(ICodeSparksThreadFilter threadFilter);
}
