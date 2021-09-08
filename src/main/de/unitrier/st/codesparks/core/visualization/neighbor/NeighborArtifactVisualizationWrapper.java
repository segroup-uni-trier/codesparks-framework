/*
 * Copyright (c) 2021. Oliver Moseler
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
