/*
 * Copyright (c) 2021. Oliver Moseler
 */
package de.unitrier.st.codesparks.core.visualization;

import de.unitrier.st.codesparks.core.data.AMetricIdentifier;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

public abstract class AArtifactVisualizationLabelFactory extends AVisualizationLabelFactory implements IArtifactVisualizationLabelFactory
{
    protected Set<Class<?>> artifactClasses;

    protected AArtifactVisualizationLabelFactory(final AMetricIdentifier primaryMetricIdentifier)
    {
        super(primaryMetricIdentifier);
        init();
    }

    protected AArtifactVisualizationLabelFactory(final AMetricIdentifier primaryMetricIdentifier, final Class<?>... artifactClasses)
    {
        super(primaryMetricIdentifier);
        init(artifactClasses);
    }

    protected AArtifactVisualizationLabelFactory(final AMetricIdentifier primaryMetricIdentifier, final int sequence)
    {
        super(primaryMetricIdentifier, sequence);
    }

    protected AArtifactVisualizationLabelFactory(final AMetricIdentifier primaryMetricIdentifier, final int sequence, final Class<?>... artifactClasses)
    {
        super(primaryMetricIdentifier, sequence);
        init(artifactClasses);
    }

    protected AArtifactVisualizationLabelFactory(final AMetricIdentifier primaryMetricIdentifier, final int sequence, final int xOffsetLeft)
    {
        super(primaryMetricIdentifier, sequence, xOffsetLeft);
    }

    protected AArtifactVisualizationLabelFactory(final AMetricIdentifier primaryMetricIdentifier, final int sequence, final int xOffsetLeft,
                                                 final Class<?>... artifactClasses)
    {
        super(primaryMetricIdentifier, sequence, xOffsetLeft);
        init(artifactClasses);
    }

    private void init(Class<?>... artifactClasses)
    {
        this.artifactClasses = new HashSet<>();
        this.artifactClasses.addAll(List.of(artifactClasses));
    }

    @Override
    public Set<Class<?>> getArtifactClasses()
    {
        return artifactClasses;
    }
}