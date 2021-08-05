/*
 * Copyright (c), Oliver Moseler, 2021
 */
package de.unitrier.st.codesparks.core.data;

import com.intellij.pom.Navigatable;
import com.intellij.psi.PsiElement;
import de.unitrier.st.codesparks.core.CoreUtil;
import de.unitrier.st.codesparks.core.IThreadArtifactFilterable;
import de.unitrier.st.codesparks.core.logging.CodeSparksLogger;

import java.io.Serializable;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Collectors;

public abstract class AArtifact implements IDisplayable, IPsiNavigable, IThreadArtifactFilterable, Serializable
{
    /*
     * Final fields
     */

    protected final String name;

    public String getName()
    {
        return name;
    }

    public String getShortName() { return name; }

    protected final String identifier;

    public String getIdentifier()
    {
        return identifier;
    }

    private final Class<? extends AThreadArtifact> threadArtifactClass;

    private final Lazy<Map<String, AThreadArtifact>> threadMap;

    private final Lazy<Map<Integer, List<ANeighborArtifact>>> predecessors;

    private final Lazy<Map<Integer, List<ANeighborArtifact>>> successors;

    private final Lazy<Map<IMetricIdentifier, Object>> metrics;

    /*
     * Non final fields
     */

    protected int lineNumber;

    public int getLineNumber() {return lineNumber;}

    protected String fileName;

    public String getFileName() {return fileName;}

    /*
     * Constructors
     */

    public AArtifact(final String identifier, final String name)
    {
        this(identifier, name, null);
    }

    public AArtifact(final String identifier, final String name, final Class<? extends AThreadArtifact> threadArtifactClass)
    {
        this.identifier = identifier == null ? "" : identifier;
        this.name = name == null ? "" : name;
        this.threadArtifactClass = threadArtifactClass;
        this.metrics = new Lazy<>((Supplier<Map<IMetricIdentifier, Object>> & Serializable) () -> new HashMap<>(8));
        this.threadMap = new Lazy<>((Supplier<Map<String, AThreadArtifact>> & Serializable) () -> new HashMap<>(8));
        this.predecessors = new Lazy<>((Supplier<Map<Integer, List<ANeighborArtifact>>> & Serializable) () -> new HashMap<>(8));
        this.successors = new Lazy<>((Supplier<Map<Integer, List<ANeighborArtifact>>> & Serializable) () -> new HashMap<>(8));
    }

    /*
     * Psi
     */

    private PsiElement visPsiElement;

    private final Object visPsiElementLock = new SerializableLockObject();

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
    public String getDisplayString(final AMetricIdentifier metricIdentifier, final int maxLen)
    {
        return CoreUtil.reduceToLength(getDisplayString(metricIdentifier), maxLen);
    }

    @Override
    public String getDisplayString(final AMetricIdentifier metricIdentifier)
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

    private final Object metricsLock = new SerializableLockObject();

    public Object getMetricValue(final AMetricIdentifier metricIdentifier)
    {
        if (metricIdentifier == null)
        {
            return null;
        }
        Object value;
        synchronized (metricsLock)
        {
            value = metrics.getOrCompute().get(metricIdentifier);
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
    public final Object getOrCreateMetricValue(final AMetricIdentifier metricIdentifier, final Constructor<?> constructor, final Object... initArgs)
    {
        if (metricIdentifier == null)
        {
            return null;
        }
        Object metricValue;
        synchronized (metricsLock)
        {
            metricValue = metrics.getOrCompute().get(metricIdentifier);
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
        return metricValue;
    }

    public void setMetricValue(final AMetricIdentifier metricIdentifier, final Object value)
    {
        if (metricIdentifier == null || value == null)
        {
            return;
        }
        synchronized (metricsLock)
        {
            metrics.getOrCompute().put(metricIdentifier, value);
        }
    }

    public void increaseNumericalMetricValue(final AMetricIdentifier metricIdentifier, final double toIncrease)
    {
        if (metricIdentifier == null || !metricIdentifier.isNumerical())
        {
            return;
        }
        synchronized (metricsLock)
        {
            final Map<IMetricIdentifier, Object> map = metrics.getOrCompute();
            Double val = (Double) map.get(metricIdentifier);
            if (val == null || val.isNaN())
            {
                val = 0d;
            }
            val += toIncrease;
            map.put(metricIdentifier, val);
        }
    }

    public void decreaseNumericalMetricValue(final AMetricIdentifier metricIdentifier, final double toDecrease)
    {
        increaseNumericalMetricValue(metricIdentifier, (-1) * toDecrease);
    }

    public double getNumericalMetricValue(final AMetricIdentifier metricIdentifier)
    {
        if (metricIdentifier == null || !metricIdentifier.isNumerical())
        {
            return 0D;
        }
        Double val;
        synchronized (metricsLock)
        {
            val = (Double) metrics.getOrCompute().get(metricIdentifier);
        }
        return Objects.requireNonNullElse(val, 0D);
    }

    public double setNumericMetricValueInRelationTo(final AMetricIdentifier metricIdentifier, double rel)
    {
        if (metricIdentifier == null || !metricIdentifier.isNumerical())
        {
            return Double.NaN;
        }
        if (rel == 0)
        {
            return Double.NaN;
        }
        rel = Math.abs(rel);
        Double val;
        synchronized (metricsLock)
        {
            val = (Double) metrics.getOrCompute().get(metricIdentifier);
            if (val == null || val.isNaN())
            {
                return Double.NaN;
            }
            val = val / rel;
            metrics.getOrCompute().put(metricIdentifier, val);
        }
        return val;
    }

    public void setNumericalMetricValue(final AMetricIdentifier metricIdentifier, final double value)
    {
        if (metricIdentifier == null || !metricIdentifier.isNumerical())
        {
            return;
        }
        synchronized (metricsLock)
        {
            metrics.getOrCompute().put(metricIdentifier, value);
        }
    }

    /*
     * Threads
     */

    public boolean hasThreads()
    {
        return !getThreadArtifacts().isEmpty();
    }

    public boolean hasThreadsWithNumericMetricValue(final AMetricIdentifier metricIdentifier)
    {
        return !getThreadArtifactsWithNumericMetricValue(metricIdentifier).isEmpty();
    }

    private final Object threadMapLock = new SerializableLockObject();

    public Collection<AThreadArtifact> getThreadArtifacts()
    {
        synchronized (threadMapLock)
        {
            return threadMap.getOrCompute().values();
        }
    }

    public Collection<AThreadArtifact> getSelectedThreadArtifacts()
    {
        synchronized (threadMapLock)
        {
            return threadMap.getOrCompute().values().stream().filter(AThreadArtifact::isSelected).collect(Collectors.toList());
        }
    }

    public Collection<AThreadArtifact> getThreadArtifactsWithNumericMetricValue(final AMetricIdentifier metricIdentifier)
    {
        synchronized (threadMapLock)
        {
            return threadMap.getOrCompute().values().stream().filter(t -> t.getNumericalMetricValue(metricIdentifier) > 0).collect(Collectors.toList());
        }
    }

    public Collection<AThreadArtifact> getSelectedThreadArtifactsWithNumericMetricValue(final AMetricIdentifier metricIdentifier)
    {
        synchronized (threadMapLock)
        {
            return threadMap.getOrCompute().values().stream()
                    .filter(t -> t.isSelected() && t.getNumericalMetricValue(metricIdentifier) > 0).collect(Collectors.toList());
        }
    }

    public Map<String, List<AThreadArtifact>> getThreadTypeLists()
    {
        // TODO: the function used here is not programming language independent!
        final Collection<AThreadArtifact> threadArtifacts = getThreadArtifacts();
        return getThreadTypeLists(threadArtifacts, s -> {
            int index = s.indexOf(":");
            //noinspection UnnecessaryLocalVariable
            String substring = s.substring(0, index);
            return substring;
        });
    }

    public Map<String, List<AThreadArtifact>> getSelectedThreadTypeLists()
    {
        // TODO: the function used here is not programming language independent!
        final Collection<AThreadArtifact> selectedThreadArtifacts = getSelectedThreadArtifacts();
        return getThreadTypeLists(selectedThreadArtifacts, s -> {
            int index = s.indexOf(":");
            //noinspection UnnecessaryLocalVariable
            String substring = s.substring(0, index);
            return substring;
        });
    }

    public Map<String, List<AThreadArtifact>> getThreadTypeListsOfThreadsWithNumericMetricValue(final AMetricIdentifier metricIdentifier)
    {
        final Collection<AThreadArtifact> threadArtifacts = getThreadArtifactsWithNumericMetricValue(metricIdentifier);
        // TODO: the function used here is not programming language independent!
        return getThreadTypeLists(threadArtifacts, s -> {
            int index = s.indexOf(":");
            //noinspection UnnecessaryLocalVariable
            String substring = s.substring(0, index);
            return substring;
        });
    }

    public Map<String, List<AThreadArtifact>> getThreadTypeListsOfSelectedThreadsWithNumericMetricValue(final AMetricIdentifier metricIdentifier)
    {
        final Collection<AThreadArtifact> threadArtifacts = getSelectedThreadArtifactsWithNumericMetricValue(metricIdentifier);
        // TODO: the function used here is not programming language independent!
        return getThreadTypeLists(threadArtifacts, s -> {
            int index = s.indexOf(":");
            //noinspection UnnecessaryLocalVariable
            String substring = s.substring(0, index);
            return substring;
        });
    }

    private Map<String, List<AThreadArtifact>> getThreadTypeLists(final Collection<AThreadArtifact> threadArtifacts,
                                                                  final Function<String, String> threadIdentifierUsedAsKeyProcessor)
    {
        final Map<String, List<AThreadArtifact>> threadTypeLists = new ConcurrentHashMap<>();
        for (final AThreadArtifact threadArtifact : threadArtifacts)
        {
            final String identifier = threadArtifact.getIdentifier();
            final String processed = threadIdentifierUsedAsKeyProcessor.apply(identifier);
            final List<AThreadArtifact> threadArtifactList = threadTypeLists.getOrDefault(processed, new ArrayList<>());
            threadArtifactList.add(threadArtifact);
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
            return threadMap.getOrCompute().get(identifier);
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
            threadArtifact = threadMap.getOrCompute().get(threadIdentifier);
            if (threadArtifact == null)
            {
                try
                {
                    final Constructor<? extends AThreadArtifact> constructor = threadArtifactClass.getConstructor(String.class);
                    threadArtifact = constructor.newInstance(threadIdentifier);
                    threadMap.getOrCompute().put(threadIdentifier, threadArtifact);
                } catch (NoSuchMethodException | InvocationTargetException | InstantiationException | IllegalAccessException e)
                {
                    e.printStackTrace();
                }
            }
        }
        return threadArtifact;
    }

    public void increaseNumericalMetricValueThread(
            final AMetricIdentifier metricIdentifier
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
        if (threadArtifact != null)
        {
            threadArtifact.increaseNumericalMetricValue(metricIdentifier, toIncrease);
        }
    }

    public int getNumberOfThreads()
    {
        synchronized (threadMapLock)
        {
            return threadMap.getOrCompute().size();
        }
    }

    public int getNumberOfThreadsWithNumericMetricValue(final AMetricIdentifier metricIdentifier)
    {
        synchronized (threadMapLock)
        {
            return (int) threadMap.getOrCompute().values().stream().filter(t -> t.getNumericalMetricValue(metricIdentifier) > 0).count();
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

    private final Map<AThreadArtifactClusteringStrategy, ThreadArtifactClustering> clusterings = new HashMap<>();
    private final Map<AThreadArtifactClusteringStrategy, Boolean> selectedClusteringStrategy = new HashMap<>();

    public ThreadArtifactClustering getSelectedClusteringOrApplyAndSelect(final AThreadArtifactClusteringStrategy clusteringStrategy)
    {
        ThreadArtifactClustering clustering;
        synchronized (clusterings)
        {
            final Optional<AThreadArtifactClusteringStrategy> selectedStrategy =
                    selectedClusteringStrategy.entrySet()
                            .stream()
                            .filter(Map.Entry::getValue) // value is of type boolean and stands for whether this strategy is selected
                            .map(Map.Entry::getKey)
                            .findAny();

            if (selectedStrategy.isPresent())
            {
                final AThreadArtifactClusteringStrategy strategy = selectedStrategy.get();
                clustering = clusterings.get(strategy);


                final Comparator<ThreadArtifactCluster> threadArtifactClusterComparator =
                        ThreadArtifactClusterNumericalMetricSumComparator.getInstance(clusteringStrategy.getMetricIdentifier());
                clustering.sort(threadArtifactClusterComparator);
            } else
            {
                clustering = getClusteringAndSelect(clusteringStrategy);
            }
        }
        return clustering;
    }

    public ThreadArtifactClustering getClusteringAndSelect(final AThreadArtifactClusteringStrategy clusteringStrategy)
    {
        ThreadArtifactClustering clustering;
        synchronized (clusterings)
        {
            clustering = clusterings.get(clusteringStrategy);
            if (clustering == null)
            {
                clustering = clusterThreadArtifacts(clusteringStrategy);
                clusterings.put(clusteringStrategy, clustering);
            }
            selectedClusteringStrategy.entrySet().forEach(entry -> entry.setValue(false));
            selectedClusteringStrategy.put(clusteringStrategy, true);
        }
        return clustering;
    }

    public ThreadArtifactClustering clusterThreadArtifacts(final AThreadArtifactClusteringStrategy clusteringStrategy)
    {
        ThreadArtifactClustering clustering;
        synchronized (clusterings)
        {
            clustering = clusterings.get(clusteringStrategy);
            if (clustering == null)
            {
                final AMetricIdentifier metricIdentifier = clusteringStrategy.getMetricIdentifier();
                final Collection<AThreadArtifact> threadArtifacts =
                        getThreadArtifactsWithNumericMetricValue(metricIdentifier);
                clustering = clusteringStrategy.clusterThreadArtifacts(threadArtifacts);
                final Comparator<ThreadArtifactCluster> threadArtifactClusterComparator =
                        ThreadArtifactClusterNumericalMetricSumComparator.getInstance(metricIdentifier);
                clustering.sort(threadArtifactClusterComparator);
                clusterings.put(clusteringStrategy, clustering);
            }
        }
        return clustering;
    }

//    public ThreadArtifactClustering getConstraintKMeansWithAMaximumOfThreeClustersThreadArtifactClustering(final AMetricIdentifier metricIdentifier)
//    {
//        return lookupClustering(ConstraintKMeansWithAMaximumOfThreeClusters.getInstance(metricIdentifier));
//    }
//
//    public ThreadArtifactClustering getSortedConstraintKMeansWithAMaximumOfThreeClustersThreadArtifactClustering(final AMetricIdentifier metricIdentifier)
//    {
//        final ThreadArtifactClustering constraintKMeansClustering =
//                lookupClustering(ConstraintKMeansWithAMaximumOfThreeClusters.getInstance(metricIdentifier));
//        final Comparator<ThreadArtifactCluster> threadArtifactClusterComparator =
//                ThreadArtifactClusterNumericalMetricSumComparator.getInstance(metricIdentifier);
//        constraintKMeansClustering.sort(threadArtifactClusterComparator);
//        return constraintKMeansClustering;
//    }

//    public void initDefaultThreadArtifactClustering(final AMetricIdentifier metricIdentifier)
//    {
//        final IThreadArtifactClusteringStrategy instance = ConstraintKMeansWithAMaximumOfThreeClusters.getInstance(metricIdentifier);
//        synchronized (clusterings)
//        {
//            ThreadArtifactClustering threadArtifactClusters = clusterings.get(instance);
//            if (threadArtifactClusters == null)
//            {
//                threadArtifactClusters = instance.clusterThreadArtifacts(getThreadArtifacts());
//                clusterings.put(instance, threadArtifactClusters);
//            }
//        }
//    }
    /*
     * Predecessors
     */

    private final Object predecessorsLock = new SerializableLockObject();

    public Map<Integer, List<ANeighborArtifact>> getPredecessors()
    {
        synchronized (predecessorsLock)
        {
            return predecessors.getOrCompute();
        }
    }

    public List<ANeighborArtifact> getPredecessorsList()
    {
        synchronized (predecessorsLock)
        {
            return predecessors.getOrCompute().values()
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
            final AMetricIdentifier metricIdentifier,
            final double neighborMetricValue,
            final String threadIdentifier
    )
    {
        synchronized (predecessorsLock)
        {
            final List<ANeighborArtifact> neighborArtifacts = predecessors.getOrCompute().computeIfAbsent(lineNumber,
                    integer -> new ArrayList<>());
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
        }
    }

    /*
     * Successors
     */

    private final Object successorsLock = new SerializableLockObject();

    public Map<Integer, List<ANeighborArtifact>> getSuccessors()
    {
        synchronized (successorsLock)
        {
            return successors.getOrCompute();
        }
    }

    public void increaseMetricValueSuccessor(
            final String name
            , final String identifier
            , final Class<? extends ANeighborArtifact> neighborArtifactClass
            , final int lineNumber
            , final AMetricIdentifier metricIdentifier
            , final double neighborMetricValue
            , final String threadIdentifier
    )
    {
        synchronized (successorsLock)
        {
            final List<ANeighborArtifact> neighborArtifacts = successors.getOrCompute().computeIfAbsent(lineNumber, integer -> new ArrayList<>());
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
            return successors.getOrCompute().values()
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
//        this.metrics.getOrCompute().clear();
        final Map<IMetricIdentifier, Object> metricsMap = this.metrics.get();
        if (metricsMap != null)
        {
            metricsMap.clear();
        }
//        this.threadMap.getOrCompute().clear();
        final Map<String, AThreadArtifact> threadArtifactMap = this.threadMap.get();
        if (threadArtifactMap != null)
        {
            threadArtifactMap.clear();
        }

        final Map<Integer, List<ANeighborArtifact>> predecessors = this.predecessors.get();
        if (predecessors != null)
        {
            for (final Map.Entry<Integer, List<ANeighborArtifact>> integerListEntry : predecessors.entrySet())
            {
                integerListEntry.getValue().clear();
            }
            predecessors.clear();
        }

        final Map<Integer, List<ANeighborArtifact>> successors = this.successors.get();
        if (successors != null)
        {
            for (final Map.Entry<Integer, List<ANeighborArtifact>> integerListEntry : successors.entrySet())
            {
                integerListEntry.getValue().clear();
            }
            successors.clear();
        }
    }
}
