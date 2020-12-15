package de.unitrier.st.codesparks.core.visualization;

import de.unitrier.st.codesparks.core.data.AArtifact;

import javax.swing.*;
import java.util.Arrays;
import java.util.Comparator;

/*
 * Copyright (C) 2020, Oliver Moseler
 */
class ArtifactVisualizationWrapper extends AArtifactVisualization
{
    ArtifactVisualizationWrapper(final AArtifact artifact, final AArtifactVisualizationLabelFactory... factories)
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

        for (AArtifactVisualizationLabelFactory factory : factories)
        {
            JLabel artifactComponent = factory.createArtifactLabel(artifact);
            ArtifactVisualizationLabelFactoryCache.getInstance().addToCache(artifact.getIdentifier(), factory.getClass(),
                    artifactComponent);
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
