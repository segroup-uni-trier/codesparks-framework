package de.unitrier.st.codesparks.core.visualization.neighbor;

import de.unitrier.st.codesparks.core.data.ANeighborArtifact;
import de.unitrier.st.codesparks.core.data.AArtifact;

import javax.swing.*;
import java.util.List;

public interface INeighborArtifactVisualizationLabelFactory
{
    JLabel createArtifactCalleeLabel(
            AArtifact artifact
            , List<ANeighborArtifact> threadFilteredNeighborArtifactsOfLine
    );
}
