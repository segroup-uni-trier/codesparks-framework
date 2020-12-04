/*
 * Copyright (C) 2020, Oliver Moseler
 */
package de.unitrier.st.codesparks.core.visualization;

import de.unitrier.st.codesparks.core.IDataVisualizer;
import de.unitrier.st.codesparks.core.data.ACodeSparksArtifact;

import java.util.Arrays;
import java.util.Comparator;
import java.util.Optional;

/*
 * Copyright (c), Oliver Moseler, 2020
 */
public abstract class ADataVisualizer implements IDataVisualizer
{
    protected final IArtifactVisualizer artifactVisualizer;
    protected AArtifactVisualizationLabelFactory<ACodeSparksArtifact>[] artifactFactories;

    ADataVisualizer(IArtifactVisualizer artifactVisualizer, AArtifactVisualizationLabelFactory<ACodeSparksArtifact>... factories)
    {
        this.artifactVisualizer = artifactVisualizer;
        init(factories);
    }

    private void init(AArtifactVisualizationLabelFactory<ACodeSparksArtifact>[] artifactFactories)
    {
        if (artifactFactories == null || artifactFactories.length == 0)
        {
//            this.artifactFactories = new AArtifactVisualizationLabelFactory[]{
//                    new DefaultArtifactVisualizationLabelFactory()
//            };
        } else
        {
            this.artifactFactories = artifactFactories;
        }
    }

    public AArtifactVisualizationLabelFactory<ACodeSparksArtifact> getDefaultArtifactVisualizationLabelFactory()
    {
        if (artifactFactories == null || artifactFactories.length < 1)
        {
            return null;
        }
        Optional<AArtifactVisualizationLabelFactory<ACodeSparksArtifact>> first =
                Arrays.stream(artifactFactories).filter(AVisualizationSequence::isDefault).findFirst();
        if (first.isPresent())
        {
            return first.get();
        }
        Optional<AArtifactVisualizationLabelFactory<ACodeSparksArtifact>> min =
                Arrays.stream(artifactFactories).min(Comparator.comparingInt(AVisualizationSequence::getSequence));
        assert artifactFactories.length > 0; // See condition above!
        return min.orElseGet(() -> artifactFactories[0]);
    }
}
