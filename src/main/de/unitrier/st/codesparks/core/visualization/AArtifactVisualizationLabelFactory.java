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