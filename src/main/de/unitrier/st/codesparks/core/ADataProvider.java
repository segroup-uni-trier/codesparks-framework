/*
 * Copyright (c) 2021. Oliver Moseler
 */
package de.unitrier.st.codesparks.core;

import de.unitrier.st.codesparks.core.data.IArtifactPool;

public abstract class ADataProvider implements IDataProvider
{
    private final IDataCollector profilingDataCollector;
    private final IDataProcessor profilingDataProcessor;

    protected ADataProvider(IDataCollector profilingDataCollector, IDataProcessor profilingDataProcessor)
    {
        this.profilingDataCollector = profilingDataCollector;
        this.profilingDataProcessor = profilingDataProcessor;
    }

    @Override
    public final boolean collectData()
    {
        return profilingDataCollector.collectData();
    }

    @Override
    public final IArtifactPool processData()
    {
        return profilingDataProcessor.processData();
    }
}
