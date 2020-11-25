package de.unitrier.st.codesparks.core.visualization.thread;

import com.intellij.ui.JBColor;
import de.unitrier.st.codesparks.core.data.AArtifact;
import de.unitrier.st.codesparks.core.data.ACodeSparksThread;
import de.unitrier.st.codesparks.core.data.IMetricIdentifier;
import de.unitrier.st.codesparks.core.visualization.popup.IThreadSelectable;
import de.unitrier.st.codesparks.core.data.CodeSparksThreadCluster;

import java.awt.*;
import java.util.List;
import java.util.Set;

public class ZoomedThreadRadar extends AThreadRadar
{
    private final List<IThreadSelectable> threadSelectables;
    private final IThreadSelectableIndexProvider indexProvider;
    private int hoveredCluster = -1;
    private final IMetricIdentifier metricIdentifier;

    ZoomedThreadRadar(
            final AArtifact artifact
            , final IThreadSelectableIndexProvider indexProvider
            , final List<IThreadSelectable> threadSelectables
            , final IMetricIdentifier metricIdentifier
    )
    {
        setUpVisualizationParameter(154, 50);
        this.indexProvider = indexProvider;
        this.artifact = artifact;
        this.threadSelectables = threadSelectables;
        this.metricIdentifier = metricIdentifier;
    }

    @Override
    protected void paintComponent(Graphics g)
    {
        super.paintComponent(g);
        paintVisualization();
    }

    private void paintVisualization()
    {
        if (artifact == null)
        {
            return;
        }
        List<CodeSparksThreadCluster> codeSparksThreadClusters = artifact.getSortedDefaultThreadArtifactClustering(metricIdentifier);
        int startAngle = 90; //set start angle to 90 for starting at 12 o'clock
        JBColor[] colors = {new JBColor(Color.decode("#5F4E95"), Color.decode("#5F4E95")), new JBColor(Color.decode("#B25283"),
                Color.decode("#B25283")), new JBColor(Color.decode("#3E877F"), Color.decode("#3E877F"))};
        //drawRectangleBackground();

        double threadRationFromRunBefore = 0;
        int markedStartAngle = -1;
        int markedAngle = -1;
        int markedRadius = -1;

        int index = indexProvider.getThreadSelectableIndex();

        final Set<ACodeSparksThread> filteredCodeSparksThreads = threadSelectables.get(index).getFilteredThreadArtifacts();
        final Set<ACodeSparksThread> selectedCodeSparksThreads = threadSelectables.get(index).getSelectedThreadArtifacts();
        final int numberOfSelectedArtifactThreads = selectedCodeSparksThreads.size();

        String completeNumberOfThreadsString = numberOfSelectedArtifactThreads + "";
        int labelWidth;
        labelWidth = 5 + completeNumberOfThreadsString.length() * 13;

        drawOverallCircle();
        drawInnerCircle();
        for (int i = 0; i < codeSparksThreadClusters.size(); i++)
        {
            VisualThreadClusterPropertiesManager propertiesManager = VisualThreadClusterPropertiesManager.getInstance();
            RadialVisualThreadClusterProperties properties =
                    new RadialVisualThreadClusterProperties(codeSparksThreadClusters.get(i), colors[i],
                            artifact.getNumberOfThreads(), metricIdentifier);

            double filteredRuntimeRatio =
                    ThreadVisualizationUtil.calculateFilteredAvgNumericalMetricRatioForZoomVisualization(codeSparksThreadClusters.get(i),
                            selectedCodeSparksThreads, metricIdentifier, false);
            double filteredRuntimeRatioSum =
                    ThreadVisualizationUtil.calculateFilteredSumNumericalMetricRatioForZoomVisualisation(codeSparksThreadClusters.get(i),
                            metricIdentifier, selectedCodeSparksThreads, false);

            final CodeSparksThreadCluster clusterArtifacts = (CodeSparksThreadCluster) codeSparksThreadClusters.get(i).clone();
            clusterArtifacts.removeAll(filteredCodeSparksThreads);
            double filteredThreadRatio = clusterArtifacts.size() / (double) numberOfSelectedArtifactThreads;
            double completeFilteredRuntimeDurationOfCluster = getFilteredMetricSumOfCluster(codeSparksThreadClusters.get(i), metricIdentifier,
                    filteredCodeSparksThreads);

            properties.setNumericalMetricRatio(filteredRuntimeRatio);
            properties.setThreadRatio(filteredThreadRatio);
            properties.setCompleteFilteredNumericalMetricValue(completeFilteredRuntimeDurationOfCluster);
            properties.setNumericalMetricRationSum(filteredRuntimeRatioSum);
            propertiesManager.registerProperties(properties);

            if (i != 0)
            {
                startAngle -= threadRationFromRunBefore * 360;
            }

            int angle = (int) (360 * filteredThreadRatio * -1);
            int radius = getRadius(filteredRuntimeRatio);
            int radiusSum = getSumRadius(filteredRuntimeRatioSum);

            drawArcForSumAndAvg(colors[i], radiusSum, radius, startAngle, angle);
            if (hoveredCluster == codeSparksThreadClusters.get(i).getId())
            {
                markedStartAngle = startAngle;
                markedAngle = angle;
                //markedRadius = radius;
                markedRadius = radiusSum;
            }

            g2d.setColor(JBColor.BLACK);

            int startAngleTmp = startAngle - Math.abs(angle);
            if (startAngleTmp < 0)
                startAngleTmp = 360 - Math.abs(startAngleTmp);

            properties.setArcStartAngle(startAngleTmp);
            properties.setArcAngle(Math.abs(angle));
            threadRationFromRunBefore = filteredThreadRatio;
        }

        final int yOffsetForTotalThreadsText = -5;
        final int yOffsetForDifferentClassesText = 18;
        final float fontSize = 16f;

        drawHoverCluster(markedStartAngle, markedAngle, numberOfSelectedArtifactThreads, markedRadius);
        drawNumberOfThreadsLabel(labelWidth, fontSize, numberOfSelectedArtifactThreads, yOffsetForTotalThreadsText);
        drawNumberOfDifferentThreadTypesLabel(labelWidth, fontSize,
                ThreadVisualizationUtil.getNumberOfSelectedThreadTypes(artifact, selectedCodeSparksThreads),
                yOffsetForDifferentClassesText);
    }

    void onHoverCluster(int clusterId)
    {
        hoveredCluster = clusterId;
        repaint();
    }

    void unHoverCluster()
    {
        hoveredCluster = -1;
        repaint();
    }
}
