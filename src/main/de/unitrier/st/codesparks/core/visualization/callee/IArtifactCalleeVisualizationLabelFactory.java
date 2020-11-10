package de.unitrier.st.codesparks.core.visualization.callee;

import de.unitrier.st.codesparks.core.data.ANeighborArtifact;
import de.unitrier.st.codesparks.core.data.AArtifact;

import javax.swing.*;
import java.awt.*;
import java.util.List;


public interface IArtifactCalleeVisualizationLabelFactory
{
    JLabel createArtifactCalleeLabel(AArtifact artifact
            , List<ANeighborArtifact> threadFilteredNeighborArtifactsOfLine
            , double threadFilteredMetricValue
            , Color metricColor
    );
}
