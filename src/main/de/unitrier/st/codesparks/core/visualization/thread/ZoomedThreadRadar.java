/*
 * Copyright (c), Oliver Moseler, 2021
 */
package de.unitrier.st.codesparks.core.visualization.thread;

import com.intellij.ui.JBColor;
import de.unitrier.st.codesparks.core.data.*;
import de.unitrier.st.codesparks.core.visualization.VisualizationUtil;
import de.unitrier.st.codesparks.core.visualization.popup.IThreadSelectable;

import java.awt.*;
import java.util.List;
import java.util.Set;

public class ZoomedThreadRadar extends AThreadRadar
{
    private final List<IThreadSelectable> threadSelectables;
    private final IThreadSelectableIndexProvider indexProvider;
    private long hoveredCluster = -1;
    private final AMetricIdentifier metricIdentifier;

    ZoomedThreadRadar(
            final AArtifact artifact
            , final IThreadSelectableIndexProvider indexProvider
            , final List<IThreadSelectable> threadSelectables
            , final AMetricIdentifier metricIdentifier
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
        VisualizationUtil.clearAndDrawTransparentBackground(g2d, getWidth(), getHeight());

        final ThreadArtifactClustering clustering =
                artifact.clusterThreadArtifacts(ConstraintKMeansWithAMaximumOfThreeClusters.getInstance(metricIdentifier));

        int startAngle = 90; //set start angle to 90 for starting at 12 o'clock

        double threadRationFromRunBefore = 0;
        int markedStartAngle = -1;
        int markedAngle = -1;
        int markedRadius = -1;

        int index = indexProvider.getThreadSelectableIndex();

        final Set<AThreadArtifact> filteredThreadArtifacts = threadSelectables.get(index).getFilteredThreadArtifacts();
        final Set<AThreadArtifact> selectedThreadArtifacts = threadSelectables.get(index).getSelectedThreadArtifacts();
        final int numberOfSelectedArtifactThreads = selectedThreadArtifacts.size();

        String completeNumberOfThreadsString = numberOfSelectedArtifactThreads + "";
        int labelWidth;
        labelWidth = 5 + completeNumberOfThreadsString.length() * 13;

        drawOuterCircle();
        drawInnerCircle();
        for (int i = 0; i < clustering.size(); i++)
        {
            final VisualThreadClusterPropertiesManager propertiesManager = VisualThreadClusterPropertiesManager.getInstance(clustering);

            final ThreadArtifactCluster cluster = clustering.get(i);

            final JBColor color = ThreadColor.getNextColor(i);

            final RadialVisualThreadClusterProperties properties =
                    new RadialVisualThreadClusterProperties(cluster, color, artifact.getNumberOfThreads());

            final double filteredRuntimeRatio =
                    ThreadVisualizationUtil.calculateFilteredAvgNumericalMetricRatioForZoomVisualization(cluster,
                            selectedThreadArtifacts, metricIdentifier, false);
            final double filteredRuntimeRatioSum =
                    ThreadVisualizationUtil.calculateFilteredSumNumericalMetricRatioForZoomVisualisation(cluster,
                            metricIdentifier, selectedThreadArtifacts, false);

            final ThreadArtifactCluster clusterArtifacts = (ThreadArtifactCluster) cluster.clone();
            clusterArtifacts.removeAll(filteredThreadArtifacts);
            final double filteredThreadRatio = clusterArtifacts.size() / (double) numberOfSelectedArtifactThreads;
            final double completeFilteredRuntimeDurationOfCluster = getFilteredMetricSumOfCluster(cluster, metricIdentifier,
                    filteredThreadArtifacts);

            properties.setAvgMetricValue(filteredRuntimeRatio);
            properties.setNumberOfThreadsInClusterRatio(filteredThreadRatio);
            properties.setCompleteFilteredNumericalMetricValue(completeFilteredRuntimeDurationOfCluster);
            properties.setSumMetricValue(filteredRuntimeRatioSum);
            properties.setColor(color);
            propertiesManager.registerProperties(properties);

            if (i != 0)
            {
                startAngle -= threadRationFromRunBefore * 360;
            }

            final int angle = (int) (360 * filteredThreadRatio * -1);
            final int radius = getRadius(filteredRuntimeRatio);
            final int radiusSum = getSumRadius(filteredRuntimeRatioSum);

            drawArcForSumAndAvg(color, radiusSum, radius, startAngle, angle);
            if (hoveredCluster == cluster.getId())
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
        // Draw number of threads pedestal at the bottom
        drawPedestal(labelWidth, false, fontSize, numberOfSelectedArtifactThreads, yOffsetForTotalThreadsText);
        // Draw number of different thread types pedestal at the top
        final int numberOfSelectedThreadTypesWithNumericMetricValueInSelection =
                ThreadVisualizationUtil.getNumberOfSelectedThreadTypesWithNumericMetricValueInSelection(artifact, metricIdentifier, selectedThreadArtifacts);
        drawPedestal(labelWidth, true, fontSize, numberOfSelectedThreadTypesWithNumericMetricValueInSelection, yOffsetForDifferentClassesText);
    }

    void onHoverCluster(final long clusterId)
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
