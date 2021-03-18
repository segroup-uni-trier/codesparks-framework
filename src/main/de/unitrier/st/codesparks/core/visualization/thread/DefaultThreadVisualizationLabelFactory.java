package de.unitrier.st.codesparks.core.visualization.thread;

import com.intellij.ui.JBColor;
import com.intellij.ui.paint.PaintUtil;
import com.intellij.util.ui.UIUtil;
import de.unitrier.st.codesparks.core.data.AArtifact;
import de.unitrier.st.codesparks.core.data.AThreadArtifact;
import de.unitrier.st.codesparks.core.data.AMetricIdentifier;
import de.unitrier.st.codesparks.core.data.ThreadArtifactCluster;
import de.unitrier.st.codesparks.core.visualization.AArtifactVisualizationLabelFactory;
import de.unitrier.st.codesparks.core.visualization.VisConstants;
import de.unitrier.st.codesparks.core.visualization.VisualizationUtil;
import de.unitrier.st.codesparks.core.visualization.popup.ThreadColor;

import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.List;

/*
 * Copyright (c), Oliver Moseler, 2020
 */
public class DefaultThreadVisualizationLabelFactory extends AArtifactVisualizationLabelFactory
{
    public DefaultThreadVisualizationLabelFactory(final AMetricIdentifier primaryMetricIdentifier)
    {
        super(primaryMetricIdentifier);
    }

    public DefaultThreadVisualizationLabelFactory(final AMetricIdentifier primaryMetricIdentifier, final int sequence)
    {
        super(primaryMetricIdentifier, sequence);
    }

    @Override
    public JLabel createArtifactLabel(
            final AArtifact artifact
    )
    {
        final int threadsPerColumn = 3;
        int lineHeight = VisualizationUtil.getLineHeightCeil(VisConstants.getLineHeight(), threadsPerColumn);
        int width = 5000; // We don't know the width yet because the viz does not visually scale.
        GraphicsConfiguration defaultConfiguration =
                GraphicsEnvironment.getLocalGraphicsEnvironment().getDefaultScreenDevice().getDefaultConfiguration();
        BufferedImage bi = UIUtil.createImage(defaultConfiguration, width, lineHeight,
                BufferedImage.TYPE_INT_ARGB, PaintUtil.RoundingMode.CEIL);
        Graphics2D graphics = (Graphics2D) bi.getGraphics();
        VisualizationUtil.drawTransparentBackground(graphics, bi);
        /*
         * Draw the threads
         */
        int threadDotXPos = 2;
        final int threadSquareEdgeLength = (lineHeight - 6) / threadsPerColumn;
        int threadSquareYPos = lineHeight - threadSquareEdgeLength - 2;
        final int threadSquareOffset = threadSquareEdgeLength + 1;

        int totalThreadCnt = 0;

        List<ThreadArtifactCluster> threadArtifactClustering = artifact.getSortedDefaultThreadArtifactClustering(primaryMetricIdentifier);

        for (int i = 0; i < threadArtifactClustering.size(); i++)
        {
            JBColor color = ThreadColor.getNextColor(i);
            ThreadArtifactCluster cluster = threadArtifactClustering.get(i);

//            VisualThreadClusterProperties clusterProperties = new VisualThreadClusterProperties(cluster, color);
            final VisualThreadClusterProperties clusterProperties = new VisualThreadClusterPropertiesBuilder(cluster).setColor(color).get();
            VisualThreadClusterPropertiesManager propertiesManager = VisualThreadClusterPropertiesManager.getInstance();
            propertiesManager.registerProperties(clusterProperties);

            graphics.setColor(color);
            //int size = cluster.size();
            for (AThreadArtifact codeSparksThread : cluster)
            {
                //boolean filtered = threadArtifact.isFiltered();
                if (codeSparksThread.isFiltered())
                {
                    graphics.setColor(JBColor.GRAY);
                } else
                {
                    graphics.setColor(color);
                }
                graphics.fillRect(threadDotXPos + (totalThreadCnt / 3)
                                * threadSquareOffset, threadSquareYPos
                                - (totalThreadCnt % 3) * threadSquareOffset,
                        threadSquareEdgeLength, threadSquareEdgeLength);
                totalThreadCnt++;
            }
        }

        Rectangle threadVisualisationArea = new Rectangle(
                threadDotXPos - 2, 0, ((threadSquareOffset)
                * ((totalThreadCnt - 1) / 3) + 1)
                + threadSquareOffset + 1, lineHeight - 1);
        graphics.setColor(VisConstants.BORDER_COLOR);

        VisualizationUtil.drawRectangle(graphics, threadVisualisationArea);

        final int iconWidth = threadDotXPos + threadVisualisationArea.width;
        BufferedImage subimage = bi.getSubimage(0, 0, iconWidth, bi.getHeight());
        ImageIcon imageIcon = new ImageIcon(subimage);

        JLabel jLabel = new JLabel();
        jLabel.setIcon(imageIcon);

        jLabel.setSize(imageIcon.getIconWidth(), imageIcon.getIconHeight());
        jLabel.addMouseListener(new DefaultThreadVisualizationMouseListener(jLabel, artifact, primaryMetricIdentifier));

        return jLabel;
    }
}
