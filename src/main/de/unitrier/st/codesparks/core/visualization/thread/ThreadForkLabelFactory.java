/*
 * Copyright (c) 2021. Oliver Moseler
 */

package de.unitrier.st.codesparks.core.visualization.thread;

import com.intellij.ui.JBColor;
import com.intellij.ui.paint.PaintUtil;
import com.intellij.util.ui.UIUtil;
import de.unitrier.st.codesparks.core.data.*;
import de.unitrier.st.codesparks.core.visualization.AArtifactVisualizationLabelFactory;
import de.unitrier.st.codesparks.core.visualization.VisConstants;
import de.unitrier.st.codesparks.core.visualization.VisualizationUtil;
import de.unitrier.st.codesparks.core.visualization.popup.ThreadColor;

import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public final class ThreadForkLabelFactory extends AArtifactVisualizationLabelFactory
{
    public ThreadForkLabelFactory(final IMetricIdentifier primaryMetricIdentifier)
    {
        super(primaryMetricIdentifier);
    }

    public ThreadForkLabelFactory(final IMetricIdentifier primaryMetricIdentifier, final int sequence)
    {
        super(primaryMetricIdentifier, sequence);
    }

    @Override
    public JLabel createArtifactLabel(final AArtifact artifact)
    {
        final List<AThreadArtifact> threadArtifacts = new ArrayList<>(artifact.getThreadArtifacts());

        if (threadArtifacts.isEmpty())
        {
            return emptyLabel();
        }

        final GraphicsConfiguration defaultConfiguration =
                GraphicsEnvironment.getLocalGraphicsEnvironment().getDefaultScreenDevice().getDefaultConfiguration();

        final int threadsPerColumn = 3;
        final int threadMetaphorWidth = 24;
        final int lineHeight = VisualizationUtil.getLineHeightFloor(VisConstants.getLineHeight(), threadsPerColumn);

        final BufferedImage bi = UIUtil.createImage(defaultConfiguration, 300, lineHeight,
                BufferedImage.TYPE_INT_ARGB, PaintUtil.RoundingMode.CEIL);

        final Graphics2D graphics = (Graphics2D) bi.getGraphics();
        graphics.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        // Draw the fully transparent background
        VisualizationUtil.drawTransparentBackground(graphics, bi);

        // Thread metaphor
        graphics.setColor(VisConstants.BORDER_COLOR);

        final int X_OFFSET_LEFT = 2;
        final int X_OFFSET_RIGHT = 1;
        final int barChartWidth = 24;

        final int barrierXPos = threadMetaphorWidth / 2;

        // Leading arrow
        graphics.fillRect(X_OFFSET_LEFT, lineHeight / 2, barrierXPos - 1, 1);
        graphics.drawLine(X_OFFSET_LEFT + barrierXPos - 3, lineHeight / 2 - 3, X_OFFSET_LEFT + barrierXPos, lineHeight / 2);
        graphics.drawLine(X_OFFSET_LEFT + barrierXPos - 3, lineHeight / 2 + 3, X_OFFSET_LEFT + barrierXPos, lineHeight / 2);

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

        //final double threadFilteredTotalArtifactMetricValue = DataUtil.getThreadFilteredRelativeNumericMetricValueOf(artifact, primaryMetricIdentifier);

        final double threadFilteredTotalMetricValueOfArtifact = getThreadFilteredTotalMetricValueOfArtifact(artifact);

        final List<ThreadArtifactCluster> threadClusters = artifact.getSortedDefaultThreadArtifactClustering(primaryMetricIdentifier);

        for (final ThreadArtifactCluster threadCluster : threadClusters)
        {
            JBColor color = ThreadColor.getNextColor(clusterNum);
            graphics.setColor(color);

            int clusterWidth;
            double percent = getThreadFilteredArtifactMetricValueOfClusterRelativeToTotal(threadArtifacts, threadCluster,
                    threadFilteredTotalMetricValueOfArtifact);

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

            final VisualThreadClusterProperties visualThreadClusterProperties =
                    new VisualThreadClusterPropertiesBuilder(threadCluster)
                            .setColor(color)
                            .setPosition(clusterNum)
                            .get();
            clusterPropertiesManager.registerProperties(visualThreadClusterProperties);

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

    private double getThreadFilteredTotalMetricValueOfArtifact(final AArtifact artifact)
    {
        final double total =
                artifact.getThreadArtifacts().stream().filter(threadExecutingArtifact -> !threadExecutingArtifact.isFiltered())
                        .mapToDouble(threadExecutingArtifact -> threadExecutingArtifact.getNumericalMetricValue(primaryMetricIdentifier)).sum();
        return total;
    }

    private double getThreadFilteredArtifactMetricValueOfClusterRelativeToTotal(final List<AThreadArtifact> threadsOfArtifact,
                                                                                final ThreadArtifactCluster threadArtifactCluster,
                                                                                double total)
    {
        final double sum =
                threadsOfArtifact.stream().filter(threadExecutingArtifact -> !threadExecutingArtifact.isFiltered() && threadArtifactCluster.stream().anyMatch(
                        clusterThread -> !clusterThread.isFiltered() && clusterThread.getIdentifier().equals(threadExecutingArtifact.getIdentifier())
                )).mapToDouble(threadExecutingArtifact -> threadExecutingArtifact.getNumericalMetricValue(primaryMetricIdentifier)).sum();
        final double ratio = sum / total;
        return ratio;
    }

}
