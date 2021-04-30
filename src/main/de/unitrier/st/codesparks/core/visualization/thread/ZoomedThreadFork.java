/*
 * Copyright (c) 2021. Oliver Moseler
 */

package de.unitrier.st.codesparks.core.visualization.thread;

import com.intellij.ui.JBColor;
import com.intellij.ui.components.JBPanel;
import com.intellij.util.ui.components.BorderLayoutPanel;
import de.unitrier.st.codesparks.core.data.AArtifact;
import de.unitrier.st.codesparks.core.data.ThreadArtifactCluster;
import de.unitrier.st.codesparks.core.data.ThreadArtifactClustering;
import de.unitrier.st.codesparks.core.visualization.VisConstants;
import de.unitrier.st.codesparks.core.visualization.popup.ThreadClusterButton;
import de.unitrier.st.codesparks.core.visualization.popup.ThreadColor;

import java.awt.*;

public class ZoomedThreadFork extends JBPanel<BorderLayoutPanel>
{
    private final AArtifact artifact;
    private final ThreadArtifactClustering threadArtifactClustering;

    public ZoomedThreadFork(
            final AArtifact artifact
            , final ThreadArtifactClustering threadArtifactClustering
    )
    {
        this.artifact = artifact;
        this.threadArtifactClustering = threadArtifactClustering;
        this.setLayout(null);
        final Dimension dimension = new Dimension(maxWidth, maxHeight);
        this.setPreferredSize(dimension);
        this.setMinimumSize(dimension);
        //this.setMaximumSize(dimension);
    }

    private final int maxWidth = 480;
    private final int maxHeight = 150;

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
        graphics2D.drawLine(LEFT_OFFSET + (int) (width / 8d) /*- (int) Math.max(outStrokeWidth / 2d, 1)*/
                , TOP_OFFSET
                , LEFT_OFFSET + (int) (width / 8d) /*- (int) Math.max(outStrokeWidth / 2d, 1)*/
                , TOP_OFFSET + height);
        graphics2D.drawLine(LEFT_OFFSET + (int) (width / 8d * 7) - (int) Math.max(outStrokeWidth / 2d, 1)
                , TOP_OFFSET
                , LEFT_OFFSET + (int) (width / 8d * 7) - (int) Math.max(outStrokeWidth / 2d, 1)
                , TOP_OFFSET + height);
//-------------



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
        graphics2D.fillRect(LEFT_OFFSET + (int) (rightBarrierXPos + barrierWidth)
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
                , (int) (rectVizWidth + halfStrokeWidth)
                , height - 2 * verticalMargin + 2 * strokeWidth);

        // The cluster visualization area on the right
        double rightThreadRectXPos = width - leftThreadRectXPos - rectVizWidth;//leftBarrierXPos + barrierWidth + (int) (threadVizWidth * 1d / 3);
        rightThreadRectXPos = (rightThreadRectXPos - strokeWidth - halfStrokeWidth);
        graphics2D.drawRect(LEFT_OFFSET + (int) rightThreadRectXPos
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

        final VisualThreadClusterPropertiesManager clusterPropertiesManager = VisualThreadClusterPropertiesManager.getInstance();

        final double clusterYBase = verticalMargin + clusterDistance;
        double clusterY = clusterYBase;
        final double leftClusterX = (leftThreadRectXPos + 0.05 * threadVizWidth);
        final double rightClusterX = rightThreadRectXPos + 0.05 * threadVizWidth;
        final double clusterButtonWidth = rectVizWidth * 0.9;

        final double clusterConnectionHeight = clusterHeight / 3d;
        final double connectionWidth = Math.abs(leftClusterX - (leftBarrierXPos + barrierWidth));

        int clusterNumber = 0;
        for (final ThreadArtifactCluster cluster : threadArtifactClustering)
        {
            if (cluster.isEmpty()) continue;

            int clusterPosition = -1;
            JBColor clusterColor = ThreadColor.getNextColor(clusterNumber);
//            final VisualThreadClusterProperties properties = clusterPropertiesManager.getProperties(cluster);
//            if (properties != null)
//            {
//                final JBColor color = properties.getColor();
//                if (color != null) clusterColor = color;
//                clusterPosition = properties.getPosition();
//            }

            double clusterYToDraw = clusterY;
            if (clusterPosition > -1)
            {
                clusterYToDraw = clusterYBase + clusterPosition * (clusterDistance + clusterHeight);
            }

            // Cluster button left
            final ThreadClusterButton leftClusterButton = new ThreadClusterButton(0);
            leftClusterButton.setBackground(clusterColor);
            leftClusterButton.setBounds(LEFT_OFFSET + (int) leftClusterX
                    , TOP_OFFSET + (int) clusterYToDraw
                    , (int) clusterButtonWidth
                    , (int) clusterHeight);
            add(leftClusterButton);
            // Cluster button right
            final ThreadClusterButton rightClusterButton = new ThreadClusterButton(0);
            rightClusterButton.setBackground(clusterColor);
            rightClusterButton.setBounds(LEFT_OFFSET + (int) rightClusterX - 2 // The -2 is a correction due to accuracy problems in floating point arithmetics
                    , TOP_OFFSET + (int) clusterYToDraw
                    , (int) clusterButtonWidth + 1 // The +1 is a correction due to accuracy problems in floating point arithmetics
                    , (int) clusterHeight);
            add(rightClusterButton);


            // Connection left
            graphics2D.setColor(clusterColor);
            graphics2D.fillRect(LEFT_OFFSET + (int) (leftBarrierXPos + barrierWidth)
                    , TOP_OFFSET + (int) (clusterYToDraw + (clusterHeight / 2 - clusterConnectionHeight / 2))
                    , (int) connectionWidth
                    , (int) clusterConnectionHeight);
            // Connection right
            final double rightConnectionXPos = rightBarrierXPos - connectionWidth;
            graphics2D.fillRect(LEFT_OFFSET + (int) (rightConnectionXPos) - 2 // The -2 is a correction due to accuracy problems in floating point arithmetics
                    , TOP_OFFSET + (int) (clusterYToDraw + (clusterHeight / 2 - clusterConnectionHeight / 2))
                    , (int) connectionWidth + 2 // The +2 is a correction due to accuracy problems in floating point arithmetics
                    , (int) clusterConnectionHeight);


            clusterY += clusterDistance + clusterHeight;
            clusterNumber += 1;
        }

        paintChildren(g);
//        for (final Component component : getComponents())
//        {
//            component.repaint();
//        }
    }
}
