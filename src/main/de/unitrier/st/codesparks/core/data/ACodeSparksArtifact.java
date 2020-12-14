package de.unitrier.st.codesparks.core.data;

import de.unitrier.st.codesparks.core.IThreadArtifactFilterable;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.*;
import java.util.stream.Collectors;

/*
 * Copyright (c), Oliver Moseler, 2020
 */
public abstract class ACodeSparksArtifact extends AArtifact implements IThreadArtifactFilterable
{
    private final Map<Integer, List<ANeighborArtifact>> predecessors;
    private final Map<Integer, List<ANeighborArtifact>> successors;

    public ACodeSparksArtifact(final String name, final String identifier)
    {
        this(name, identifier, DefaultThreadArtifact.class);
    }

    public ACodeSparksArtifact(final String name, final String identifier, final Class<? extends AThreadArtifact> threadArtifactClass)
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
            final int lineNumber,
            final IMetricIdentifier metricIdentifier,
            final double neighborMetricValue,
            final String threadIdentifier
    )
    {
        synchronized (predecessors)
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
            final String name
            , final String identifier
            , final Class<? extends ANeighborArtifact> neighborArtifactClass
            , final int lineNumber
            , final IMetricIdentifier metricIdentifier
            , final double neighborMetricValue
            , final String threadIdentifier
    )
    {
        synchronized (successors)
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
//            neighbor = new NeighborProfilingArtifact(name, identifier, line);
            neighbor = constructor.newInstance(name, identifier, lineNumber);
            neighborList.add(neighbor);
            return neighbor;
        } catch (NoSuchMethodException | IllegalAccessException | InstantiationException | InvocationTargetException e)
        {
            e.printStackTrace();
        }
        return null;
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
