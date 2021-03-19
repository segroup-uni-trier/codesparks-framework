/*
 * Copyright (c) 2021. Oliver Moseler
 */

package de.unitrier.st.codesparks.core.visualization.thread;

import com.intellij.ui.JBColor;
import com.intellij.ui.paint.PaintUtil;
import com.intellij.util.ui.UIUtil;
import de.unitrier.st.codesparks.core.data.AArtifact;
import de.unitrier.st.codesparks.core.data.AMetricIdentifier;
import de.unitrier.st.codesparks.core.data.AThreadArtifact;
import de.unitrier.st.codesparks.core.data.ThreadArtifactCluster;
import de.unitrier.st.codesparks.core.visualization.AArtifactVisualizationLabelFactory;
import de.unitrier.st.codesparks.core.visualization.VisConstants;
import de.unitrier.st.codesparks.core.visualization.VisualizationUtil;
import de.unitrier.st.codesparks.core.visualization.popup.ThreadColor;

import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.Collection;
import java.util.List;
import java.util.OptionalDouble;

public final class ThreadForkLabelFactory extends AArtifactVisualizationLabelFactory
{
    public ThreadForkLabelFactory(final AMetricIdentifier primaryMetricIdentifier)
    {
        super(primaryMetricIdentifier, -1);
    }

    public ThreadForkLabelFactory(final AMetricIdentifier primaryMetricIdentifier, final int sequence)
    {
        super(primaryMetricIdentifier, sequence);
    }

    public ThreadForkLabelFactory(final AMetricIdentifier primaryMetricIdentifier, final int sequence, final int xOffsetLeft)
    {
        super(primaryMetricIdentifier, sequence, xOffsetLeft);
    }

    @Override
    public JLabel createArtifactLabel(final AArtifact artifact)
    {
        final Collection<AThreadArtifact> threadArtifacts = artifact.getThreadArtifacts();

        if (threadArtifacts.isEmpty())
        {
            return emptyLabel();
        }

        final GraphicsConfiguration defaultConfiguration =
                GraphicsEnvironment.getLocalGraphicsEnvironment().getDefaultScreenDevice().getDefaultConfiguration();

        final int X_OFFSET_LEFT = this.X_OFFSET_LEFT + 1;
        final int threadsPerColumn = 3;
        final int threadMetaphorWidth = 24;
        final int barChartWidth = 24;
        final int X_OFFSET_RIGHT = 0;

        final int lineHeight = VisualizationUtil.getLineHeightFloor(VisConstants.getLineHeight(), threadsPerColumn);

        final BufferedImage bi = UIUtil.createImage(defaultConfiguration, X_OFFSET_LEFT + threadMetaphorWidth + barChartWidth + X_OFFSET_RIGHT, lineHeight,
                BufferedImage.TYPE_INT_ARGB, PaintUtil.RoundingMode.CEIL);

        final Graphics2D graphics = (Graphics2D) bi.getGraphics();
        graphics.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        // Draw the fully transparent background
        VisualizationUtil.drawTransparentBackground(graphics, bi);

        // Thread metaphor
        graphics.setColor(VisConstants.BORDER_COLOR);

        final int barrierXPos = threadMetaphorWidth / 2;

        // Leading arrow
        graphics.fillRect(X_OFFSET_LEFT, lineHeight / 2, barrierXPos - 1, 1);
        graphics.drawLine(X_OFFSET_LEFT + barrierXPos - 4, lineHeight / 2 - 3, X_OFFSET_LEFT + barrierXPos - 1, lineHeight / 2);
        graphics.drawLine(X_OFFSET_LEFT + barrierXPos - 4, lineHeight / 2 + 3, X_OFFSET_LEFT + barrierXPos - 1, lineHeight / 2);

        // Vertical bar or barrier, respectively
        final int barrierWidth = 3;
        graphics.fillRect(X_OFFSET_LEFT + barrierXPos, 0, barrierWidth, lineHeight);

        final Rectangle threadVisualisationArea = new Rectangle(
                X_OFFSET_LEFT + threadMetaphorWidth, 0, barChartWidth - 1, lineHeight - 1);

        VisualizationUtil.drawRectangle(graphics, threadVisualisationArea);


        // Draw the clusters
        final int threadSquareEdgeLength = 3;//(lineHeight - 6) / threadsPerColumn;
        final int clusterBarMaxWidth = 20;
        int threadSquareYPos = lineHeight - threadSquareEdgeLength - 2;
        final int threadSquareOffset = threadSquareEdgeLength + 1;

        int clusterNum = 0;
        final VisualThreadClusterPropertiesManager clusterPropertiesManager = VisualThreadClusterPropertiesManager.getInstance();

        // If there is no thread which is selected, i.e. all threads executing this artifact are filtered
        boolean createDisabledViz = threadArtifacts.stream().allMatch(AThreadArtifact::isFiltered);

        final double threadFilteredTotalMetricValueOfArtifact = getThreadFilteredTotalMetricValueOfArtifact(artifact, createDisabledViz);

        final List<ThreadArtifactCluster> threadClusters = artifact.getSortedDefaultThreadArtifactClustering(primaryMetricIdentifier);


        for (final ThreadArtifactCluster threadCluster : threadClusters)
        {
            JBColor clusterColor = ThreadColor.getNextColor(clusterNum, createDisabledViz);

            /*
             * Draw the metric value sum bar
             */

            final Color backgroundMetricColor = VisualizationUtil.getBackgroundMetricColor(clusterColor, .25f);
            JBColor clusterMetricValueSumColor = new JBColor(backgroundMetricColor, backgroundMetricColor);

            graphics.setColor(clusterMetricValueSumColor);

            int clusterWidth;
            double percent = getThreadFilteredArtifactMetricValueSumOfClusterRelativeToTotal(threadArtifacts, threadCluster,
                    threadFilteredTotalMetricValueOfArtifact, createDisabledViz);

            if (percent > 0D)
            {
                int discrete = (int) (percent * 100 / 10 + 0.9999);
                clusterWidth = clusterBarMaxWidth / 10 * discrete;
            } else
            {
                clusterWidth = 0;
            }
            graphics.fillRect(X_OFFSET_LEFT + threadMetaphorWidth + 2, threadSquareYPos, clusterWidth, threadSquareEdgeLength);

            if (clusterWidth > 0)
            {
                // Arrows after barrier
                graphics.fillRect(X_OFFSET_LEFT + barrierXPos + barrierWidth, threadSquareYPos + 1, barrierXPos - 1, 1);
            }

            // Save the position and color to the properties such that they can be reused in the neighbor artifact visualization
            final VisualThreadClusterProperties visualThreadClusterProperties =
                    new VisualThreadClusterPropertiesBuilder(threadCluster)
                            .setColor(clusterColor)
                            .setPosition(clusterNum)
                            .get();
            clusterPropertiesManager.registerProperties(visualThreadClusterProperties);

            /*
             * Draw the metric value avg bar
             */

            graphics.setColor(clusterColor);
            percent = getThreadFilteredArtifactMetricValueAverageOfClusterRelativeToTotal(threadArtifacts, threadCluster,
                    threadFilteredTotalMetricValueOfArtifact, createDisabledViz);

            if (percent > 0D)
            {
                int discrete = (int) (percent * 100 / 10 + 0.9999);
                clusterWidth = clusterBarMaxWidth / 10 * discrete;
            } else
            {
                clusterWidth = 0;
            }
            graphics.fillRect(X_OFFSET_LEFT + threadMetaphorWidth + 2, threadSquareYPos, clusterWidth, threadSquareEdgeLength);

            if (clusterWidth > 0)
            {
                // Arrows after barrier
                graphics.fillRect(X_OFFSET_LEFT + barrierXPos + barrierWidth, threadSquareYPos + 1, barrierXPos - 1, 1);
            }


            /*
             * -------------------------------------------
             */

            clusterNum += 1;

            threadSquareYPos -= threadSquareOffset;

        }
        // Creation of the label

        BufferedImage subimage = bi.getSubimage(0, 0, bi.getWidth(), bi.getHeight());
        ImageIcon imageIcon = new ImageIcon(subimage);

        JLabel jLabel = new JLabel();
        jLabel.setIcon(imageIcon);

        jLabel.setSize(imageIcon.getIconWidth(), imageIcon.getIconHeight());
        jLabel.addMouseListener(new DefaultThreadVisualizationMouseListener(jLabel, artifact, primaryMetricIdentifier));

        return jLabel;
    }

    private double getThreadFilteredTotalMetricValueOfArtifact(final AArtifact artifact, final boolean createDisabledViz)
    {
        //noinspection UnnecessaryLocalVariable
        final double total =
                artifact.getThreadArtifacts().stream().filter(threadExecutingArtifact -> createDisabledViz || !threadExecutingArtifact.isFiltered())
                        .mapToDouble(threadExecutingArtifact -> threadExecutingArtifact.getNumericalMetricValue(primaryMetricIdentifier)).sum();
        return total;
    }

    private double getThreadFilteredArtifactMetricValueSumOfClusterRelativeToTotal(final Collection<AThreadArtifact> threadsOfArtifact,
                                                                                   final ThreadArtifactCluster threadArtifactCluster,
                                                                                   final double total,
                                                                                   final boolean createDisabledViz)
    {
        final double sum =
                threadsOfArtifact.stream().filter(threadExecutingArtifact -> (createDisabledViz || !threadExecutingArtifact.isFiltered()) && threadArtifactCluster.stream().anyMatch(
                        clusterThread -> (createDisabledViz || !clusterThread.isFiltered()) && clusterThread.getIdentifier().equals(threadExecutingArtifact.getIdentifier())
                )).mapToDouble(threadExecutingArtifact -> threadExecutingArtifact.getNumericalMetricValue(primaryMetricIdentifier)).sum();
        //noinspection UnnecessaryLocalVariable
        final double ratio = sum / total;
        return ratio;
    }

    private double getThreadFilteredArtifactMetricValueAverageOfClusterRelativeToTotal(final Collection<AThreadArtifact> threadsOfArtifact,
                                                                                       final ThreadArtifactCluster threadArtifactCluster,
                                                                                       final double total,
                                                                                       final boolean createDisabledViz)
    {
        final OptionalDouble average =
                threadsOfArtifact.stream().filter(threadExecutingArtifact -> (createDisabledViz || !threadExecutingArtifact.isFiltered()) && threadArtifactCluster.stream().anyMatch(
                        clusterThread -> (createDisabledViz || !clusterThread.isFiltered()) && clusterThread.getIdentifier().equals(threadExecutingArtifact.getIdentifier())
                )).mapToDouble(threadExecutingArtifact -> threadExecutingArtifact.getNumericalMetricValue(primaryMetricIdentifier)).average();
        if (average.isPresent())
        {
            //noinspection UnnecessaryLocalVariable
            final double ratio = average.getAsDouble() / total;
            return ratio;
        }
        return Double.NaN;
    }

}
