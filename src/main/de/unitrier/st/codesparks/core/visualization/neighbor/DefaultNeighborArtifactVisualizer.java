/*
 * Copyright (C) 2020, Oliver Moseler
 */
package de.unitrier.st.codesparks.core.visualization.neighbor;

import de.unitrier.st.codesparks.core.data.AArtifact;
import de.unitrier.st.codesparks.core.data.ANeighborArtifact;

import java.util.*;
import java.util.stream.Collectors;

/*
 * Copyright (c), Oliver Moseler, 2020
 */
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

    private DefaultNeighborArtifactVisualizer() { }

    @Override
    public Collection<ANeighborArtifactVisualization> createNeighborArtifactVisualizations(
            final AArtifact artifact
            , final ANeighborArtifactVisualizationLabelFactory... neighborFactories
    )
    {
        assert artifact != null;

        List<ANeighborArtifactVisualization> calleeVisualizations = new ArrayList<>();

        //int lineHeight = VisConstants.getLineHeight();

        Set<Map.Entry<Integer, List<ANeighborArtifact>>> threadFilteredSuccessors = artifact.getSuccessors()
                .entrySet()
                .stream()
                .filter(integerListEntry ->
                        integerListEntry.getValue().stream().anyMatch(aNeighborProfilingArtifact ->
                                aNeighborProfilingArtifact.getThreadArtifacts()
                                        .stream()
                                        .anyMatch(threadArtifact -> !threadArtifact.isFiltered()))).collect(Collectors.toSet());

        for (Map.Entry<Integer, List<ANeighborArtifact>> entry : threadFilteredSuccessors)
        {
            List<ANeighborArtifact> threadFilteredCalleesOfCurrentLine = entry.getValue()
                    .stream()
                    .filter(aNeighborProfilingArtifact -> aNeighborProfilingArtifact.getThreadArtifacts()
                            .stream().anyMatch(threadArtifact -> !threadArtifact.isFiltered())).collect(Collectors.toList());

            NeighborArtifactVisualizationWrapper neighborArtifactVisualizationWrapper =
                    new NeighborArtifactVisualizationWrapper(artifact, threadFilteredCalleesOfCurrentLine, neighborFactories);

            calleeVisualizations.add(neighborArtifactVisualizationWrapper);
        }
        return calleeVisualizations;
    }
}
