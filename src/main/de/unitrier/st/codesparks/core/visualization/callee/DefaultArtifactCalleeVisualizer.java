/*
 * Copyright (C) 2020, Oliver Moseler
 */
package de.unitrier.st.codesparks.core.visualization.callee;

import de.unitrier.st.codesparks.core.data.ANeighborArtifact;
import de.unitrier.st.codesparks.core.data.AArtifact;

import java.util.*;
import java.util.stream.Collectors;

public class DefaultArtifactCalleeVisualizer implements IArtifactCalleeVisualizer
{
    private static volatile IArtifactCalleeVisualizer instance;

    public static IArtifactCalleeVisualizer getInstance()
    {
        if (instance == null)
        {
            synchronized (DefaultArtifactCalleeVisualizer.class)
            {
                if (instance == null)
                {
                    instance = new DefaultArtifactCalleeVisualizer();
                }
            }
        }
        return instance;
    }

    private DefaultArtifactCalleeVisualizer() { }

    @Override
    public Collection<AArtifactCalleeVisualization> createArtifactCalleeVisualizations(AArtifact artifact,
                                                                                       AArtifactCalleeVisualizationLabelFactory... calleeFactories)
    {
        assert artifact != null;

        List<AArtifactCalleeVisualization> calleeVisualizations = new ArrayList<>();

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

            ArtifactCalleeVisualizationWrapper artifactCalleeVisualizationWrapper =
                    new ArtifactCalleeVisualizationWrapper(artifact, threadFilteredCalleesOfCurrentLine, calleeFactories);

            calleeVisualizations.add(artifactCalleeVisualizationWrapper);
        }
        return calleeVisualizations;
    }
}
