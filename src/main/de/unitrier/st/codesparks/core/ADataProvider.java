/*
 * Copyright (c) 2022. Oliver Moseler
 */
package de.unitrier.st.codesparks.core;

public abstract class ADataProvider implements IDataProvider
{
    public IDataCollector getDataCollector()
    {
        return this;
    }

    public IDataProcessor getDataProcessor()
    {
        return this;
    }

    protected ADataProvider() { }
}
