/*
 * Copyright (c) 2021. Oliver Moseler
 */
package de.unitrier.st.codesparks.core.visualization;

import de.unitrier.st.codesparks.core.data.AArtifact;

import javax.swing.*;

public interface IArtifactVisualizationLabelFactory
{
    JLabel createArtifactLabel(final AArtifact artifact);
}