package de.unitrier.st.insituprofiling.core.visualization;

import de.unitrier.st.insituprofiling.core.data.AProfilingArtifact;

import javax.swing.*;
import java.util.Arrays;
import java.util.Comparator;

/*
 * Copyright (C) 2020, Oliver Moseler
 */
class ArtifactVisualizationWrapper extends AArtifactVisualization
{
    ArtifactVisualizationWrapper(AProfilingArtifact artifact, AArtifactVisualizationLabelFactory... factories)
    {
        super(artifact);
        //this.psiElement = artifact.getVisPsiElement();

        this.setLayout(new BottomFlowLayout());

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

        for (AArtifactVisualizationLabelFactory factory : factories)
        {
            JLabel artifactComponent = factory.createArtifactLabel(artifact);
            ArtifactVisualizationLabelFactoryCache.getInstance().addToCache(artifact.getIdentifier(), factory.getClass(),
                    artifactComponent);
            int iconWidth = artifactComponent.getIcon().getIconWidth();
            width += iconWidth;
            int iconHeight = artifactComponent.getIcon().getIconHeight();
            height = Math.max(height, iconHeight);
            this.add(artifactComponent);
        }

//        setSize(width + offsetWidth, height);
        setSize(width, height);
    }
}
