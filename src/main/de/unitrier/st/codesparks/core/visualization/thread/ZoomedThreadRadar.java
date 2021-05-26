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

        //VisualizationUtil.drawTransparentBackground(g2d, getWidth(), getHeight(), AlphaComposite.CLEAR);

        final ThreadArtifactClustering clustering =
                artifact.clusterThreadArtifacts(ConstraintKMeansWithAMaximumOfThreeClusters.getInstance(metricIdentifier), true);
//                artifact.getSortedConstraintKMeansWithAMaximumOfThreeClustersThreadArtifactClustering(metricIdentifier);
        int startAngle = 90; //set start angle to 90 for starting at 12 o'clock
        final JBColor[] colors = {new JBColor(Color.decode("#5F4E95"), Color.decode("#5F4E95")), new JBColor(Color.decode("#B25283"),
                Color.decode("#B25283")), new JBColor(Color.decode("#3E877F"), Color.decode("#3E877F"))};
        //drawRectangleBackground();

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
            VisualThreadClusterPropertiesManager propertiesManager = VisualThreadClusterPropertiesManager.getInstance(clustering);
            RadialVisualThreadClusterProperties properties =
                    new RadialVisualThreadClusterProperties(clustering.get(i), colors[i],
                            artifact.getNumberOfThreads(), metricIdentifier);

            double filteredRuntimeRatio =
                    ThreadVisualizationUtil.calculateFilteredAvgNumericalMetricRatioForZoomVisualization(clustering.get(i),
                            selectedThreadArtifacts, metricIdentifier, false);
            double filteredRuntimeRatioSum =
                    ThreadVisualizationUtil.calculateFilteredSumNumericalMetricRatioForZoomVisualisation(clustering.get(i),
                            metricIdentifier, selectedThreadArtifacts, false);

            final ThreadArtifactCluster clusterArtifacts = (ThreadArtifactCluster) clustering.get(i).clone();
            clusterArtifacts.removeAll(filteredThreadArtifacts);
            double filteredThreadRatio = clusterArtifacts.size() / (double) numberOfSelectedArtifactThreads;
            double completeFilteredRuntimeDurationOfCluster = getFilteredMetricSumOfCluster(clustering.get(i), metricIdentifier,
                    filteredThreadArtifacts);

            properties.setAvgMetricValue(filteredRuntimeRatio);
            properties.setNumberOfThreadsInClusterRatio(filteredThreadRatio);
            properties.setCompleteFilteredNumericalMetricValue(completeFilteredRuntimeDurationOfCluster);
            properties.setSumMetricValue(filteredRuntimeRatioSum);
            propertiesManager.registerProperties(properties);

            if (i != 0)
            {
                startAngle -= threadRationFromRunBefore * 360;
            }

            int angle = (int) (360 * filteredThreadRatio * -1);
            int radius = getRadius(filteredRuntimeRatio);
            int radiusSum = getSumRadius(filteredRuntimeRatioSum);

            drawArcForSumAndAvg(colors[i], radiusSum, radius, startAngle, angle);
            if (hoveredCluster == clustering.get(i).getId())
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
//        drawNumberOfThreadsLabel(labelWidth, fontSize, numberOfSelectedArtifactThreads, yOffsetForTotalThreadsText);
        // Draw number of threads pedestal at the bottom
        drawPedestal(labelWidth, false, fontSize, numberOfSelectedArtifactThreads, yOffsetForTotalThreadsText);
        // Draw number of different thread types pedestal at the top
        final int numberOfSelectedThreadTypesWithNumericMetricValueInSelection =
                ThreadVisualizationUtil.getNumberOfSelectedThreadTypesWithNumericMetricValueInSelection(artifact, metricIdentifier, selectedThreadArtifacts);
//        final int numberOfDifferentThreadTypes = ThreadVisualizationUtil.getNumberOfSelectedThreadTypesInSelection(artifact, selectedThreadArtifacts);
        drawPedestal(labelWidth, true, fontSize, numberOfSelectedThreadTypesWithNumericMetricValueInSelection, yOffsetForDifferentClassesText);
//        drawNumberOfDifferentThreadTypesLabel(labelWidth, fontSize, numberOfDifferentThreadTypes, yOffsetForDifferentClassesText);
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
