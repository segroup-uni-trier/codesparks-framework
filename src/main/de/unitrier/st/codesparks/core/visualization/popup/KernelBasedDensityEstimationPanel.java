/*
 * Copyright (c) 2021. Oliver Moseler
 */

package de.unitrier.st.codesparks.core.visualization.popup;

import com.intellij.ui.JBColor;
import com.intellij.ui.components.JBPanel;
import com.intellij.util.ui.components.BorderLayoutPanel;
import de.unitrier.st.codesparks.core.CoreUtil;
import de.unitrier.st.codesparks.core.data.AMetricIdentifier;
import de.unitrier.st.codesparks.core.data.AThreadArtifact;
import de.unitrier.st.codesparks.core.data.ThreadArtifactCluster;
import de.unitrier.st.codesparks.core.data.ThreadArtifactClustering;
import de.unitrier.st.codesparks.core.visualization.VisConstants;
import de.unitrier.st.codesparks.core.visualization.thread.VisualThreadClusterProperties;
import de.unitrier.st.codesparks.core.visualization.thread.VisualThreadClusterPropertiesManager;
import smile.stat.distribution.KernelDensity;

import java.awt.*;
import java.util.*;
import java.util.List;

public class KernelBasedDensityEstimationPanel extends JBPanel<BorderLayoutPanel>
{
    private final List<IThreadSelectable> threadSelectionProvider;
    private final AMetricIdentifier primaryMetricIdentifier;
    private ThreadArtifactClustering threadArtifactClustering;

    public KernelBasedDensityEstimationPanel(
            final List<IThreadSelectable> threadSelectionProvider
            , final AMetricIdentifier primaryMetricIdentifier
            , final ThreadArtifactClustering threadArtifactClustering
    )
    {
        this.threadSelectionProvider = threadSelectionProvider;
        this.primaryMetricIdentifier = primaryMetricIdentifier;
        this.threadArtifactClustering = threadArtifactClustering;
    }

    public void setThreadArtifactClustering(final ThreadArtifactClustering threadArtifactClustering)
    {
        this.threadArtifactClustering = threadArtifactClustering;
        repaint();
    }

    @Override
    public void paint(final Graphics g)
    {
        super.paint(g);
        final Optional<IThreadSelectable> any = threadSelectionProvider.stream().findAny();
        if (any.isEmpty())
        {
            return;
        }
        final IThreadSelectable threadSelectable = any.get();
        final Set<AThreadArtifact> threadArtifacts = threadSelectable.getSelectedThreadArtifacts();


        final VisualThreadClusterPropertiesManager clusterPropertiesManager = VisualThreadClusterPropertiesManager.getInstance();

        final int width = this.getWidth();
        final int height = this.getHeight();

        final int topOffset = 30;
        final int horizontalMargin = 20;
        final int verticalMargin = 1;
        final Graphics2D graphics2D = (Graphics2D) g;
        graphics2D.setStroke(new BasicStroke(2));
        graphics2D.setColor(VisConstants.BORDER_COLOR);
        // Draw the x axis at the bottom of the panel
        final int xAxisY = height - verticalMargin;
        graphics2D.drawLine(horizontalMargin, xAxisY, width - horizontalMargin, xAxisY);
        // Draw the y axis, leave an offset to the top
        graphics2D.drawLine(horizontalMargin, topOffset, horizontalMargin, height - verticalMargin);


        final int size = threadArtifacts.size();
        final Map<Double, Integer> valueOccurrences = new HashMap<>(size);
        int maxOccurrence = Integer.MIN_VALUE;
        final double[] metricValues = new double[size];
        int i = 0;
        for (final AThreadArtifact threadArtifact : threadArtifacts)
        {
            final double metricValue = threadArtifact.getNumericalMetricValue(primaryMetricIdentifier);
            metricValues[i++] = metricValue;
            final int occurrences = valueOccurrences.getOrDefault(metricValue, 0) + 1;
            valueOccurrences.put(metricValue, occurrences);
            maxOccurrence = Math.max(maxOccurrence, occurrences);
        }
        double maxMetricValue = Double.MIN_VALUE;
        double minMetricValue = Double.MAX_VALUE;
        for (final double metricValue : metricValues)
        {
            maxMetricValue = Math.max(maxMetricValue, metricValue);
            minMetricValue = Math.min(minMetricValue, metricValue);
        }

        final int vizHeight = height - topOffset;
        final int yStep = vizHeight / (maxOccurrence + 1);//size;
        final Map<Double, Integer> yValues = new HashMap<>();

        final int vizWith = width - 2 * horizontalMargin;
        final int dotWithMetricValues = 6;

        for (final ThreadArtifactCluster cluster : threadArtifactClustering)
        {
            final VisualThreadClusterProperties properties = clusterPropertiesManager.getProperties(cluster);
            JBColor color = VisConstants.ORANGE;
            if (properties != null)
            {
                final JBColor propertiesColor = properties.getColor();
                if (propertiesColor != null)
                {
                    color = propertiesColor;
                }
            }
            graphics2D.setColor(color);

            final Set<AThreadArtifact> threadArtifactsOfCluster = threadSelectable.getSelectedThreadArtifactsOfCluster(cluster);

            for (final AThreadArtifact threadArtifact : threadArtifactsOfCluster)
            {
                final double metricValue = threadArtifact.getNumericalMetricValue(primaryMetricIdentifier);
                final double xValue = metricValue / maxMetricValue * vizWith;
                final Integer yValue = yValues.getOrDefault(xValue, yStep);
                graphics2D.fillRect(horizontalMargin + (int) xValue - dotWithMetricValues / 2, height - yValue, dotWithMetricValues,
                        dotWithMetricValues);
                yValues.put(xValue, yValue + yStep);
            }
        }

//                graphics2D.setColor(VisConstants.ORANGE);
//                for (final double metricValue : metricValues)
//                {
//                    final double xValue = metricValue / maxMetricValue * vizWith;
//                    final Integer yValue = yValues.getOrDefault(xValue, yStep);
//                    graphics2D.fillRect(horizontalMargin + (int) xValue - dotWithMetricValues / 2, height - yValue, dotWithMetricValues, dotWithMetricValues);
//                    yValues.put(xValue, yValue + yStep);
//                }
        graphics2D.setColor(VisConstants.BORDER_COLOR);
        String infoString = "";
        if (size > 1)
        { // Cannot compute a density for only one value!
            final double bandWidth = 0.01;
            final KernelDensity kernelDensity = new KernelDensity(metricValues, bandWidth);

            final int dotsToShow = vizWith / 4; // A dot all four pixels

            final double step = maxMetricValue / dotsToShow;//0.002;//(maxMetricValue - minMetricValue) / (4 * size);
            final int dotWithProbability = 2;
            for (double j = 0d; j <= maxMetricValue; j += step)
            {
                final double pj = kernelDensity.p(j);
                final double xValue = j / maxMetricValue * vizWith;
                final double normalizedPj = pj / 100d;
                int yValue = (int) (normalizedPj * vizHeight);
                graphics2D.fillRect(horizontalMargin + (int) xValue - dotWithProbability / 2, height - yValue, dotWithProbability, dotWithProbability);
            }
            infoString += /*"Kernel Based Density Estimation: */"kernel=gaussian, bandwidth=" + bandWidth + ", ";
        }

        infoString += "max metric value=" + CoreUtil.formatPercentage(maxMetricValue) + "(x axis)";
        final int stringWidth = graphics2D.getFontMetrics().stringWidth(infoString);
        graphics2D.drawString(infoString, width / 2 - stringWidth / 2, topOffset / 2);
    }
}
