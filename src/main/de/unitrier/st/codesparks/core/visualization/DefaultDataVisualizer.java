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
package de.unitrier.st.codesparks.core.visualization;

import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.project.Project;
import com.intellij.psi.PsiElement;
import de.unitrier.st.codesparks.core.data.AArtifact;
import de.unitrier.st.codesparks.core.editorcoverlayer.EditorCoverLayerItem;
import de.unitrier.st.codesparks.core.visualization.neighbor.ANeighborArtifactVisualization;
import de.unitrier.st.codesparks.core.visualization.neighbor.ANeighborArtifactVisualizationLabelFactory;
import de.unitrier.st.codesparks.core.visualization.neighbor.DefaultNeighborArtifactVisualizer;
import de.unitrier.st.codesparks.core.visualization.neighbor.INeighborArtifactVisualizer;

import java.util.ArrayList;
import java.util.Collection;

public class DefaultDataVisualizer extends ADataVisualizer
{
    public DefaultDataVisualizer()
    {
        this(DefaultArtifactVisualizer.getInstance(), DefaultNeighborArtifactVisualizer.getInstance(), null, null);
    }

    public DefaultDataVisualizer(final AArtifactVisualizationLabelFactory[] artifactFactories)
    {
        this(DefaultArtifactVisualizer.getInstance(), DefaultNeighborArtifactVisualizer.getInstance(), artifactFactories, null);
    }

    public DefaultDataVisualizer(
            final AArtifactVisualizationLabelFactory[] artifactFactories
            , final ANeighborArtifactVisualizationLabelFactory[] neighborFactories
    )
    {
        this(DefaultArtifactVisualizer.getInstance(), DefaultNeighborArtifactVisualizer.getInstance(), artifactFactories, neighborFactories);
    }

    public DefaultDataVisualizer(
            final IArtifactVisualizer artifactVisualizer
            , final INeighborArtifactVisualizer neighborArtifactVisualizer
            , final AArtifactVisualizationLabelFactory[] artifactFactories
            , final ANeighborArtifactVisualizationLabelFactory[] neighborFactories
    )
    {
        super(artifactVisualizer, neighborArtifactVisualizer, artifactFactories, neighborFactories);
    }

    @Override
    public Collection<EditorCoverLayerItem> createVisualizations(
            final Project project,
            final Collection<AArtifact> matchedArtifacts
    )
    {
        // TODO: possible parallelization applicable?
        final Collection<EditorCoverLayerItem> coverLayerItems = new ArrayList<>();

        for (final AArtifact artifact : matchedArtifacts)
        {
            ApplicationManager.getApplication().runReadAction(() -> {

                final AArtifactVisualization artifactVisualization = artifactVisualizer.createArtifactVisualization(
                        artifact,
                        artifactLabelFactories
                );

                final PsiElement psiElement = artifact.getVisPsiElement();
                final EditorCoverLayerItem layerItem = new EditorCoverLayerItem(psiElement, artifactVisualization);

                coverLayerItems.add(layerItem);

                final Collection<ANeighborArtifactVisualization> calleeVisualizations =
                        neighborArtifactVisualizer.createNeighborArtifactVisualizations(artifact, neighborLabelFactories);

                for (final ANeighborArtifactVisualization calleeVisualization : calleeVisualizations)
                {
                    final EditorCoverLayerItem item = new EditorCoverLayerItem(calleeVisualization.getPsiElement(), calleeVisualization);
                    coverLayerItems.add(item);
                }
            });
        }
        return coverLayerItems;
    }
}
