package de.unitrier.st.codesparks.core.visualization;

import de.unitrier.st.codesparks.core.data.AArtifact;
import org.jetbrains.annotations.NotNull;

import javax.swing.JLabel;

public interface IArtifactVisualizationLabelFactory
{
    JLabel createArtifactLabel(@NotNull AArtifact artifact);
}
