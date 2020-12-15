/*
 * Copyright (C) 2020, Oliver Moseler
 */
package de.unitrier.st.codesparks.core.visualization;

import de.unitrier.st.codesparks.core.IDataVisualizer;

import java.util.Arrays;
import java.util.Comparator;
import java.util.Optional;

/*
 * Copyright (c), Oliver Moseler, 2020
 */
public abstract class ADataVisualizer implements IDataVisualizer
{
    protected final IArtifactVisualizer artifactVisualizer;
    protected AArtifactVisualizationLabelFactory[] artifactLabelFactories;

    ADataVisualizer(IArtifactVisualizer artifactVisualizer, AArtifactVisualizationLabelFactory... artifactLabelFactories)
    {
        this.artifactVisualizer = artifactVisualizer;
        this.artifactLabelFactories = artifactLabelFactories;
    }

    public AArtifactVisualizationLabelFactory getDefaultArtifactVisualizationLabelFactory()
    {
        if (artifactLabelFactories == null || artifactLabelFactories.length < 1)
        {
            return null;
        }
        Optional<AArtifactVisualizationLabelFactory> first =
                Arrays.stream(artifactLabelFactories).filter(AVisualizationSequence::isDefault).findFirst();
        if (first.isPresent())
        {
            return first.get();
        }
        Optional<AArtifactVisualizationLabelFactory> min =
                Arrays.stream(artifactLabelFactories).min(Comparator.comparingInt(AVisualizationSequence::getSequence));
        assert artifactLabelFactories.length > 0; // See condition above!
        return min.orElseGet(() -> artifactLabelFactories[0]);
    }
}
