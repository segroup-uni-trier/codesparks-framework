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
import de.unitrier.st.codesparks.core.visualization.popup.ThreadClusterJBPanel;
import de.unitrier.st.codesparks.core.visualization.popup.ThreadColor;

import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

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
        final int width = Math.min(maxWidth, this.getWidth());
        final int height = Math.min(maxHeight, this.getHeight());
        final int horizontalMargin = 10;
        final int verticalMargin = 10;
        System.out.println("ZoomedThreadFork: height=" + height + ", width=" + width + ", vizHeight=" + String.valueOf(height - 2 * verticalMargin) + ", " +
                "vizWidth=" + String.valueOf(width - 2 * horizontalMargin));


        final Graphics2D graphics2D = (Graphics2D) g;
        graphics2D.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        //graphics2D.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0f));

//-------------
        graphics2D.setColor(JBColor.RED);
        final int middleStrokeWidth = 4;
        graphics2D.setStroke(new BasicStroke(middleStrokeWidth));
        // graphics2D.drawLine(width / 2 - (int)Math.max(middleStrokeWidth / 2d, 1), 0, width / 2 - (int)Math.max(middleStrokeWidth / 2d, 1), height);
//-------------
        graphics2D.setColor(JBColor.BLUE);
        final int outStrokeWidth = 1;
        graphics2D.setStroke(new BasicStroke(outStrokeWidth));
        graphics2D.drawLine((int) (width / 8d) /*- (int) Math.max(outStrokeWidth / 2d, 1)*/, 0, (int) (width / 8d) /*- (int) Math.max(outStrokeWidth / 2d, 1)*/,
                height);
        graphics2D.drawLine((int) (width / 8d * 7) - (int) Math.max(outStrokeWidth / 2d, 1), 0, (int) (width / 8d * 7) - (int) Math.max(outStrokeWidth / 2d
                , 1), height);
//-------------



        /*
         * Calculation of the positions
         */

        final int threadForkSymbolWith = (int) ((width - 2 * horizontalMargin) / 8d); // 1/2 * 1/4

        final int barrierWidth = (int) (threadForkSymbolWith * 0.15);
        final int arrowHeight = (int) (height * 0.03);


        graphics2D.setColor(VisConstants.BORDER_COLOR);

        // Arrow left
        final int leftBarrierXPos = horizontalMargin + threadForkSymbolWith - barrierWidth;
        final int arrowWidth = leftBarrierXPos - horizontalMargin;
        graphics2D.fillRect(horizontalMargin, height / 2 - arrowHeight / 2, arrowWidth, arrowHeight);
        final int leftArrowPartXPos = horizontalMargin + (int) (arrowWidth * 5d / 6);
        graphics2D.setStroke(new BasicStroke((float) arrowHeight));
        graphics2D.drawLine(leftArrowPartXPos, (int) (height / 8d * 3), leftBarrierXPos, height / 2 - arrowHeight / 2);
        graphics2D.drawLine(leftArrowPartXPos, (int) (height / 8d * 5), leftBarrierXPos, height / 2 + arrowHeight / 2);

        // Left barrier
        graphics2D.fillRect(leftBarrierXPos, verticalMargin, barrierWidth, height - 2 * verticalMargin);

        // Arrow right
        final int rightBarrierXPos = width - threadForkSymbolWith - horizontalMargin;
        graphics2D.fillRect(rightBarrierXPos + barrierWidth, height / 2 - arrowHeight / 2, arrowWidth, arrowHeight);
        final int rightArrowPartXPos = width - leftArrowPartXPos;//threadForkSymbolWith * 3 / 4 * 4 / 5;
        graphics2D.setStroke(new BasicStroke((float) arrowHeight));
        graphics2D.drawLine(rightArrowPartXPos, (int) (height / 8d * 3), rightBarrierXPos + barrierWidth, height / 2 - arrowHeight / 2);
        graphics2D.drawLine(rightArrowPartXPos, (int) (height / 8d * 5), rightBarrierXPos + barrierWidth, height / 2 + arrowHeight / 2);

        // Right barrier
        graphics2D.fillRect(rightBarrierXPos, verticalMargin, barrierWidth, height - 2 * verticalMargin);


        // The cluster visualization area on the left
        final int threadVizWidth = (int) ((width - 2 * horizontalMargin) * 3d / 8);
        final int rectVizWidth = (int) (threadVizWidth * 0.8);
        final int leftThreadRectXPos = leftBarrierXPos + barrierWidth + (int) (threadVizWidth * 0.2);

        final int strokeWidth = 2;
        final int halfStrokeWidth = (int) (strokeWidth / 2d);

        graphics2D.setStroke(new BasicStroke(strokeWidth));
        graphics2D.drawRect(leftThreadRectXPos - strokeWidth, verticalMargin - strokeWidth,
                rectVizWidth + halfStrokeWidth, height - 2 * verticalMargin + 2 * strokeWidth);

        // The cluster visualization area on the right
        final int rightThreadRectXPos = width - leftThreadRectXPos - rectVizWidth;//leftBarrierXPos + barrierWidth + (int) (threadVizWidth * 1d / 3);
        graphics2D.drawRect(rightThreadRectXPos - strokeWidth - halfStrokeWidth, verticalMargin - strokeWidth, rectVizWidth + halfStrokeWidth,
                height - 2 * verticalMargin + 2 * strokeWidth);


        /*
         * Draw the clusters
         */
        final int nrOfClusters = Math.max(3, threadArtifactClustering.size());

        /*
            let x be the space between the clusters resp. the margins to the top and bottom and y be the cluster width:

            (nr + 1) * x + nr * y = height
                    3 * x = y
         */

        final int clusterDistance = (int) ((height - (2 * verticalMargin)) / (double) (4 * nrOfClusters + 1));
        final int clusterHeight = 3 * clusterDistance;

        System.out.println("ZoomedThreadFork: clusterDistance=" + clusterDistance + ", clusterHeight=" + clusterHeight);

        final VisualThreadClusterPropertiesManager clusterPropertiesManager = VisualThreadClusterPropertiesManager.getInstance();

        final int clusterYBase = verticalMargin + clusterDistance;
        int clusterY = clusterYBase;
        final int clusterX = (int) (leftThreadRectXPos + 0.05 * threadVizWidth);

        final int clusterConnectionHeight = (int) (clusterHeight / 3d);
        final int connectionWidth = Math.abs(clusterX - (leftBarrierXPos + barrierWidth));


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

            final ThreadClusterJBPanel clusterJBPanel = new ThreadClusterJBPanel(0);
            clusterJBPanel.setBackground(clusterColor);

            int clusterYToDraw = clusterY;
//            if (clusterPosition > -1)
//            {
//                clusterYToDraw = clusterYBase + clusterPosition * (clusterDistance + clusterHeight);
//            }

//            clusterJBPanel.setBounds(clusterX, clusterYToDraw, (int) (threadVizWidth * 0.9), clusterHeight);
//            add(clusterJBPanel);
//            clusterJBPanel.addMouseMotionListener(new MouseAdapter()
//            {
//                @Override
//                public void mouseEntered(final MouseEvent e)
//                {
//                    super.mouseEntered(e);
//                    clusterJBPanel.setMouseIn(true);
//                }
//
//                @Override
//                public void mouseExited(final MouseEvent e)
//                {
//                    super.mouseExited(e);
//                    clusterJBPanel.setMouseIn(false);
//                }
//            });


            graphics2D.setColor(clusterColor);
            graphics2D.fillRect(leftBarrierXPos + barrierWidth, clusterYToDraw + (clusterHeight / 2 - clusterConnectionHeight / 2), connectionWidth,
                    clusterConnectionHeight);

            clusterY += clusterDistance + clusterHeight;
            clusterNumber += 1;
        }


        paintChildren(g);
        for (final Component component : getComponents())
        {
            component.repaint();
        }
    }
}
