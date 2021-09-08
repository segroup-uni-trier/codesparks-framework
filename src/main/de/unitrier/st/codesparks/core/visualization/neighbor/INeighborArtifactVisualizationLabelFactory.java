/*
 * Copyright (c) 2021. Oliver Moseler
 */
package de.unitrier.st.codesparks.core.visualization.neighbor;

import de.unitrier.st.codesparks.core.data.AArtifact;
import de.unitrier.st.codesparks.core.data.ANeighborArtifact;

import javax.swing.*;
import java.util.List;

public interface INeighborArtifactVisualizationLabelFactory
{
    JLabel createNeighborArtifactLabel(
            final AArtifact artifact
            , final List<ANeighborArtifact> threadFilteredNeighborArtifactsOfLine
    );
}
