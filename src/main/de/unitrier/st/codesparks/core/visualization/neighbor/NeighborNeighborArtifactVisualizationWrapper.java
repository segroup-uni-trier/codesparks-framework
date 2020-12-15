/*
 * Copyright (C) 2020, Oliver Moseler
 */
package de.unitrier.st.codesparks.core.visualization.neighbor;

import de.unitrier.st.codesparks.core.data.AArtifact;
import de.unitrier.st.codesparks.core.data.ANeighborArtifact;
import de.unitrier.st.codesparks.core.visualization.BottomFlowLayout;

import javax.swing.*;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;

/*
 * Copyright (c), Oliver Moseler, 2020
 */
class NeighborNeighborArtifactVisualizationWrapper extends ANeighborArtifactVisualization
{
    NeighborNeighborArtifactVisualizationWrapper(
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
            JLabel artifactCalleeLabel = factory.createArtifactNeighborLabel(artifact, neighborArtifactsOfLine);
            if (artifactCalleeLabel == null)
            {
                continue;
            }
            int iconWidth = artifactCalleeLabel.getIcon().getIconWidth();
            width += iconWidth;
            int iconHeight = artifactCalleeLabel.getIcon().getIconHeight();
            height = Math.max(height, iconHeight);
            this.add(artifactCalleeLabel);
        }

        setSize(width, height);
    }
}
