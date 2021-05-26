/*
 * Copyright (c) 2021. Oliver Moseler
 */

package de.unitrier.st.codesparks.core.visualization.thread;

import com.intellij.ui.JBColor;
import de.unitrier.st.codesparks.core.data.AArtifact;
import de.unitrier.st.codesparks.core.data.AMetricIdentifier;
import de.unitrier.st.codesparks.core.data.ThreadArtifactCluster;
import de.unitrier.st.codesparks.core.data.ThreadArtifactClustering;
import de.unitrier.st.codesparks.core.visualization.VisConstants;
import de.unitrier.st.codesparks.core.visualization.popup.*;

import javax.swing.*;
import java.awt.*;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ZoomedThreadFork extends JPanel
{
    private final AArtifact artifact;
    private final AMetricIdentifier metricIdentifier;
    private ThreadArtifactClustering threadArtifactClustering;
    private final IThreadSelectableIndexProvider selectableIndexProvider;
    private final List<IThreadSelectable> threadSelectables;
    private final IClusterHoverable clusterHoverable;

    public ZoomedThreadFork(
            final AArtifact artifact
            , final AMetricIdentifier metricIdentifier
            , final ThreadArtifactClustering threadArtifactClustering
            , final IThreadSelectableIndexProvider selectableIndexProvider
            , final List<IThreadSelectable> threadSelectables
            , final IClusterHoverable clusterHoverable
    )
    {
        this.artifact = artifact;
        this.metricIdentifier = metricIdentifier;
        this.threadArtifactClustering = threadArtifactClustering;
        this.selectableIndexProvider = selectableIndexProvider;
        this.threadSelectables = threadSelectables;
        this.clusterHoverable = clusterHoverable;
        this.setLayout(null);
        final Dimension dimension = new Dimension(maxWidth, maxHeight);
        this.setPreferredSize(dimension);
        this.setMinimumSize(dimension);
    }

    private final int maxWidth = 480;
    private final int maxHeight = 150;

    public void setThreadArtifactClustering(final ThreadArtifactClustering threadArtifactClustering)
    {
        this.threadArtifactClustering = threadArtifactClustering;
        new Thread(this::doubleRepaint).start();
    }

    private final Object doubleRepaintLock = new Object();

    private void doubleRepaint()
    {
        repaint();
        synchronized (doubleRepaintLock)
        {
            try
            {
                doubleRepaintLock.wait();
            } catch (InterruptedException e)
            {
                // ignored
            }
            repaint();
        }
    }

    @Override
    public void paint(final Graphics g)
    {
        super.paint(g);
        this.removeAll();
        if (artifact == null)
        {
            return;
        }
        final int panelWidth = this.getWidth();
        final int panelHeight = this.getHeight();
        final int width = Math.min(maxWidth, panelWidth);
        final int height = Math.min(maxHeight, panelHeight);

        final int TOP_OFFSET = (panelHeight - height) / 2;
        final int LEFT_OFFSET = (panelWidth - width) / 2;

        final int horizontalMargin = 10;
        final int verticalMargin = 10;
//        System.out.println("ZoomedThreadFork: height=" + height + ", width=" + width + ", vizHeight=" + String.valueOf(height - 2 * verticalMargin) + ", " +
//                "vizWidth=" + String.valueOf(width - 2 * horizontalMargin));

        final Graphics2D graphics2D = (Graphics2D) g;
        graphics2D.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        //graphics2D.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0f));

/*
//-------------
        graphics2D.setColor(JBColor.RED);
        final int middleStrokeWidth = 4;
        graphics2D.setStroke(new BasicStroke(middleStrokeWidth));
        graphics2D.drawLine(LEFT_OFFSET + width / 2 - (int) Math.max(middleStrokeWidth / 2d, 1)
                , TOP_OFFSET
                , LEFT_OFFSET + width / 2 - (int) Math.max(middleStrokeWidth / 2d, 1)
                , TOP_OFFSET + height);
//-------------
        graphics2D.setColor(JBColor.BLUE);
        final int outStrokeWidth = 1;
        graphics2D.setStroke(new BasicStroke(outStrokeWidth));
        graphics2D.drawLine(LEFT_OFFSET + (int) (width / 8d)
                , TOP_OFFSET
                , LEFT_OFFSET + (int) (width / 8d)
                , TOP_OFFSET + height);
        graphics2D.drawLine(LEFT_OFFSET + (int) (width / 8d * 7) - (int) Math.max(outStrokeWidth / 2d, 1)
                , TOP_OFFSET
                , LEFT_OFFSET + (int) (width / 8d * 7) - (int) Math.max(outStrokeWidth / 2d, 1)
                , TOP_OFFSET + height);
//-------------
*/


        /*
         * Calculation of the positions
         */

        final double threadForkSymbolWith = (width - 2 * horizontalMargin) / 8d; // 1/2 * 1/4

        final double barrierWidth = threadForkSymbolWith * 0.15;
        final double arrowHeight = height * 0.03;


        graphics2D.setColor(VisConstants.BORDER_COLOR);

        // Arrow left
        final double leftBarrierXPos = horizontalMargin + threadForkSymbolWith - barrierWidth;
        final double arrowWidth = leftBarrierXPos - horizontalMargin;
        graphics2D.fillRect(LEFT_OFFSET + horizontalMargin
                , TOP_OFFSET + (int) (height / 2 - arrowHeight / 2)
                , (int) arrowWidth
                , (int) arrowHeight);
        final int leftArrowPartXPos = horizontalMargin + (int) (arrowWidth * 5d / 6);
        graphics2D.setStroke(new BasicStroke((float) arrowHeight));
        graphics2D.drawLine(LEFT_OFFSET + leftArrowPartXPos
                , TOP_OFFSET + (int) (height / 8d * 3)
                , LEFT_OFFSET + (int) leftBarrierXPos
                , TOP_OFFSET + (int) (height / 2 - arrowHeight / 2));
        graphics2D.drawLine(LEFT_OFFSET + leftArrowPartXPos
                , TOP_OFFSET + (int) (height / 8d * 5)
                , LEFT_OFFSET + (int) leftBarrierXPos
                , TOP_OFFSET + (int) (height / 2 + arrowHeight / 2));

        // Left barrier
        graphics2D.fillRect(LEFT_OFFSET + (int) leftBarrierXPos
                , TOP_OFFSET + verticalMargin
                , (int) barrierWidth
                , height - 2 * verticalMargin);

        // Arrow right
        final double rightBarrierXPos = width - threadForkSymbolWith - horizontalMargin;
        graphics2D.fillRect(LEFT_OFFSET + (int) (rightBarrierXPos + barrierWidth) - 1 // The -1 is a correction due to accuracy problems in floating point
                // arithmetics
                , TOP_OFFSET + (int) (height / 2 - arrowHeight / 2)
                , (int) arrowWidth
                , (int) arrowHeight);
        final int rightArrowPartXPos = width - leftArrowPartXPos;//threadForkSymbolWith * 3 / 4 * 4 / 5;
        graphics2D.setStroke(new BasicStroke((float) arrowHeight));
        graphics2D.drawLine(LEFT_OFFSET + rightArrowPartXPos
                , TOP_OFFSET + (int) (height / 8d * 3)
                , LEFT_OFFSET + (int) (rightBarrierXPos + barrierWidth)
                , TOP_OFFSET + (int) (height / 2 - arrowHeight / 2));
        graphics2D.drawLine(LEFT_OFFSET + rightArrowPartXPos
                , TOP_OFFSET + (int) (height / 8d * 5)
                , LEFT_OFFSET + (int) (rightBarrierXPos + barrierWidth)
                , TOP_OFFSET + (int) (height / 2 + arrowHeight / 2));

        // Right barrier
        graphics2D.fillRect(LEFT_OFFSET + (int) rightBarrierXPos
                , TOP_OFFSET + verticalMargin
                , (int) barrierWidth
                , height - 2 * verticalMargin);


        // The cluster visualization area on the left
        final double threadVizWidth = (width - 2 * horizontalMargin) * 3d / 8;
        final double rectVizWidth = threadVizWidth * 0.8;
        final double leftThreadRectXPos = leftBarrierXPos + barrierWidth + threadVizWidth * 0.2;

        final int strokeWidth = 2;
        final double halfStrokeWidth = strokeWidth / 2d;

        graphics2D.setStroke(new BasicStroke(strokeWidth));
        graphics2D.drawRect(LEFT_OFFSET + (int) (leftThreadRectXPos - strokeWidth)
                , TOP_OFFSET + (verticalMargin - strokeWidth)
                , (int) (rectVizWidth + halfStrokeWidth) - 1 // -1 such that the left and right rect will overlap such that visually only one line is there
                , height - 2 * verticalMargin + 2 * strokeWidth);

        // The cluster visualization area on the right
        double rightThreadRectXPos = width - leftThreadRectXPos - rectVizWidth;//leftBarrierXPos + barrierWidth + (int) (threadVizWidth * 1d / 3);
        rightThreadRectXPos = (rightThreadRectXPos - strokeWidth - halfStrokeWidth);
        graphics2D.drawRect(LEFT_OFFSET + (int) rightThreadRectXPos + 1 // +1 such that the left and right rect will overlap such that visually only one
                // line
                // is there
                , TOP_OFFSET + (verticalMargin - strokeWidth)
                , (int) (rectVizWidth + halfStrokeWidth)
                , height - 2 * verticalMargin + 2 * strokeWidth);


        /*
         * Draw the clusters
         */
        final int nrOfClusters = Math.max(3, threadArtifactClustering.size());

        /*
            let x be the space between the clusters resp. the margins to the top and bottom and y be the cluster width:

            (nr + 1) * x + nr * y = height
                    3 * x = y
         */

        final double clusterDistance = (height - (2 * verticalMargin)) / (double) (4 * nrOfClusters + 1);
        final double clusterHeight = 3 * clusterDistance;

//        System.out.println("ZoomedThreadFork: clusterDistance=" + clusterDistance + ", clusterHeight=" + clusterHeight);

        final VisualThreadClusterPropertiesManager clusterPropertiesManager = VisualThreadClusterPropertiesManager.getInstance(threadArtifactClustering);
        final Map<ThreadArtifactCluster, Boolean> clusterPropertiesPresent = new HashMap<>(threadArtifactClustering.size()); // Do not replace
        // threadClusters
        // .size()

        final double clusterYBase = height - verticalMargin - clusterDistance - clusterHeight;
        double clusterY = clusterYBase;
        final double leftClusterX = (leftThreadRectXPos + 0.05 * threadVizWidth);
        final double rightClusterX = rightThreadRectXPos + 0.05 * threadVizWidth;
        final double clusterButtonWidth = rectVizWidth * 0.9;

        final double clusterConnectionHeight = clusterHeight / 3d;
        final double connectionWidth = Math.abs(leftClusterX - (leftBarrierXPos + barrierWidth));

        int clusterNum = 0;
        for (final ThreadArtifactCluster threadCluster : threadArtifactClustering)
        {
            if (threadCluster.isEmpty())
            {
                continue;
            }

            int clusterPosition = -1;
            JBColor clusterColor = ThreadColor.getNextColor(clusterNum);
            final VisualThreadClusterProperties properties = clusterPropertiesManager.getProperties(threadCluster);
            if (properties != null)
            {
                clusterPropertiesPresent.put(threadCluster, true);
                clusterColor = properties.getOrSetColor(clusterColor);
                clusterPosition = properties.getOrSetPosition(clusterNum);
            }

            double clusterYToDraw = clusterY;
            if (clusterPosition > -1)
            {
                clusterYToDraw = clusterYBase - clusterPosition * (clusterDistance + clusterHeight);
            }

            // Cluster button left
            final Rectangle leftClusterButtonBoundsRectangle = new Rectangle(
                    LEFT_OFFSET + (int) leftClusterX
                    , TOP_OFFSET + (int) clusterYToDraw
                    , (int) clusterButtonWidth
                    , (int) clusterHeight);
            final ThreadClusterButton leftClusterButton = new ThreadClusterButton(
                    artifact
                    , threadArtifactClustering
                    , metricIdentifier
                    , threadCluster
                    , selectableIndexProvider
                    , threadSelectables
                    , clusterColor
                    , leftClusterButtonBoundsRectangle
                    , SumAvgClusterButtonFillStrategy.getInstance()
                    , clusterHoverable
            );
            add(leftClusterButton);
            // Cluster button right
            final Rectangle rightClusterButtonBoundsRectangle = new Rectangle(
                    LEFT_OFFSET + (int) rightClusterX - 2 // The -2 is a correction due to accuracy problems in floating point arithmetics
                    , TOP_OFFSET + (int) clusterYToDraw
                    , (int) clusterButtonWidth + 1 // The +1 is a correction due to accuracy problems in floating point arithmetics
                    , (int) clusterHeight);
            final ThreadClusterButton rightClusterButton = new ThreadClusterButton(
                    artifact
                    , threadArtifactClustering
                    , metricIdentifier
                    , threadCluster
                    , selectableIndexProvider
                    , threadSelectables
                    , clusterColor
                    , rightClusterButtonBoundsRectangle
                    , TotalNumberOfThreadsAndThreadTypesButtonFillStrategy.getInstance()
                    , clusterHoverable
            );
            add(rightClusterButton);

            // Register the buttons to each other such that they sync whenever a mouse enter event occurs
            leftClusterButton.registerComponentToRepaint(rightClusterButton);
            rightClusterButton.registerComponentToRepaint(leftClusterButton);

            // Connection left
            graphics2D.setColor(clusterColor);
            graphics2D.fillRect(LEFT_OFFSET + (int) (leftBarrierXPos + barrierWidth) - 1 //The -1 is a correction due to accuracy problems in floating point
                    // arithmetics
                    , TOP_OFFSET + (int) (clusterYToDraw + (clusterHeight / 2 - clusterConnectionHeight / 2))
                    , (int) connectionWidth + 1 //The +1 is a correction due to accuracy problems in floating point arithmetics
                    , (int) clusterConnectionHeight);
            // Connection right
            final double rightConnectionXPos = rightBarrierXPos - connectionWidth;
            graphics2D.fillRect(LEFT_OFFSET + (int) (rightConnectionXPos) - 2 // The -2 is a correction due to accuracy problems in floating point
                    // arithmetics
                    , TOP_OFFSET + (int) (clusterYToDraw + (clusterHeight / 2 - clusterConnectionHeight / 2))
                    , (int) connectionWidth + 2 // The +2 is a correction due to accuracy problems in floating point arithmetics
                    , (int) clusterConnectionHeight);

            if (!clusterPropertiesPresent.getOrDefault(threadCluster, false))
            {
                final VisualThreadClusterProperties visualThreadClusterProperties =
                        new VisualThreadClusterPropertiesBuilder(threadCluster)
                                .setColor(clusterColor)
                                .setPosition(clusterNum)
                                .get();
                clusterPropertiesManager.registerProperties(visualThreadClusterProperties);
            }

            clusterY -= clusterDistance + clusterHeight;
            clusterNum += 1;
        }

        paintChildren(g);
//        for (final Component component : getComponents())
//        {
//            component.repaint();
//        }
        synchronized (doubleRepaintLock)
        {
            doubleRepaintLock.notifyAll();
        }
    }
}
