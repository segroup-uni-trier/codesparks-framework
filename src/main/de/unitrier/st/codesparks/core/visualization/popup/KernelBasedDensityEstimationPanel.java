/*
 * Copyright (c) 2021. Oliver Moseler
 */

package de.unitrier.st.codesparks.core.visualization.popup;

import com.intellij.ui.JBColor;
import com.intellij.ui.components.JBPanel;
import com.intellij.util.ui.components.BorderLayoutPanel;
import de.unitrier.st.codesparks.core.CoreUtil;
import de.unitrier.st.codesparks.core.data.*;
import de.unitrier.st.codesparks.core.visualization.VisConstants;
import de.unitrier.st.codesparks.core.visualization.thread.IThreadSelectableIndexProvider;
import de.unitrier.st.codesparks.core.visualization.thread.ThreadColor;
import de.unitrier.st.codesparks.core.visualization.thread.VisualThreadClusterProperties;
import de.unitrier.st.codesparks.core.visualization.thread.VisualThreadClusterPropertiesManager;
import smile.stat.distribution.KernelDensity;

import java.awt.*;
import java.awt.geom.RoundRectangle2D;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class KernelBasedDensityEstimationPanel extends JBPanel<BorderLayoutPanel>
{
    private final IThreadSelectableIndexProvider selectableIndexProvider;
    private final List<IThreadSelectable> threadSelectables;
    private final AMetricIdentifier primaryMetricIdentifier;
    private ThreadArtifactClustering threadArtifactClustering;

    public KernelBasedDensityEstimationPanel(
            final IThreadSelectableIndexProvider selectableIndexProvider
            , final List<IThreadSelectable> threadSelectables
            , final AMetricIdentifier primaryMetricIdentifier
            , final ThreadArtifactClustering threadArtifactClustering
    )
    {
        this.selectableIndexProvider = selectableIndexProvider;
        this.threadSelectables = threadSelectables;
        this.primaryMetricIdentifier = primaryMetricIdentifier;
        this.threadArtifactClustering = threadArtifactClustering;
    }

    public void setThreadArtifactClustering(final ThreadArtifactClustering threadArtifactClustering)
    {
        this.threadArtifactClustering = threadArtifactClustering;
        repaint();
    }

    private static final int minWidth = 400;
    private static final int minHeight = 150;

    @Override
    public void paint(final Graphics g)
    {
        super.paint(g);
        final int index = selectableIndexProvider.getThreadSelectableIndex();
        if (index < 0)
        {
            return;
        }

        final IThreadSelectable threadSelectable = threadSelectables.get(index);
        Set<AThreadArtifact> threadArtifactsToShow = threadSelectable.getSelectedThreadArtifacts();
        int numberOfThreadsToShow = threadArtifactsToShow.size();
        final boolean createDisabledViz = numberOfThreadsToShow == 0;
        if (createDisabledViz)
        {
            threadArtifactsToShow = threadSelectable.getFilteredThreadArtifacts();
            numberOfThreadsToShow = threadArtifactsToShow.size();
        }

        final int width = Math.max(minWidth, this.getWidth());
        final int height = Math.max(minHeight, this.getHeight());

        final int topOffset = 10;
        final int horizontalMargin = 24;
        final int verticalMargin = 20;
        final Graphics2D graphics2D = (Graphics2D) g;
        graphics2D.setStroke(new BasicStroke(2));
        graphics2D.setColor(VisConstants.BORDER_COLOR);
        // Draw the x-axis at the bottom of the panel
        final int xAxisY = height - verticalMargin;
        graphics2D.drawLine(horizontalMargin, xAxisY, width - horizontalMargin, xAxisY);
        // Draw the y-axis, leave an offset to the top
        // graphics2D.drawLine(horizontalMargin, topOffset, horizontalMargin, height - verticalMargin);

        final Map<Double, Integer> valueOccurrences = new HashMap<>(numberOfThreadsToShow);
        int maxOccurrence = Integer.MIN_VALUE;
        final double[] metricValuesOfThreadsToShow = new double[numberOfThreadsToShow];
        int i = 0;
        double maxMetricValueOfThreadsToShow = Double.MIN_VALUE;
        for (final AThreadArtifact threadArtifact : threadArtifactsToShow)
        {
            final double metricValue = Math.max(0.0001, ((int) (threadArtifact.getNumericalMetricValue(primaryMetricIdentifier) * 1000)) / 1000d);
            metricValuesOfThreadsToShow[i++] = metricValue;
            final int occurrences = valueOccurrences.getOrDefault(metricValue, 0) + 1;
            valueOccurrences.put(metricValue, occurrences);
            maxOccurrence = Math.max(maxOccurrence, occurrences);
            maxMetricValueOfThreadsToShow = Math.max(maxMetricValueOfThreadsToShow, metricValue);
        }
        // ------------
        final Set<AThreadArtifact> allThreads = Stream.concat(threadSelectable.getFilteredThreadArtifacts().stream(),
                threadSelectable.getSelectedThreadArtifacts().stream()).collect(Collectors.toSet());
        double maxMetricValueOfAll = Double.MIN_VALUE;
        for (final AThreadArtifact threadArtifact : allThreads)//metricValuesOfThreadsToShow)
        {
            final double metricValue = threadArtifact.getNumericalMetricValue(primaryMetricIdentifier);
            maxMetricValueOfAll = Math.max(maxMetricValueOfAll, metricValue);
        }
        // ------------
        final int vizHeight = height - (topOffset + 2 * verticalMargin);
        final int vizWith = width - 2 * horizontalMargin;

        // Top line determining the beginning of the viz area
        // graphics2D.drawLine(horizontalMargin, topOffset + verticalMargin, horizontalMargin + vizWith, topOffset + verticalMargin);

        final double yStep = (double) vizHeight / (maxOccurrence + 1);//size;
        final Map<Double, Double> yValues = new HashMap<>();
        final VisualThreadClusterPropertiesManager clusterPropertiesManager = VisualThreadClusterPropertiesManager.getInstance(threadArtifactClustering);
        final double dotWidth = 6d;
        int clusterNum = 0;
        for (final ThreadArtifactCluster cluster : threadArtifactClustering)
        {
            final VisualThreadClusterProperties properties = clusterPropertiesManager.getOrDefault(cluster, clusterNum);
            JBColor clusterColor = properties.getColor();
            if (createDisabledViz || threadArtifactClustering.size() > 6)
            {
                clusterColor = ThreadColor.getDisabledColor(clusterColor);
            }
            graphics2D.setColor(clusterColor);

            Set<AThreadArtifact> threadArtifactsOfCluster;
            if (createDisabledViz)
            {
                threadArtifactsOfCluster = Set.copyOf(cluster);
            } else
            {
                threadArtifactsOfCluster = threadSelectable.getSelectedThreadArtifactsOfCluster(cluster);
            }

            for (final AThreadArtifact threadArtifact : threadArtifactsOfCluster)
            {
                // When it comes to showing the values, the minimal value we distinguish is 0.01
                final double metricValue = Math.max(0.0001, ((int) (threadArtifact.getNumericalMetricValue(primaryMetricIdentifier) * 1000)) / 1000d);
                final double xValue = metricValue / maxMetricValueOfAll * vizWith;
                final Double yValue = yValues.getOrDefault(xValue, yStep);
                final RoundRectangle2D threadDot = new RoundRectangle2D.Double(
                        horizontalMargin + xValue - dotWidth * 0.5
                        , xAxisY - yValue - dotWidth * 0.5
                        , dotWidth
                        , dotWidth
                        , 0d
                        , 0d
                );
                graphics2D.fill(threadDot);

                yValues.put(xValue, yValue + yStep);
            }
        }

        // Labels and tikz on the x-axis

        graphics2D.setColor(VisConstants.BORDER_COLOR);

        graphics2D.drawLine(horizontalMargin + vizWith, xAxisY - 1, horizontalMargin + vizWith, xAxisY + 3); // max tik
        graphics2D.drawLine(horizontalMargin, xAxisY - 1, horizontalMargin, xAxisY + 3); // min tik
        graphics2D.drawLine(horizontalMargin + vizWith / 2, xAxisY - 1, horizontalMargin + vizWith / 2, xAxisY + 3); // middle tik

        final FontMetrics fontMetrics = graphics2D.getFontMetrics();
        final String xAxisMaxLabel = CoreUtil.formatPercentage(maxMetricValueOfAll);
        final int stringWidthMaxLabel = fontMetrics.stringWidth(xAxisMaxLabel);
        graphics2D.drawString(xAxisMaxLabel, horizontalMargin + vizWith - stringWidthMaxLabel / 2
                //, xAxisY + 3
                , topOffset + verticalMargin + vizHeight + 16
        );

        final String xAxisMinLabel = CoreUtil.formatPercentage(0, true);
        final int stringWidthMinLabel = fontMetrics.stringWidth(xAxisMinLabel);
        graphics2D.drawString(xAxisMinLabel, horizontalMargin - stringWidthMinLabel / 2
                //, xAxisY + 3
                , topOffset + verticalMargin + vizHeight + 16
        );

        final String xAxisMidLabel = CoreUtil.formatPercentage(maxMetricValueOfAll / 2);
        final int stringWidthMidLabel = fontMetrics.stringWidth(xAxisMidLabel);
        graphics2D.drawString(xAxisMidLabel, horizontalMargin + (vizWith / 2) - stringWidthMidLabel / 2
                //, xAxisY + 3
                , topOffset + verticalMargin + vizHeight + 16
        );

        if (!showKernelBasedDensityEstimation)
        {
            return;
        }

        // The kernel-based-density-estimation-function graph

        String infoString = "";
        if (numberOfThreadsToShow > 1)
        { // Cannot compute a density for only one value!
            final boolean showMinimaMarkers =
                    threadArtifactClustering.getStrategy().equals(KernelBasedDensityEstimationClustering.getInstance(primaryMetricIdentifier));

            final double bandWidth = 0.01;
            final KernelDensity kernelDensity = new KernelDensity(metricValuesOfThreadsToShow, bandWidth);

            final int dotsToShow = vizWith / 4; // A dot every four pixels

            final double step = maxMetricValueOfAll / dotsToShow;
            final int dotWidthProbability = 2;

            boolean firstRun = true;
            boolean minFound = false;
            double previousProbability = 0d;
            double delta;
            for (double j = 0d; j <= maxMetricValueOfAll; j += step)
            {
                final double probability = kernelDensity.p(j);
                final double xValue = j / maxMetricValueOfAll * vizWith;
                final int xPos = (int) (horizontalMargin + xValue - dotWidthProbability / 2d);
                final double normalizedPj = probability / 100d;
                int yValue = (int) (normalizedPj * vizHeight);
                final int yPos = topOffset + verticalMargin + vizHeight - yValue;
                graphics2D.setColor(VisConstants.BORDER_COLOR);
                //noinspection SuspiciousNameCombination
                graphics2D.fillRect(xPos
                        //horizontalMargin + (int) xValue - dotWidthProbability / 2,
                        //xAxisY - yValue
                        , yPos - 2 // the '-2' is an adjustment to better see the values
                        , dotWidthProbability
                        , dotWidthProbability
                );

                if (showMinimaMarkers)
                {
                    // draw markers for every split point, i.e. the local minima of the estimated density function
                    delta = probability - previousProbability;
                    previousProbability = probability;
                    if (delta > 0 && !minFound)
                    {
                        minFound = true;
                        if (firstRun)
                        {
                            firstRun = false;
                        } else
                        {
                            final int splitPointXPos = (int) (horizontalMargin + ((j - step) / maxMetricValueOfAll * vizWith));
                            // final int splitPointY = topOffset + verticalMargin;
                            graphics2D.setColor(VisConstants.ORANGE);
                            //graphics2D.setStroke(new BasicStroke(1.5f));
                            graphics2D.drawLine(splitPointXPos, yPos - 9, splitPointXPos, yPos + 7);
                            graphics2D.fillRect(splitPointXPos - 2, yPos - 3, 4, 4);
                        }
                    } else if (delta < 0)
                    {
                        minFound = false;
                    }
                }
            }
            infoString += "kernel=gaussian, bandwidth=" + bandWidth;
        }

        graphics2D.setColor(VisConstants.BORDER_COLOR);
        //final FontMetrics fontMetrics = graphics2D.getFontMetrics();
        final int stringWidth = fontMetrics.stringWidth(infoString);
        graphics2D.drawString(infoString, width / 2 - stringWidth / 2, topOffset + 10);
    }

    private boolean showKernelBasedDensityEstimation = false;

    public void setShowKernelBasedDensityEstimation(final boolean b)
    {
        this.showKernelBasedDensityEstimation = b;
        repaint();
    }

}
