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

import de.unitrier.st.codesparks.core.IDataVisualizer;
import de.unitrier.st.codesparks.core.visualization.neighbor.ANeighborArtifactVisualizationLabelFactory;
import de.unitrier.st.codesparks.core.visualization.neighbor.INeighborArtifactVisualizer;

public abstract class ADataVisualizer implements IDataVisualizer
{
    protected final IArtifactVisualizer artifactVisualizer;
    protected final AArtifactVisualizationLabelFactory[] artifactLabelFactories;
    protected final INeighborArtifactVisualizer neighborArtifactVisualizer;
    protected final ANeighborArtifactVisualizationLabelFactory[] neighborLabelFactories;

    ADataVisualizer(
            final IArtifactVisualizer artifactVisualizer
            , final INeighborArtifactVisualizer neighborArtifactVisualizer
            , final AArtifactVisualizationLabelFactory[] artifactLabelFactories
            , final ANeighborArtifactVisualizationLabelFactory[] neighborLabelFactories
    )
    {
        this.artifactVisualizer = artifactVisualizer;
        this.neighborArtifactVisualizer = neighborArtifactVisualizer;
        this.artifactLabelFactories = artifactLabelFactories;
        this.neighborLabelFactories = neighborLabelFactories;
    }
}
