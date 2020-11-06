package de.unitrier.st.codesparks.core;

public abstract class AProfilingDataProvider implements IProfilingDataProvider
{
    private final IProfilingDataCollector profilingDataCollector;
    private final IProfilingDataProcessor profilingDataProcessor;

    protected AProfilingDataProvider(IProfilingDataCollector profilingDataCollector, IProfilingDataProcessor profilingDataProcessor)
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
    public final IProfilingResult processData()
    {
        return profilingDataProcessor.processData();
    }
}
