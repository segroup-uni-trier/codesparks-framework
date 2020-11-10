package de.unitrier.st.codesparks.core;

import de.unitrier.st.codesparks.core.data.ICodeSparksThreadFilter;

public interface ICodeSparksThreadFilterable
{
    void applyThreadFilter(ICodeSparksThreadFilter threadFilter);
}
