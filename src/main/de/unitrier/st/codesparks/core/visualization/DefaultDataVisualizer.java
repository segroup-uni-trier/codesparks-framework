/*
 * Copyright (C) 2020, Oliver Moseler
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

/*
 * Copyright (c), Oliver Moseler, 2020
 */
public class DefaultDataVisualizer extends ADataVisualizer
{
    private final INeighborArtifactVisualizer neighborArtifactVisualizer;
    private final ANeighborArtifactVisualizationLabelFactory[] neighborFactories;

    public DefaultDataVisualizer()
    {
        this(DefaultArtifactVisualizer.getInstance(), DefaultNeighborArtifactVisualizer.getInstance(), null, null);
    }

    @SuppressWarnings("unused")
    public DefaultDataVisualizer(
            final AArtifactVisualizationLabelFactory[] artifactFactories
    )
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

    @SuppressWarnings("WeakerAccess")
    public DefaultDataVisualizer(
            final IArtifactVisualizer artifactVisualizer
            , final INeighborArtifactVisualizer neighborArtifactVisualizer
            , final AArtifactVisualizationLabelFactory[] artifactFactories
            , final ANeighborArtifactVisualizationLabelFactory[] neighborFactories
    )
    {
        super(artifactVisualizer, artifactFactories);
        this.neighborArtifactVisualizer = neighborArtifactVisualizer;
        this.neighborFactories = neighborFactories;
    }

    @Override
    public Collection<EditorCoverLayerItem> createVisualizations(
            final Project project
            , final Collection<AArtifact> matchedArtifacts
    )
    {
        // TODO: possible parallelization applicable?
        final Collection<EditorCoverLayerItem> overlayElements = new ArrayList<>();

        for (AArtifact artifact : matchedArtifacts)
        {
            ApplicationManager.getApplication().runReadAction(() -> {

                AArtifactVisualization artifactVisualization = artifactVisualizer.createArtifactVisualization(artifact,
                        artifactLabelFactories);

                PsiElement psiElement = artifact.getVisPsiElement();
                EditorCoverLayerItem layerItem = new EditorCoverLayerItem(psiElement, artifactVisualization);

                overlayElements.add(layerItem);

                Collection<ANeighborArtifactVisualization> calleeVisualizations =
                        neighborArtifactVisualizer.createArtifactCalleeVisualizations(artifact, neighborFactories);

                for (ANeighborArtifactVisualization calleeVisualization : calleeVisualizations)
                {
                    EditorCoverLayerItem item = new EditorCoverLayerItem(calleeVisualization.getPsiElement(), calleeVisualization);
                    overlayElements.add(item);
                }
            });
        }
        return overlayElements;
    }
}
