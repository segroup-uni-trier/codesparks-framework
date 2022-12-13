/*
 * Copyright (c) 2022. Oliver Moseler
 */
package de.unitrier.st.codesparks.core;

import de.unitrier.st.codesparks.core.data.IArtifactPool;

public abstract class ADataProvider implements IDataProvider
{
    private final IDataCollector dataCollector;
    private final IDataProcessor dataProcessor;

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

    @Override
    public final boolean collectData()
    {
        return dataCollector.collectData();
    }

    @Override
    public final IArtifactPool processData()
    {
        return dataProcessor.processData();
    }

    @Override
    public void postProcess(final IArtifactPool artifactPool)
    {
        dataProcessor.postProcess(artifactPool);
    }
}
