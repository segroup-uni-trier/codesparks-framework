/*
 * Copyright (c) 2021. Oliver Moseler
 */
package de.unitrier.st.codesparks.core.visualization;

import de.unitrier.st.codesparks.core.data.AArtifact;
import de.unitrier.st.codesparks.core.data.AMetricIdentifier;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

public abstract class AArtifactVisualizationLabelFactory extends AVisualizationLabelFactory implements IArtifactVisualizationLabelFactory
{
    protected Set<Class<? extends AArtifact>> artifactClasses;

    protected AArtifactVisualizationLabelFactory(final AMetricIdentifier primaryMetricIdentifier)
    {
        super(primaryMetricIdentifier);
        init();
    }

    @SafeVarargs
    protected AArtifactVisualizationLabelFactory(final AMetricIdentifier primaryMetricIdentifier,
                                                 final Class<? extends AArtifact>... artifactClasses
    )
    {
        super(primaryMetricIdentifier);
        init(artifactClasses);
    }

    protected AArtifactVisualizationLabelFactory(final AMetricIdentifier primaryMetricIdentifier,
                                                 final int sequence
    )
    {
        super(primaryMetricIdentifier, sequence);
        init();
    }

    @SafeVarargs
    protected AArtifactVisualizationLabelFactory(final AMetricIdentifier primaryMetricIdentifier,
                                                 final int sequence,
                                                 final Class<? extends AArtifact>... artifactClasses
    )
    {
        super(primaryMetricIdentifier, sequence);
        init(artifactClasses);
    }

    protected AArtifactVisualizationLabelFactory(final AMetricIdentifier primaryMetricIdentifier,
                                                 final int sequence,
                                                 final int xOffsetLeft
    )
    {
        super(primaryMetricIdentifier, sequence, xOffsetLeft);
        init();
    }

    @SafeVarargs
    protected AArtifactVisualizationLabelFactory(final AMetricIdentifier primaryMetricIdentifier,
                                                 final int sequence,
                                                 final int xOffsetLeft,
                                                 final Class<? extends AArtifact>... artifactClasses
    )
    {
        super(primaryMetricIdentifier, sequence, xOffsetLeft);
        init(artifactClasses);
    }

    @SafeVarargs
    private void init(Class<? extends AArtifact>... artifactClasses)
    {
        this.artifactClasses = new HashSet<>();
        this.artifactClasses.addAll(List.of(artifactClasses));
    }

    @Override
    public Set<Class<? extends AArtifact>> getArtifactClasses()
    {
        return artifactClasses;
    }
}