/*
 * Copyright (C) 2020, Oliver Moseler
 */
package de.unitrier.st.insituprofiling.core.visualization;

import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.project.Project;
import com.intellij.psi.PsiElement;
import de.unitrier.st.insituprofiling.core.data.AProfilingArtifact;
import de.unitrier.st.insituprofiling.core.editorcoverlayer.EditorCoverLayerItem;
import de.unitrier.st.insituprofiling.core.visualization.callee.*;

import java.util.ArrayList;
import java.util.Collection;

public class DefaultProfilingDataVisualizer extends AProfilingDataVisualizer//implements IProfilingDataVisualizer
{
    private final IArtifactCalleeVisualizer artifactCalleeVisualizer;
    private AArtifactCalleeVisualizationLabelFactory[] calleeFactories;

    public DefaultProfilingDataVisualizer()
    {
        this(DefaultArtifactVisualizer.getInstance(), DefaultArtifactCalleeVisualizer.getInstance(), null, null);
    }

    @SuppressWarnings("unused")
    public DefaultProfilingDataVisualizer(AArtifactVisualizationLabelFactory[] artifactFactories)
    {
        this(DefaultArtifactVisualizer.getInstance(), DefaultArtifactCalleeVisualizer.getInstance(), artifactFactories, null);
    }

    public DefaultProfilingDataVisualizer(AArtifactVisualizationLabelFactory[] artifactFactories,
                                          AArtifactCalleeVisualizationLabelFactory[] calleeFactories)
    {
        this(DefaultArtifactVisualizer.getInstance(), DefaultArtifactCalleeVisualizer.getInstance(), artifactFactories, calleeFactories);
    }

    @SuppressWarnings("WeakerAccess")
    public DefaultProfilingDataVisualizer(IArtifactVisualizer artifactVisualizer,
                                          IArtifactCalleeVisualizer artifactCalleeVisualizer,
                                          AArtifactVisualizationLabelFactory[] artifactFactories,
                                          AArtifactCalleeVisualizationLabelFactory[] calleeFactories)
    {
        //this.artifactVisualizer = artifactVisualizer;
        super(artifactVisualizer, artifactFactories);
        this.artifactCalleeVisualizer = artifactCalleeVisualizer;
        init(calleeFactories);
    }

    private void init(AArtifactCalleeVisualizationLabelFactory[] calleeFactories)
    {
        if (calleeFactories == null || calleeFactories.length == 0)
        {
//            this.calleeFactories = new AArtifactCalleeVisualizationLabelFactory[]{
//                    new DefaultArtifactCalleeVisualizationLabelFactory()
//            };
        } else
        {
            this.calleeFactories = calleeFactories;
        }
    }

    @Override
    public Collection<EditorCoverLayerItem> createVisualizations(
            final Project project
            , final Collection<AProfilingArtifact> matchedArtifacts
    )
    {
        // TODO: possible parallelization applicable?
        final Collection<EditorCoverLayerItem> overlayElements = new ArrayList<>();

        for (AProfilingArtifact artifact : matchedArtifacts)
        {
            ApplicationManager.getApplication().runReadAction(() -> {

                AArtifactVisualization artifactVisualization = artifactVisualizer.createArtifactVisualization(artifact,
                        artifactFactories);

                PsiElement psiElement = artifact.getVisPsiElement();
                EditorCoverLayerItem layerItem = new EditorCoverLayerItem(psiElement, artifactVisualization);

                overlayElements.add(layerItem);

                Collection<AArtifactCalleeVisualization> calleeVisualizations =
                        artifactCalleeVisualizer.createArtifactCalleeVisualizations(artifact, calleeFactories);

                for (AArtifactCalleeVisualization calleeVisualization : calleeVisualizations)
                {
                    EditorCoverLayerItem item = new EditorCoverLayerItem(calleeVisualization.getPsiElement(), calleeVisualization);
                    overlayElements.add(item);
                }
            });
        }
        return overlayElements;
    }
}
