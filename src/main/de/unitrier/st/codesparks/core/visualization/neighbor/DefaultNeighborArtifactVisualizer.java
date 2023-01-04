/*
 *  Copyright 2023 Oliver Moseler
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
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
