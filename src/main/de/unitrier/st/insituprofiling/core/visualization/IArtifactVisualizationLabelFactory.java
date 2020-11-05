package de.unitrier.st.insituprofiling.core.visualization;

import de.unitrier.st.insituprofiling.core.data.AProfilingArtifact;
import org.jetbrains.annotations.NotNull;

import javax.swing.JLabel;

public interface IArtifactVisualizationLabelFactory
{
    JLabel createArtifactLabel(@NotNull AProfilingArtifact artifact);
}
