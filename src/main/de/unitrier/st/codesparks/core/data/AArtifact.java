package de.unitrier.st.codesparks.core.data;

import com.intellij.pom.Navigatable;
import com.intellij.psi.PsiElement;
import de.unitrier.st.codesparks.core.CoreUtil;
import de.unitrier.st.codesparks.core.IThreadArtifactFilterable;
import de.unitrier.st.codesparks.core.logging.CodeSparksLogger;

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

    private final Class<? extends AThreadArtifact> threadArtifactClass;

    private final Map<Integer, List<ANeighborArtifact>> predecessors;

    private final Map<Integer, List<ANeighborArtifact>> successors;

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

    public AArtifact(final String name, final String identifier)
    {
        this(name, identifier, null);
    }

    public AArtifact(final String name, final String identifier, final Class<? extends AThreadArtifact> threadArtifactClass)
    {
        this.name = name == null ? "" : name;
        this.identifier = identifier == null ? "" : identifier;
        this.metrics = new HashMap<>(4);
        this.threadArtifactClass = threadArtifactClass;
        this.threadMap = new HashMap<>(32);
        this.predecessors = new HashMap<>(8);
        this.successors = new HashMap<>(8);
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

    @Override
    public void navigate()
    {
        final PsiElement visPsiElement = getVisPsiElement();
        if (visPsiElement == null)
        {
            return;
        }
        final PsiElement navigationElement = visPsiElement.getNavigationElement();
        if (navigationElement instanceof Navigatable)
        {
            ((Navigatable) navigationElement).navigate(true);
        }
    }

    /*
     * Display strings
     */

    @Override
    public String getDisplayString(final IMetricIdentifier metricIdentifier, final int maxLen)
    {
        return CoreUtil.reduceToLength(getDisplayString(metricIdentifier), maxLen);
    }

    @Override
    public String getDisplayString(final IMetricIdentifier metricIdentifier)
    {
        String metricValueString;
        if (metricIdentifier.isNumerical())
        {
            final double value = getNumericalMetricValue(metricIdentifier);
            if (metricIdentifier.isRelative())
            {
                metricValueString = CoreUtil.formatPercentage(value);
            } else
            {
                metricValueString = Double.toString(value);
            }
        } else
        {
            metricValueString = getMetricValue(metricIdentifier).toString();
        }
        return name + " - " + metricIdentifier.getDisplayString() + ": " + metricValueString;
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
        final Collection<AThreadArtifact> threadArtifacts = getThreadArtifacts();
        final Map<String, List<AThreadArtifact>> threadTypeLists = new ConcurrentHashMap<>();
        for (AThreadArtifact codeSparksThread : threadArtifacts)
        {
            final String identifier = codeSparksThread.getIdentifier();
            final String processed = threadIdentifierProcessor.apply(identifier);
            final List<AThreadArtifact> threadArtifactList = threadTypeLists.getOrDefault(processed, new ArrayList<>());
            threadArtifactList.add(codeSparksThread);
            threadTypeLists.put(processed, threadArtifactList);
        }
        return threadTypeLists;
    }

    public AThreadArtifact getThreadArtifact(final String identifier)
    {
        if (identifier == null || "".equals(identifier))
        {
            return null;
        }
        synchronized (threadMapLock)
        {
            return threadMap.get(identifier);
        }
    }

    public AThreadArtifact getOrCreateThreadArtifact(final String threadIdentifier)
    {
        if (threadIdentifier == null || "".equals(threadIdentifier))
        {
            return null;
        }
        AThreadArtifact threadArtifact;
        synchronized (threadMapLock)
        {
            threadArtifact = threadMap.get(threadIdentifier);
            if (threadArtifact == null)
            {
                try
                {
                    final Constructor<? extends AThreadArtifact> constructor = threadArtifactClass.getConstructor(String.class);
                    threadArtifact = constructor.newInstance(threadIdentifier);
                    threadMap.put(threadIdentifier, threadArtifact);
                } catch (NoSuchMethodException | InvocationTargetException | InstantiationException | IllegalAccessException e)
                {
                    e.printStackTrace();
                }
            }
        }
        return threadArtifact;
    }

    public void increaseNumericalMetricValueThread(
            final IMetricIdentifier metricIdentifier
            , final String threadIdentifier
            , final double toIncrease
    )
    {
        if (threadArtifactClass == null)
        {
            CodeSparksLogger.addText("%s: Thread artifact class is not setup! Setting the metric value for a thread not available.", getClass());
            return;
        }
        final AThreadArtifact threadArtifact = getOrCreateThreadArtifact(threadIdentifier);
        threadArtifact.increaseNumericalMetricValue(metricIdentifier, toIncrease);

//            double threadMetricValue = threadArtifact.getNumericalMetricValue(metricIdentifier);
//            assertSecondaryMetricValue(threadMetricValue, "thread");
    }

    public int getNumberOfThreads()
    {
        synchronized (threadMapLock)
        {
            return threadMap.size();
        }
    }

    @Override
    public void applyThreadFilter(final IThreadArtifactFilter threadFilter)
    {
        final Set<String> threadArtifactIdentifiers = getThreadArtifactIdentifiers();
        final Set<String> filteredThreadArtifactIdentifiers = threadFilter.getFilteredThreadIdentifiers();
        filteredThreadArtifactIdentifiers.retainAll(threadArtifactIdentifiers);
        for (String filteredThreadArtifactIdentifier : filteredThreadArtifactIdentifiers)
        {
            final AThreadArtifact threadArtifact = getThreadArtifact(filteredThreadArtifactIdentifier);
            if (threadArtifact != null)
            {
                threadArtifact.setFiltered(true);
            }
        }
        final Set<String> selectedThreadArtifactIdentifiers = threadFilter.getSelectedThreadIdentifiers();
        selectedThreadArtifactIdentifiers.retainAll(threadArtifactIdentifiers);
        for (String selectedThreadArtifactIdentifier : selectedThreadArtifactIdentifiers)
        {
            final AThreadArtifact threadArtifact = getThreadArtifact(selectedThreadArtifactIdentifier);
            if (threadArtifact != null)
            {
                threadArtifact.setFiltered(false);
            }
        }
    }

    public Set<String> getThreadArtifactIdentifiers()
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
    /*
     * Predecessors
     */

    private final Object predecessorsLock = new Object();

    public Map<Integer, List<ANeighborArtifact>> getPredecessors()
    {
        synchronized (predecessorsLock)
        {
            return predecessors;
        }
    }

    public List<ANeighborArtifact> getPredecessorsList()
    {
        synchronized (predecessorsLock)
        {
            return predecessors.values()
                    .stream()
                    .flatMap(Collection::stream)
                    .collect(Collectors.toList());
        }
    }

    public void increaseMetricValuePredecessor(
            final String name,
            final String identifier,
            final Class<? extends ANeighborArtifact> neighborArtifactClass,
            final int lineNumber,
            final IMetricIdentifier metricIdentifier,
            final double neighborMetricValue,
            final String threadIdentifier
    )
    {
        synchronized (predecessorsLock)
        {
            List<ANeighborArtifact> neighborArtifacts = predecessors.computeIfAbsent(lineNumber,
                    integer -> new ArrayList<>());
            ANeighborArtifact neighbor = getOrCreateNeighborByIdentifier(
                    neighborArtifacts
                    , neighborArtifactClass
                    , name
                    , identifier
                    , lineNumber
            );
            assert neighbor != null;
            neighbor.increaseNumericalMetricValue(metricIdentifier, neighborMetricValue);
            neighbor.increaseNumericalMetricValueThread(metricIdentifier, threadIdentifier, neighborMetricValue);
//            assertSecondaryMetricValue(neighbor.getMetricValue(), "predecessor");
        }
    }

    /*
     * Successors
     */

    private final Object successorsLock = new Object();

    public Map<Integer, List<ANeighborArtifact>> getSuccessors()
    {
        synchronized (successorsLock)
        {
            return successors;
        }
    }

    public void increaseMetricValueSuccessor(
            final String name
            , final String identifier
            , final Class<? extends ANeighborArtifact> neighborArtifactClass
            , final int lineNumber
            , final IMetricIdentifier metricIdentifier
            , final double neighborMetricValue
            , final String threadIdentifier
    )
    {
        synchronized (successorsLock)
        {
            final List<ANeighborArtifact> neighborArtifacts = successors.computeIfAbsent(lineNumber, integer -> new ArrayList<>());
            final ANeighborArtifact neighbor = getOrCreateNeighborByIdentifier(
                    neighborArtifacts
                    , neighborArtifactClass
                    , name
                    , identifier
                    , lineNumber
            );
            assert neighbor != null;
            neighbor.increaseNumericalMetricValue(metricIdentifier, neighborMetricValue);
            neighbor.increaseNumericalMetricValueThread(metricIdentifier, threadIdentifier, neighborMetricValue);
//            assertSecondaryMetricValue(neighbor.getMetricValue(), "successor");
        }
    }

    public List<ANeighborArtifact> getSuccessorsList()
    {
        synchronized (successorsLock)
        {
            return successors.values()
                    .stream()
                    .flatMap(Collection::stream)
                    .collect(Collectors.toList());
        }
    }

    private ANeighborArtifact getOrCreateNeighborByIdentifier(
            final List<ANeighborArtifact> neighborList
            , final Class<? extends ANeighborArtifact> neighborArtifactClass
            , final String name
            , final String identifier
            , final int lineNumber
    )
    {
        ANeighborArtifact neighbor;
        for (ANeighborArtifact neighborArtifact : neighborList)
        {
            if (neighborArtifact.getIdentifier().equals(identifier))
            {
                return neighborArtifact;
            }
        }
        try
        {
            Constructor<? extends ANeighborArtifact> constructor = neighborArtifactClass.getConstructor(
                    String.class
                    , String.class
                    , int.class
            );
            neighbor = constructor.newInstance(name, identifier, lineNumber);
            neighborList.add(neighbor);
            return neighbor;
        } catch (NoSuchMethodException | IllegalAccessException | InstantiationException | InvocationTargetException e)
        {
            e.printStackTrace();
        }
        return null;
    }

    public void clear()
    {
        this.metrics.clear();
        this.threadMap.clear();
        for (final Map.Entry<Integer, List<ANeighborArtifact>> integerListEntry : this.predecessors.entrySet())
        {
            integerListEntry.getValue().clear();
        }
        this.predecessors.clear();
        for (final Map.Entry<Integer, List<ANeighborArtifact>> integerListEntry : this.successors.entrySet())
        {
            integerListEntry.getValue().clear();
        }
        this.successors.clear();
    }

    /*
     * Helpers
     */
    // TODO: enable assertion again!

//    @Deprecated
//    void assertSecondaryMetricValue(double secondaryMetricValue, String name)
//    {
//        double epsilon = .0000000000000001;
//        assert secondaryMetricValue - epsilon <= metricValue : "secondary metric value (" + name + ") larger than total metric value (" +
//                secondaryMetricValue + " > " + metricValue + ")";
//    }
}
