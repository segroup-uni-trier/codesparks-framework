package de.unitrier.st.insituprofiling.core.visualization.callee;

import de.unitrier.st.insituprofiling.core.data.ANeighborProfilingArtifact;
import de.unitrier.st.insituprofiling.core.data.AProfilingArtifact;

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
