/*
 * Copyright (c) 2022. Oliver Moseler
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
            final Set<Class<?>> artifactClasses = factory.getArtifactClasses();
            if (artifactClasses != null)
            {
                if (!artifactClasses.isEmpty() && !artifactClasses.contains(artifactClass))
                {
                    continue;
                }
            }

            final JLabel artifactComponent = factory.createArtifactLabel(artifact);
//            ArtifactVisualizationLabelFactoryCache.getInstance().addToCache(artifact.getIdentifier(), factory.getClass(),
//                    artifactComponent);
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
