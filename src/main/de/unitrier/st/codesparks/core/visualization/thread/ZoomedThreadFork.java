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
        final int width = this.getWidth();
        final int height = this.getHeight();


        final Graphics2D graphics2D = (Graphics2D) g;

        //graphics2D.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0f));
        graphics2D.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        final int horizontalMargin = 10;
        final int verticalMargin = 10;

        final int threadForkSymbolWith = (int) (width / 4d);

        final int barrierWidth = (int) (threadForkSymbolWith * 0.2);
        final int barrierXPos = threadForkSymbolWith - barrierWidth;
        final int arrowHeight = (int) (height * 0.04);

        final int nrOfClusters = Math.max(3, threadArtifactClustering.size());

        /*
            let x be the space between the clusters resp. the margins to the top and bottom and y be the cluster width:

            (nr + 1) * x + nr * y = height
                    3 * x = y
         */

        final int clusterDistance = (int) (height / (4d * nrOfClusters + 1));
        final int clusterHeight = 3 * clusterDistance;
        graphics2D.setColor(VisConstants.BORDER_COLOR);

        // Leading arrow
        graphics2D.fillRect(horizontalMargin, height / 2 - arrowHeight / 2, barrierXPos, arrowHeight);
        final int arrowPartXPos = threadForkSymbolWith * 3 / 4 * 4 / 5;
        graphics2D.setStroke(new BasicStroke((float) arrowHeight));
        graphics2D.drawLine(arrowPartXPos, (int) (height / 8d * 3), barrierXPos, height / 2 - arrowHeight / 2);
        graphics2D.drawLine(arrowPartXPos, (int) (height / 8d * 5), barrierXPos, height / 2 + arrowHeight / 2);

        // Vertical bar or barrier, respectively
        //final int barrierWidth = arrowHeight * 2;
        graphics2D.fillRect(barrierXPos, verticalMargin, barrierWidth, height - verticalMargin);

        // The actual cluster visualization area
        final int threadVizXPos = (int) (width / 4d + (width / 4d * 3 * 1 / 6));
        final int threadVizWidth = (int) (width / 4d * 3 * 5 / 6);
        final int strokeWidth = 2;
        graphics2D.setStroke(new BasicStroke(strokeWidth));
        graphics2D.drawRect(threadVizXPos - strokeWidth, verticalMargin - strokeWidth, threadVizWidth, height - verticalMargin + strokeWidth);

        /*
         * Draw the clusters
         */
        final VisualThreadClusterPropertiesManager clusterPropertiesManager = VisualThreadClusterPropertiesManager.getInstance();

        final int clusterYBase = verticalMargin + clusterDistance;
        int clusterY = clusterYBase;
        final int clusterX = (int) (threadVizXPos + 0.05 * threadVizWidth);

        final int clusterConnectionHeight = clusterHeight / 2;
        final int connectionWidth = Math.abs(clusterX - (barrierXPos + barrierWidth));


        int clusterNumber = 0;
        for (final ThreadArtifactCluster cluster : threadArtifactClustering)
        {
            if (cluster.isEmpty()) continue;

            int clusterPosition = -1;
            JBColor clusterColor = ThreadColor.getNextColor(clusterNumber);
            final VisualThreadClusterProperties properties = clusterPropertiesManager.getProperties(cluster);
            if (properties != null)
            {
                final JBColor color = properties.getColor();
                if (color != null) clusterColor = color;
                clusterPosition = properties.getPosition();
            }

            final ThreadClusterJBPanel clusterJBPanel = new ThreadClusterJBPanel(0);
            clusterJBPanel.setBackground(clusterColor);
            clusterJBPanel.setBounds(clusterX, clusterY, (int) (threadVizWidth * 0.9), clusterHeight);
            add(clusterJBPanel);
            clusterJBPanel.addMouseMotionListener(new MouseAdapter()
            {
                @Override
                public void mouseEntered(final MouseEvent e)
                {
                    super.mouseEntered(e);
                    clusterJBPanel.setMouseIn(true);
                }

                @Override
                public void mouseExited(final MouseEvent e)
                {
                    super.mouseExited(e);
                    clusterJBPanel.setMouseIn(false);
                }
            });

            int clusterYToDraw = clusterY;
            if (clusterPosition > -1)
            {
                clusterYToDraw = clusterYBase + clusterPosition * (clusterDistance + clusterHeight);
            }

            graphics2D.setColor(clusterColor);
            graphics2D.fillRect(barrierXPos + barrierWidth, clusterYToDraw + (clusterHeight / 2 - clusterConnectionHeight / 2), connectionWidth,
                    clusterConnectionHeight);

            clusterY += clusterDistance + clusterHeight;
            clusterNumber += 1;
        }


        for (final Component component : getComponents())
        {
            component.repaint();
        }
    }
}
