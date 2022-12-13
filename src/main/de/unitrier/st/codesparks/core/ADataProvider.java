/*
 * Copyright (c) 2022. Oliver Moseler
 */
package de.unitrier.st.codesparks.core;

public abstract class ADataProvider implements IDataProvider
{
    private IDataCollector dataCollector;
    private IDataProcessor dataProcessor;

    public IDataCollector getDataCollector()
    {
        return dataCollector;
    }

    public IDataProcessor getDataProcessor()
    {
        return dataProcessor;
    }

    protected ADataProvider(final IDataCollector dataCollector, final IDataProcessor dataProcessor)
    {
        this.dataCollector = dataCollector;
        this.dataProcessor = dataProcessor;
    }

    protected ADataProvider() { }
}
