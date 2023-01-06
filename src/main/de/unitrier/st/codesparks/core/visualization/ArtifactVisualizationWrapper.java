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

import javax.swing.*;
import java.util.Arrays;
import java.util.Comparator;
import java.util.Set;

public class ArtifactVisualizationWrapper extends AArtifactVisualization
{
    public ArtifactVisualizationWrapper(final AArtifact artifact, final AArtifactVisualizationLabelFactory... factories)
    {
        super(artifact);

        setLayout(new BottomFlowLayout());
        setOpaque(false);

        if (factories == null || factories.length == 0)
        {
            return;
        }

        int width = 0;
        int height = 0;

        Arrays.sort(factories, Comparator.comparingInt(AArtifactVisualizationLabelFactory::getSequence));

        final Class<? extends AArtifact> artifactClass = artifact.getClass();

        for (final AArtifactVisualizationLabelFactory factory : factories)
        {
            final Set<Class<? extends AArtifact>> artifactClasses = factory.getArtifactClasses();
            if (artifactClasses != null)
            {
                if (!artifactClasses.isEmpty() && !artifactClasses.contains(artifactClass))
                {
                    continue;
                }
            }

            final JLabel artifactComponent = factory.createArtifactLabel(artifact);
            final Icon icon = artifactComponent.getIcon();
            if (icon == null)
            {
                final int componentWidth = artifactComponent.getWidth();
                width += componentWidth;
                final int componentHeight = artifactComponent.getHeight();
                height += componentHeight;
            } else
            {
                final int iconWidth = icon.getIconWidth();
                width += iconWidth;
                final int iconHeight = icon.getIconHeight();
                height = Math.max(height, iconHeight);
            }
            this.add(artifactComponent);
        }

        setSize(width, height);
    }
}
