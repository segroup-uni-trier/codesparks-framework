package de.unitrier.st.codesparks.core.visualization;

import de.unitrier.st.codesparks.core.data.AArtifact;

import javax.swing.*;

/*
 * Copyright (c), Oliver Moseler, 2020
 */
public interface IArtifactVisualizationLabelFactory
{
    JLabel createArtifactLabel(final AArtifact artifact);
}