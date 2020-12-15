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
    @SafeVarargs
    ArtifactVisualizationWrapper(AArtifact artifact, AArtifactVisualizationLabelFactory<AArtifact>... factories)
    {
        super(artifact);
        //this.psiElement = artifact.getVisPsiElement();

        setLayout(new BottomFlowLayout());
        setOpaque(false);

        if (factories == null || factories.length == 0)
        {
            return;
        }

//        JBPanel jbPanel = new JBPanel();
//        final int offsetWidth = 10;
//        jbPanel.setMinimumSize(new Dimension(offsetWidth, VisConstants.getLineHeight()));
//        Color backgroundColor = CoreUtil.getSelectedFileEditorBackgroundColor();
//        jbPanel.setBackground(backgroundColor);
//        add(jbPanel);

        int width = 0;
        int height = 0;

        Arrays.sort(factories, Comparator.comparingInt(AArtifactVisualizationLabelFactory::getSequence));

        for (AArtifactVisualizationLabelFactory<AArtifact> factory : factories)
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

//        setSize(width + offsetWidth, height);
        setSize(width, height);
    }
}
