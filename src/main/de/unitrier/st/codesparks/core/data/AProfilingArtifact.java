package de.unitrier.st.codesparks.core.data;

import de.unitrier.st.codesparks.core.CoreUtil;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.*;
import java.util.stream.Collectors;

public abstract class AProfilingArtifact extends ABaseProfilingArtifact implements IPsiNavigable
{
    private final Map<Integer, List<ANeighborProfilingArtifact>> predecessors;
    private final Map<Integer, List<ANeighborProfilingArtifact>> successors;

    protected AProfilingArtifact(String name, String identifier)
    {
        super(name, identifier);
        predecessors = new HashMap<>();
        successors = new HashMap<>();
    }

    protected AProfilingArtifact(String name, String identifier, Class<? extends ThreadArtifact> threadArtifactClass)
    {
        super(name, identifier, threadArtifactClass);
        predecessors = new HashMap<>();
        successors = new HashMap<>();
    }

    /*
     * Predecessors
     */

    public Map<Integer, List<ANeighborProfilingArtifact>> getPredecessors()
    {
        synchronized (predecessors)
        {
            return predecessors;
        }
    }

    public List<ANeighborProfilingArtifact> getPredecessorsList()
    {
        synchronized (predecessors)
        {
            return predecessors.values()
                    .stream()
                    .flatMap(Collection::stream)
                    .collect(Collectors.toList());
        }
    }

    public void increaseMetricValuePredecessor(String name,
                                               String identifier,
                                               Class<? extends ANeighborProfilingArtifact> neighborArtifactClass,
                                               int invocationLine,
                                               double neighborMetricValue,
                                               String threadIdentifier)
    {
        synchronized (predecessors)
        {
            List<ANeighborProfilingArtifact> neighborProfilingArtifacts = predecessors.computeIfAbsent(invocationLine,
                    integer -> new ArrayList<>());
            ANeighborProfilingArtifact neighbor = getOrCreateNeighborByIdentifier(neighborProfilingArtifacts, name, identifier,
                    neighborArtifactClass, invocationLine);
            assert neighbor != null;
            neighbor.increaseMetricValue(neighborMetricValue);
            neighbor.increaseMetricValueThread(threadIdentifier, neighborMetricValue);
            assertSecondaryMetricValue(neighbor.getMetricValue(), "predecessor");
        }
    }

    /*
     * Successors
     */

    public Map<Integer, List<ANeighborProfilingArtifact>> getSuccessors()
    {
        synchronized (successors)
        {
            return successors;
        }
    }

    public void increaseMetricValueSuccessor(String name,
                                             String identifier,
                                             Class<? extends ANeighborProfilingArtifact> neighborArtifactClass,
                                             int invocationLine,
                                             double neighborMetricValue,
                                             String threadIdentifier)
    {
        synchronized (successors)
        {
            List<ANeighborProfilingArtifact> neighborProfilingArtifacts = successors.computeIfAbsent(invocationLine,
                    integer -> new ArrayList<>());
            ANeighborProfilingArtifact neighbor = getOrCreateNeighborByIdentifier(neighborProfilingArtifacts, name, identifier,
                    neighborArtifactClass, invocationLine);
            assert neighbor != null;
            neighbor.increaseMetricValue(neighborMetricValue);
            neighbor.increaseMetricValueThread(threadIdentifier, neighborMetricValue);
            assertSecondaryMetricValue(neighbor.getMetricValue(), "successor");
        }
    }

    public List<ANeighborProfilingArtifact> getSuccessorsList()
    {
        synchronized (successors)
        {
            return successors.values()
                    .stream()
                    .flatMap(Collection::stream)
                    .collect(Collectors.toList());
        }
    }

    private ANeighborProfilingArtifact getOrCreateNeighborByIdentifier(List<ANeighborProfilingArtifact> neighborList,
                                                                       String name,
                                                                       String identifier,
                                                                       Class<? extends ANeighborProfilingArtifact> neighborArtifactClass,
                                                                       int line)
    {
        ANeighborProfilingArtifact neighbor;
        for (ANeighborProfilingArtifact neighborProfilingArtifact : neighborList)
        {
            if (neighborProfilingArtifact.getIdentifier().equals(identifier))
            {
                return neighborProfilingArtifact;
            }
        }
        try
        {
            Constructor<? extends ANeighborProfilingArtifact> constructor = neighborArtifactClass.getConstructor(String.class, String.class,
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

    private final Map<IThreadClusteringStrategy, ThreadArtifactClustering> clusterings = new HashMap<>();

    private ThreadArtifactClustering lookupClustering(IThreadClusteringStrategy clusteringStrategy)
    {
        synchronized (clusterings)
        {
            ThreadArtifactClustering threadArtifactClusters = clusterings.get(clusteringStrategy);
            if (threadArtifactClusters == null)
            {
                threadArtifactClusters = clusteringStrategy.clusterThreadArtifacts(getThreadArtifacts());
                clusterings.put(clusteringStrategy, threadArtifactClusters);
            }
            return threadArtifactClusters;
        }
    }

    public ThreadArtifactClustering getThreadArtifactClustering(IThreadClusteringStrategy clusteringStrategy)
    {
        return lookupClustering(clusteringStrategy);
    }

    public ThreadArtifactClustering getDefaultThreadArtifactClustering()
    {
        return lookupClustering(DefaultThreadClusteringStrategy.getInstance());
    }

    public ThreadArtifactClustering getSortedDefaultThreadArtifactClustering()
    {
        ThreadArtifactClustering defaultThreadArtifactClusters = lookupClustering(DefaultThreadClusteringStrategy.getInstance());
        defaultThreadArtifactClusters.sort(ThreadArtifactClusterComparator.getInstance());
        return defaultThreadArtifactClusters;
    }

    public void initDefaultThreadArtifactClustering()
    {
        IThreadClusteringStrategy instance = DefaultThreadClusteringStrategy.getInstance();
        synchronized (clusterings)
        {
            ThreadArtifactClustering threadArtifactClusters = clusterings.get(instance);
            if (threadArtifactClusters == null)
            {
                threadArtifactClusters = instance.clusterThreadArtifacts(getThreadArtifacts());
                clusterings.put(instance, threadArtifactClusters);
            }
        }
    }

    public String getMetricValuesString()
    {
        return identifier + " => " + "TOTAL-METRIC-VALUE = " + CoreUtil.formatPercentage(metricValue) + "; METRIC-VALUE-SELF = " + CoreUtil
                .formatPercentage(metricValueSelf) + " (" + CoreUtil.formatPercentage(metricValueSelf / metricValue)
                + " OF TOTAL-METRIC-VALUE)";
    }

    public abstract String getTitleName();
}
