package de.unitrier.st.codesparks.core.data;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.*;
import java.util.stream.Collectors;

public abstract class AArtifact extends ABaseArtifact implements IPsiNavigable
{
    private final Map<Integer, List<ANeighborArtifact>> predecessors;
    private final Map<Integer, List<ANeighborArtifact>> successors;

    public AArtifact(final String name, final String identifier)
    {
        super(name, identifier);
        predecessors = new HashMap<>();
        successors = new HashMap<>();
    }

    public AArtifact(final String name, final String identifier, final Class<? extends ACodeSparksThread> threadArtifactClass)
    {
        super(name, identifier, threadArtifactClass);
        predecessors = new HashMap<>();
        successors = new HashMap<>();
    }

    /*
     * Predecessors
     */

    public Map<Integer, List<ANeighborArtifact>> getPredecessors()
    {
        synchronized (predecessors)
        {
            return predecessors;
        }
    }

    public List<ANeighborArtifact> getPredecessorsList()
    {
        synchronized (predecessors)
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
            final int invocationLine,
            final IMetricIdentifier metricIdentifier,
            final double neighborMetricValue,
            final String threadIdentifier
    )
    {
        synchronized (predecessors)
        {
            List<ANeighborArtifact> neighborProfilingArtifacts = predecessors.computeIfAbsent(invocationLine,
                    integer -> new ArrayList<>());
            ANeighborArtifact neighbor = getOrCreateNeighborByIdentifier(neighborProfilingArtifacts, name, identifier,
                    neighborArtifactClass, invocationLine);
            assert neighbor != null;
//            neighbor.increaseMetricValue(neighborMetricValue);
            neighbor.increaseNumericalMetricValue(metricIdentifier, neighborMetricValue);
            neighbor.increaseNumericalMetricValueThread(metricIdentifier, threadIdentifier, neighborMetricValue);
//            assertSecondaryMetricValue(neighbor.getMetricValue(), "predecessor");
        }
    }

    /*
     * Successors
     */

    public Map<Integer, List<ANeighborArtifact>> getSuccessors()
    {
        synchronized (successors)
        {
            return successors;
        }
    }

    public void increaseMetricValueSuccessor(
            final String name,
            final String identifier,
            final Class<? extends ANeighborArtifact> neighborArtifactClass,
            final int invocationLine,
            final IMetricIdentifier metricIdentifier,
            final double neighborMetricValue,
            final String threadIdentifier
    )
    {
        synchronized (successors)
        {
            List<ANeighborArtifact> neighborProfilingArtifacts = successors.computeIfAbsent(invocationLine,
                    integer -> new ArrayList<>());
            ANeighborArtifact neighbor = getOrCreateNeighborByIdentifier(neighborProfilingArtifacts, name, identifier,
                    neighborArtifactClass, invocationLine);
            assert neighbor != null;
//            neighbor.increaseMetricValue(neighborMetricValue);
            neighbor.increaseNumericalMetricValue(metricIdentifier, neighborMetricValue);
            neighbor.increaseNumericalMetricValueThread(metricIdentifier, threadIdentifier, neighborMetricValue);
//            assertSecondaryMetricValue(neighbor.getMetricValue(), "successor");
        }
    }

    public List<ANeighborArtifact> getSuccessorsList()
    {
        synchronized (successors)
        {
            return successors.values()
                    .stream()
                    .flatMap(Collection::stream)
                    .collect(Collectors.toList());
        }
    }

    private ANeighborArtifact getOrCreateNeighborByIdentifier(
            final List<ANeighborArtifact> neighborList
            , final String name
            , final String identifier
            , final Class<? extends ANeighborArtifact> neighborArtifactClass
            , final int line
    )
    {
        ANeighborArtifact neighbor;
        for (ANeighborArtifact neighborProfilingArtifact : neighborList)
        {
            if (neighborProfilingArtifact.getIdentifier().equals(identifier))
            {
                return neighborProfilingArtifact;
            }
        }
        try
        {
            Constructor<? extends ANeighborArtifact> constructor = neighborArtifactClass.getConstructor(String.class, String.class,
                    int.class);
//            neighbor = new NeighborProfilingArtifact(name, identifier, line);
            neighbor = constructor.newInstance(name, identifier, line);
            neighborList.add(neighbor);
            return neighbor;
        } catch (NoSuchMethodException | IllegalAccessException | InstantiationException | InvocationTargetException e)
        {
            e.printStackTrace();
        }
        return null;
    }

    private final Map<ICodeSparksThreadClusteringStrategy, CodeSparksThreadClustering> clusterings = new HashMap<>();

    private CodeSparksThreadClustering lookupClustering(ICodeSparksThreadClusteringStrategy clusteringStrategy)
    {
        synchronized (clusterings)
        {
            CodeSparksThreadClustering threadArtifactClusters = clusterings.get(clusteringStrategy);
            if (threadArtifactClusters == null)
            {
                threadArtifactClusters = clusteringStrategy.clusterCodeSparksThreads(getThreadArtifacts());
                clusterings.put(clusteringStrategy, threadArtifactClusters);
            }
            return threadArtifactClusters;
        }
    }

    public CodeSparksThreadClustering getThreadArtifactClustering(ICodeSparksThreadClusteringStrategy clusteringStrategy)
    {
        return lookupClustering(clusteringStrategy);
    }

    public CodeSparksThreadClustering getDefaultThreadArtifactClustering(final IMetricIdentifier metricIdentifier)
    {
        return lookupClustering(DefaultCodeSparksThreadClusteringStrategy.getInstance(metricIdentifier));
    }

    public CodeSparksThreadClustering getSortedDefaultThreadArtifactClustering(final IMetricIdentifier metricIdentifier)
    {
        CodeSparksThreadClustering defaultThreadArtifactClusters = lookupClustering(DefaultCodeSparksThreadClusteringStrategy.getInstance(metricIdentifier));
        Comparator<CodeSparksThreadCluster> codeSparksThreadClusterComparator = CodeSparksThreadClusterComparator.getInstance(metricIdentifier);
        defaultThreadArtifactClusters.sort(codeSparksThreadClusterComparator);
        return defaultThreadArtifactClusters;
    }

    public void initDefaultThreadArtifactClustering(final IMetricIdentifier metricIdentifier)
    {
        ICodeSparksThreadClusteringStrategy instance = DefaultCodeSparksThreadClusteringStrategy.getInstance(metricIdentifier);
        synchronized (clusterings)
        {
            CodeSparksThreadClustering threadArtifactClusters = clusterings.get(instance);
            if (threadArtifactClusters == null)
            {
                threadArtifactClusters = instance.clusterCodeSparksThreads(getThreadArtifacts());
                clusterings.put(instance, threadArtifactClusters);
            }
        }
    }

//    public String getMetricValuesString()
//    {
//        return identifier + " => " + "TOTAL-METRIC-VALUE = " + CoreUtil.formatPercentage(metricValue) + "; METRIC-VALUE-SELF = " + CoreUtil
//                .formatPercentage(metricValueSelf) + " (" + CoreUtil.formatPercentage(metricValueSelf / metricValue)
//                + " OF TOTAL-METRIC-VALUE)";
//    }

    public String getTitleName()
    {
        return name;
    }
}
