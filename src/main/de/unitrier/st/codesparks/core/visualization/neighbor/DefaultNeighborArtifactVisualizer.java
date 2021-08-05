/*
 * Copyright (c) 2021. Oliver Moseler
 */
package de.unitrier.st.codesparks.core.visualization.neighbor;

import de.unitrier.st.codesparks.core.data.AArtifact;
import de.unitrier.st.codesparks.core.data.ANeighborArtifact;

import java.util.*;
import java.util.stream.Collectors;

public class DefaultNeighborArtifactVisualizer implements INeighborArtifactVisualizer
{
    private static volatile INeighborArtifactVisualizer instance;

    public static INeighborArtifactVisualizer getInstance()
    {
        if (instance == null)
        {
            synchronized (DefaultNeighborArtifactVisualizer.class)
            {
                if (instance == null)
                {
                    instance = new DefaultNeighborArtifactVisualizer();
                }
            }
        }
        return instance;
    }

    private DefaultNeighborArtifactVisualizer() {}

    @Override
    public Collection<ANeighborArtifactVisualization> createNeighborArtifactVisualizations(
            final AArtifact artifact
            , final ANeighborArtifactVisualizationLabelFactory... neighborFactories
    )
    {
        final List<ANeighborArtifactVisualization> neighborArtifactVisualizations = new ArrayList<>();
        if (artifact == null)
        {
            return neighborArtifactVisualizations;
        }

        final Set<Map.Entry<Integer, List<ANeighborArtifact>>> threadFilteredSuccessors = artifact.getSuccessors()
                .entrySet()
                .stream()
                .filter(integerListEntry ->
                        integerListEntry.getValue().stream().anyMatch(aNeighborProfilingArtifact ->
                                aNeighborProfilingArtifact.getThreadArtifacts()
                                        .stream()
                                        .anyMatch(threadArtifact -> !threadArtifact.isFiltered()))).collect(Collectors.toSet());

        for (final Map.Entry<Integer, List<ANeighborArtifact>> entry : threadFilteredSuccessors)
        {
            final List<ANeighborArtifact> threadFilteredNeighborsOfCurrentLine = entry.getValue()
                    .stream()
                    .filter(aNeighborProfilingArtifact -> aNeighborProfilingArtifact.getThreadArtifacts()
                            .stream().anyMatch(threadArtifact -> !threadArtifact.isFiltered())).collect(Collectors.toList());

            final NeighborArtifactVisualizationWrapper neighborArtifactVisualizationWrapper =
                    new NeighborArtifactVisualizationWrapper(artifact, threadFilteredNeighborsOfCurrentLine, neighborFactories);

            neighborArtifactVisualizations.add(neighborArtifactVisualizationWrapper);
        }
        return neighborArtifactVisualizations;
    }
}
