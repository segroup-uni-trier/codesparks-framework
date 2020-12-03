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
public abstract class ASourceCodeArtifact extends AArtifact implements IPsiNavigable, IThreadArtifactFilterable
{
    private final Map<String, AThreadArtifact> threadMap;
    private final Class<? extends AThreadArtifact> threadClass;

    ASourceCodeArtifact(final String name, final String identifier)
    {
        this(name, identifier, DefaultThreadArtifact.class);
    }

    ASourceCodeArtifact(final String name, final String identifier, final Class<? extends AThreadArtifact> threadClass)
    {
        super(name, identifier);
        this.threadClass = threadClass;
        threadMap = new HashMap<>();
    }

    int lineNumber;

    public int getLineNumber() { return lineNumber; }

    String fileName;

    public String getFileName() { return fileName; }

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

    public void setVisPsiElement(PsiElement visPsiElement)
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
