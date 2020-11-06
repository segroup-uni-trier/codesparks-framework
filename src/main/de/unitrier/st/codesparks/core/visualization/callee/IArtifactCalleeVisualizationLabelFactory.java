package de.unitrier.st.codesparks.core.visualization.callee;

import de.unitrier.st.codesparks.core.data.ANeighborProfilingArtifact;
import de.unitrier.st.codesparks.core.data.AProfilingArtifact;

import javax.swing.*;
import java.awt.*;
import java.util.List;


public interface IArtifactCalleeVisualizationLabelFactory
{
    JLabel createArtifactCalleeLabel(AProfilingArtifact artifact
            , List<ANeighborProfilingArtifact> threadFilteredNeighborArtifactsOfLine
            , double threadFilteredMetricValue
            , Color metricColor
    );
}
