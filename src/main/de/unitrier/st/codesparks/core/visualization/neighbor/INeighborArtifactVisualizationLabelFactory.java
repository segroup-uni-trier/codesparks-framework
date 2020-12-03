package de.unitrier.st.codesparks.core.visualization.neighbor;

import de.unitrier.st.codesparks.core.data.ANeighborArtifact;
import de.unitrier.st.codesparks.core.data.ACodeSparksArtifact;

import javax.swing.*;
import java.util.List;

/*
 * Copyright (c), Oliver Moseler, 2020
 */
public interface INeighborArtifactVisualizationLabelFactory
{
    JLabel createArtifactCalleeLabel(
            ACodeSparksArtifact artifact
            , List<ANeighborArtifact> threadFilteredNeighborArtifactsOfLine
    );
}
