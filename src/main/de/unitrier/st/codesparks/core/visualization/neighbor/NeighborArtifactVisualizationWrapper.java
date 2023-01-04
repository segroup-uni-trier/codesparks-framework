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
package de.unitrier.st.codesparks.core.visualization.neighbor;

import de.unitrier.st.codesparks.core.data.AArtifact;
import de.unitrier.st.codesparks.core.data.ANeighborArtifact;
import de.unitrier.st.codesparks.core.visualization.BottomFlowLayout;

import javax.swing.*;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;

class NeighborArtifactVisualizationWrapper extends ANeighborArtifactVisualization
{
    NeighborArtifactVisualizationWrapper(
            final AArtifact artifact
            , final List<ANeighborArtifact> neighborArtifactsOfLine
            , final ANeighborArtifactVisualizationLabelFactory... neighborFactories
    )
    {
        super(artifact);
        assert neighborArtifactsOfLine.size() > 0;

        psiElement = neighborArtifactsOfLine.get(0).getVisPsiElement();

        this.setLayout(new BottomFlowLayout());

        if (neighborFactories == null || neighborFactories.length == 0)
        {
            return;
        }

        int width = 0;
        int height = 0;

        Arrays.sort(neighborFactories, Comparator.comparingInt(ANeighborArtifactVisualizationLabelFactory::getSequence));

        for (ANeighborArtifactVisualizationLabelFactory factory : neighborFactories)
        {
            JLabel neighborLabel = factory.createNeighborArtifactLabel(artifact, neighborArtifactsOfLine);
            if (neighborLabel == null)
            {
                continue;
            }
            final Icon icon = neighborLabel.getIcon();
            if (icon != null)
            {
                int iconWidth = icon.getIconWidth();
                width += iconWidth;
                int iconHeight = icon.getIconHeight();
                height = Math.max(height, iconHeight);
            } else
            {
                int labelWidth = neighborLabel.getWidth();
                width += labelWidth;
                int labelHeight = neighborLabel.getHeight();
                height = Math.max(height, labelHeight);
            }

            this.add(neighborLabel);
        }

        setSize(width, height);
    }
}
