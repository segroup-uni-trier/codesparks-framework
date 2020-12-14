package de.unitrier.st.codesparks.core.data;

import com.intellij.psi.PsiElement;
import de.unitrier.st.codesparks.core.CoreUtil;
import de.unitrier.st.codesparks.core.IThreadArtifactFilterable;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Function;
import java.util.stream.Collectors;

/*
 * Copyright (c), Oliver Moseler, 2020
 */
public abstract class AArtifact implements IDisplayable, IPsiNavigable, IThreadArtifactFilterable
{
    /*
     * Final fields
     */

    protected final String name;

    public String getName()
    {
        return name;
    }

    protected final String identifier;

    public String getIdentifier()
    {
        return identifier;
    }

    private final Map<String, AThreadArtifact> threadMap;

    private final Class<? extends AThreadArtifact> threadClass;

    /*
     * Non final fields
     */

    protected int lineNumber;

    public int getLineNumber() { return lineNumber; }

    protected String fileName;

    public String getFileName() { return fileName; }

    /*
     * Constructors
     */

    AArtifact(final String name, final String identifier)
    {
        this(name, identifier, DefaultThreadArtifact.class);
    }

    AArtifact(final String name, final String identifier, final Class<? extends AThreadArtifact> threadArtifactClass)
    {
        this.name = name == null ? "" : name;
        this.identifier = identifier == null ? "" : identifier;
        this.metrics = new HashMap<>();
        this.threadClass = threadArtifactClass;
        this.threadMap = new HashMap<>();
    }

    /*
     * Psi
     */

    private PsiElement visPsiElement;

    private final Object visPsiElementLock = new Object();

    public PsiElement getVisPsiElement()
    {
        synchronized (visPsiElementLock)
        {
            return visPsiElement;
        }
    }

    public void setVisPsiElement(final PsiElement visPsiElement)
    {
        synchronized (visPsiElementLock)
        {
            this.visPsiElement = visPsiElement;
        }
    }

    /*
     * Display strings
     */

    @Override
    public String getDisplayString(final IMetricIdentifier metricIdentifier, final int maxLen)
    {
        return CoreUtil.reduceToLength(name, maxLen);
    }

    @Override
    public String getDisplayString(final IMetricIdentifier metricIdentifier)
    {
        return name;
    }

    /*
     * Metrics
     */

    private final Map<IMetricIdentifier, Object> metrics;

    private final Object metricsLock = new Object();

    public Collection<Metric> getMetrics()
    {
        Set<Map.Entry<IMetricIdentifier, Object>> entries;
        synchronized (metricsLock)
        {
            entries = metrics.entrySet();
        }
        Collection<Metric> ret = new ArrayList<>(entries.size());

        for (final Map.Entry<IMetricIdentifier, Object> entry : entries)
        {
            final String name = entry.getKey().getDisplayString();
            final Object value = entry.getValue();
            final Metric metric = new Metric(name, value);
            ret.add(metric);
        }

        return ret;
    }

    public Metric getMetric(final IMetricIdentifier metricIdentifier)
    {
        if (metricIdentifier == null)
        {
            return null;
        }
        final String name = metricIdentifier.getDisplayString();
        Metric m = new Metric(name);
        Object value;
        synchronized (metricsLock)
        {
            value = metrics.get(metricIdentifier);
        }
        m.setValue(value);
        return m;
    }

    public Object getMetricValue(final IMetricIdentifier metricIdentifier)
    {
        if (metricIdentifier == null)
        {
            return null;
        }
        Object value;
        synchronized (metricsLock)
        {
            value = metrics.get(metricIdentifier);
        }
        return value;
    }

    /**
     * A thread safe method to get or create a metric value in case it might not have been initialised yet. If the value is non null, no new value will be
     * instantiated.
     *
     * @param metricIdentifier The metric identifier.
     * @return The value (as object) associated with the metric identifier.
     */
    public final Object getOrCreateMetricValue(final IMetricIdentifier metricIdentifier, final Constructor<?> constructor, final Object... initArgs)
    {
        if (metricIdentifier == null)
        {
            return null;
        }
        Object metricValue = metrics.get(metricIdentifier);
        if (metricValue == null)
        {
            synchronized (metricsLock)
            { // Double checked locking!
                metricValue = metrics.get(metricIdentifier);
                if (metricValue == null)
                {
                    try
                    {
                        metricValue = constructor.newInstance(initArgs);
                        setMetricValue(metricIdentifier, metricValue);
                    } catch (InstantiationException | IllegalAccessException | InvocationTargetException e)
                    {
                        e.printStackTrace();
                    }
                }
            }
        }
        return metricValue;
    }

    public void setMetricValue(final IMetricIdentifier metricIdentifier, final Object value)
    {
        if (metricIdentifier == null || value == null)
        {
            return;
        }
        synchronized (metricsLock)
        {
            metrics.put(metricIdentifier, value);
        }
    }

    public void increaseNumericalMetricValue(final IMetricIdentifier metricIdentifier, final double toIncrease)
    {
        if (metricIdentifier == null || !metricIdentifier.isNumerical())
        {
            return;
        }
        synchronized (metricsLock)
        {
            Double val = (Double) metrics.get(metricIdentifier);
            if (val == null)
            {
                val = 0d;
            }
            val += toIncrease;
            metrics.put(metricIdentifier, val);
        }
    }

    public void decreaseNumericalMetricValue(final IMetricIdentifier metricIdentifier, final double toDecrease)
    {
        increaseNumericalMetricValue(metricIdentifier, (-1) * toDecrease);
    }

    public double getNumericalMetricValue(final IMetricIdentifier metricIdentifier)
    {
        if (metricIdentifier == null || !metricIdentifier.isNumerical())
        {
            return Double.NaN;
        }
        Double val;
        synchronized (metricsLock)
        {
            val = (Double) metrics.get(metricIdentifier);
        }
        if (val == null)
        {
            return 0D;
        }
        return val;
    }

    public void setNumericalMetricValue(final IMetricIdentifier metricIdentifier, final double value)
    {
        if (metricIdentifier == null || !metricIdentifier.isNumerical())
        {
            return;
        }
        synchronized (metricsLock)
        {
            metrics.put(metricIdentifier, value);
        }
    }

    /*
     * Threads
     */

    public boolean hasThreads()
    {
        return !getThreadArtifacts().isEmpty();
    }

    private final Object threadMapLock = new Object();

    public Collection<AThreadArtifact> getThreadArtifacts()
    {
        synchronized (threadMapLock)
        {
            return threadMap.values();
        }
    }

    public Map<String, List<AThreadArtifact>> getThreadTypeLists()
    {
        return getThreadTypeLists(s -> {
            int index = s.indexOf(":");
            //noinspection UnnecessaryLocalVariable
            String substring = s.substring(0, index);
            return substring;
        });
    }

    public Map<String, List<AThreadArtifact>> getThreadTypeLists(final Function<String, String> threadIdentifierProcessor)
    {
        Collection<AThreadArtifact> threadArtifacts = getThreadArtifacts();
        Map<String, List<AThreadArtifact>> threadTypeLists = new ConcurrentHashMap<>();
        for (AThreadArtifact codeSparksThread : threadArtifacts)
        {
            String identifier = codeSparksThread.getIdentifier();
            String processed = threadIdentifierProcessor.apply(identifier);
            List<AThreadArtifact> threadArtifactList = threadTypeLists.getOrDefault(processed, new ArrayList<>());
            threadArtifactList.add(codeSparksThread);
            threadTypeLists.put(processed, threadArtifactList);
        }
        return threadTypeLists;
    }

    public AThreadArtifact getThreadArtifact(String identifier)
    {
        synchronized (threadMapLock)
        {
            return threadMap.get(identifier);
        }
    }

    @Deprecated
    public void addThreadArtifact(AThreadArtifact codeSparksThread)
    {
        synchronized (threadMapLock)
        {
            threadMap.put(codeSparksThread.getIdentifier(), codeSparksThread);
        }
    }

    public synchronized void increaseNumericalMetricValueThread(final IMetricIdentifier metricIdentifier, final String threadIdentifier, double toIncrease)
    {
        synchronized (threadMapLock)
        {
            AThreadArtifact codeSparksThread = threadMap.get(threadIdentifier);
            if (codeSparksThread == null)
            {
                try
                {
                    final Constructor<? extends AThreadArtifact> constructor = threadClass.getConstructor(String.class);
                    codeSparksThread = constructor.newInstance(threadIdentifier);
                    threadMap.put(threadIdentifier, codeSparksThread);
                } catch (NoSuchMethodException | InvocationTargetException | InstantiationException | IllegalAccessException e)
                {
                    e.printStackTrace();
                }
            }

            assert codeSparksThread != null;

            codeSparksThread.increaseNumericalMetricValue(metricIdentifier, toIncrease);

            double threadMetricValue = codeSparksThread.getNumericalMetricValue(metricIdentifier);

//            assertSecondaryMetricValue(threadMetricValue, "thread");
        }
    }

    public int getNumberOfThreads()
    {
        synchronized (threadMapLock)
        {
            return threadMap.size();
        }
    }

    @Override
    public void applyThreadFilter(IThreadArtifactFilter threadFilter)
    {
        final Set<String> threadArtifactIdentifiers = getCodeSparksThreadIdentifiers();
        final Set<String> filteredThreadArtifactIdentifiers = threadFilter.getFilteredThreadIdentifiers();
        filteredThreadArtifactIdentifiers.retainAll(threadArtifactIdentifiers);
        for (String filteredThreadArtifactIdentifier : filteredThreadArtifactIdentifiers)
        {
            getThreadArtifact(filteredThreadArtifactIdentifier).setFiltered(true);
        }

        final Set<String> selectedThreadArtifactIdentifiers = threadFilter.getSelectedThreadIdentifiers();
        selectedThreadArtifactIdentifiers.retainAll(threadArtifactIdentifiers);
        for (String selectedThreadArtifactIdentifier : selectedThreadArtifactIdentifiers)
        {
            getThreadArtifact(selectedThreadArtifactIdentifier).setFiltered(false);
        }
    }

    public Set<String> getCodeSparksThreadIdentifiers()
    {
        return getThreadArtifacts().stream().map(AThreadArtifact::getIdentifier).collect(Collectors.toSet());
    }

    /*
     Thread Clustering
     */

    private final Map<IThreadArtifactClusteringStrategy, ThreadArtifactClustering> clusterings = new HashMap<>();

    private ThreadArtifactClustering lookupClustering(IThreadArtifactClusteringStrategy clusteringStrategy)
    {
        synchronized (clusterings)
        {
            ThreadArtifactClustering threadArtifactClusters = clusterings.get(clusteringStrategy);
            if (threadArtifactClusters == null)
            {
                threadArtifactClusters = clusteringStrategy.clusterCodeSparksThreads(getThreadArtifacts());
                clusterings.put(clusteringStrategy, threadArtifactClusters);
            }
            return threadArtifactClusters;
        }
    }

    public ThreadArtifactClustering getThreadArtifactClustering(IThreadArtifactClusteringStrategy clusteringStrategy)
    {
        return lookupClustering(clusteringStrategy);
    }

    public ThreadArtifactClustering getDefaultThreadArtifactClustering(final IMetricIdentifier metricIdentifier)
    {
        return lookupClustering(DefaultThreadArtifactClusteringStrategy.getInstance(metricIdentifier));
    }

    public ThreadArtifactClustering getSortedDefaultThreadArtifactClustering(final IMetricIdentifier metricIdentifier)
    {
        ThreadArtifactClustering defaultThreadArtifactClusters = lookupClustering(DefaultThreadArtifactClusteringStrategy.getInstance(metricIdentifier));
        Comparator<ThreadArtifactCluster> codeSparksThreadClusterComparator = ThreadArtifactClusterComparator.getInstance(metricIdentifier);
        defaultThreadArtifactClusters.sort(codeSparksThreadClusterComparator);
        return defaultThreadArtifactClusters;
    }

    public void initDefaultThreadArtifactClustering(final IMetricIdentifier metricIdentifier)
    {
        IThreadArtifactClusteringStrategy instance = DefaultThreadArtifactClusteringStrategy.getInstance(metricIdentifier);
        synchronized (clusterings)
        {
            ThreadArtifactClustering threadArtifactClusters = clusterings.get(instance);
            if (threadArtifactClusters == null)
            {
                threadArtifactClusters = instance.clusterCodeSparksThreads(getThreadArtifacts());
                clusterings.put(instance, threadArtifactClusters);
            }
        }
    }
}
