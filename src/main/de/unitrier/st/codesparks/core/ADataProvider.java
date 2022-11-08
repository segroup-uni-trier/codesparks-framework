/*
 * Copyright (c) 2022. Oliver Moseler
 */
package de.unitrier.st.codesparks.core;

import de.unitrier.st.codesparks.core.data.IArtifactPool;

public abstract class ADataProvider implements IDataProvider
{
    private final IDataCollector profilingDataCollector;
    private final IDataProcessor profilingDataProcessor;

    protected ADataProvider(final IDataCollector profilingDataCollector, final IDataProcessor profilingDataProcessor)
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

    @Override
    public void postProcess(final IArtifactPool artifactPool)
    {
        profilingDataProcessor.postProcess(artifactPool);
    }
}
