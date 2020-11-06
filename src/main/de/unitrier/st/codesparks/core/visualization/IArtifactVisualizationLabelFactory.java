package de.unitrier.st.codesparks.core.visualization;

import de.unitrier.st.codesparks.core.data.AProfilingArtifact;
import org.jetbrains.annotations.NotNull;

import javax.swing.JLabel;

public interface IArtifactVisualizationLabelFactory
{
    JLabel createArtifactLabel(@NotNull AProfilingArtifact artifact);
}
