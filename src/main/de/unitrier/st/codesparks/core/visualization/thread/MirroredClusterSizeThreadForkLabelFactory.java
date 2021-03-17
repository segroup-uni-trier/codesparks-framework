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
import java.util.OptionalDouble;

public final class MirroredClusterSizeThreadForkLabelFactory extends AArtifactVisualizationLabelFactory
{
    public MirroredClusterSizeThreadForkLabelFactory(final IMetricIdentifier primaryMetricIdentifier)
    {
        super(primaryMetricIdentifier);
    }

    public MirroredClusterSizeThreadForkLabelFactory(final IMetricIdentifier primaryMetricIdentifier, final int sequence)
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

        final int X_OFFSET_LEFT = 0;
        final int threadsPerColumn = 3;
        final int threadMetaphorWidth = 24;
        final int barChartWidth = 24;
        final int X_OFFSET_RIGHT = 2;

        final int lineHeight = VisualizationUtil.getLineHeightFloor(VisConstants.getLineHeight(), threadsPerColumn);

        final BufferedImage bi = UIUtil.createImage(defaultConfiguration, X_OFFSET_LEFT + threadMetaphorWidth + barChartWidth + X_OFFSET_RIGHT, lineHeight,
                BufferedImage.TYPE_INT_ARGB, PaintUtil.RoundingMode.CEIL);

        final Graphics2D graphics = (Graphics2D) bi.getGraphics();
        graphics.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        // Draw the fully transparent background
        VisualizationUtil.drawTransparentBackground(graphics, bi);


        // The rectangle for the bars
        graphics.setColor(VisConstants.BORDER_COLOR);

        final Rectangle threadVisualisationArea = new Rectangle(
                X_OFFSET_LEFT, 0, barChartWidth - 1, lineHeight - 1);
        VisualizationUtil.drawRectangle(graphics, threadVisualisationArea);

        // Thread metaphor
        final int barrierXOffset = 9;
        final int barrierXPos = barChartWidth + barrierXOffset;//threadMetaphorWidth / 2;
        // Vertical bar or barrier, respectively
        final int barrierWidth = 3;
        graphics.fillRect(X_OFFSET_LEFT + barrierXPos, 0, barrierWidth, lineHeight);

        // Subsequent arrow
        final int arrowLenght = threadMetaphorWidth / 2;
        final int arrowStartX = X_OFFSET_LEFT + barrierXPos + barrierWidth;
        graphics.fillRect(arrowStartX, lineHeight / 2, arrowLenght, 1);
        graphics.drawLine(arrowStartX + arrowLenght - 3, lineHeight / 2 - 3, arrowStartX + arrowLenght, lineHeight / 2);
        graphics.drawLine(arrowStartX + arrowLenght - 3, lineHeight / 2 + 3, arrowStartX + arrowLenght, lineHeight / 2);


        // Draw the clusters
        final int threadSquareEdgeLength = 3;//(lineHeight - 6) / threadsPerColumn;
        final int clusterBarMaxWidth = 20;
        int threadSquareYPos = lineHeight - threadSquareEdgeLength - 2;
        final int threadSquareOffset = threadSquareEdgeLength + 1;

        int clusterNum = 0;
        final VisualThreadClusterPropertiesManager clusterPropertiesManager = VisualThreadClusterPropertiesManager.getInstance();

        //final double threadFilteredTotalArtifactMetricValue = DataUtil.getThreadFilteredRelativeNumericMetricValueOf(artifact, primaryMetricIdentifier);

        final double totalNumberOfFilteredThreads =
                (double) artifact.getThreadArtifacts().stream().filter(threadExecutingArtifact -> !threadExecutingArtifact.isFiltered()).count();

        final List<ThreadArtifactCluster> threadClusters = artifact.getSortedDefaultThreadArtifactClustering(primaryMetricIdentifier);

        for (final ThreadArtifactCluster threadCluster : threadClusters)
        {
            JBColor clusterColor = ThreadColor.getNextColor(clusterNum);
            graphics.setColor(clusterColor);

            double percent = threadCluster.size() / totalNumberOfFilteredThreads;

            int clusterWidth;
            if (percent > 0D)
            {
                int discrete = (int) (percent * 100 / 10 + 0.9999);
                clusterWidth = clusterBarMaxWidth / 10 * discrete;
            } else
            {
                clusterWidth = 0;
            }
            graphics.fillRect(X_OFFSET_LEFT + barChartWidth - clusterWidth - 2, threadSquareYPos, clusterWidth, threadSquareEdgeLength);

            if (clusterWidth > 0)
            {
                // Arrows after barrier
                graphics.fillRect(X_OFFSET_LEFT + barChartWidth - 2, threadSquareYPos + 1, arrowLenght - 1, 1);
            }
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
        //noinspection UnnecessaryLocalVariable
        final double total =
                artifact.getThreadArtifacts().stream().filter(threadExecutingArtifact -> !threadExecutingArtifact.isFiltered())
                        .mapToDouble(threadExecutingArtifact -> threadExecutingArtifact.getNumericalMetricValue(primaryMetricIdentifier)).sum();
        return total;
    }

    private double getThreadFilteredArtifactMetricValueSumOfClusterRelativeToTotal(final List<AThreadArtifact> threadsOfArtifact,
                                                                                   final ThreadArtifactCluster threadArtifactCluster,
                                                                                   final double total)
    {
        final double sum =
                threadsOfArtifact.stream().filter(threadExecutingArtifact -> !threadExecutingArtifact.isFiltered() && threadArtifactCluster.stream().anyMatch(
                        clusterThread -> !clusterThread.isFiltered() && clusterThread.getIdentifier().equals(threadExecutingArtifact.getIdentifier())
                )).mapToDouble(threadExecutingArtifact -> threadExecutingArtifact.getNumericalMetricValue(primaryMetricIdentifier)).sum();
        //noinspection UnnecessaryLocalVariable
        final double ratio = sum / total;
        return ratio;
    }

    private double getThreadFilteredArtifactMetricValueAverageOfClusterRelativeToTotal(final List<AThreadArtifact> threadsOfArtifact,
                                                                                       final ThreadArtifactCluster threadArtifactCluster,
                                                                                       final double total)
    {
        final OptionalDouble average =
                threadsOfArtifact.stream().filter(threadExecutingArtifact -> !threadExecutingArtifact.isFiltered() && threadArtifactCluster.stream().anyMatch(
                        clusterThread -> !clusterThread.isFiltered() && clusterThread.getIdentifier().equals(threadExecutingArtifact.getIdentifier())
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
