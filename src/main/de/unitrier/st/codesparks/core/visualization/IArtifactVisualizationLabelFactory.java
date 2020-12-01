package de.unitrier.st.codesparks.core.visualization;

import de.unitrier.st.codesparks.core.data.AArtifact;
import org.jetbrains.annotations.NotNull;

import javax.swing.JLabel;

/*
 * Copyright (c), Oliver Moseler, 2020
 */
public interface IArtifactVisualizationLabelFactory
{
    JLabel createArtifactLabel(@NotNull final AArtifact artifact);
}