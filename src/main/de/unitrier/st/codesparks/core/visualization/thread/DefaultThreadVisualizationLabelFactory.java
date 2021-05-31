/*
 * Copyright (c), Oliver Moseler, 2021
 */
package de.unitrier.st.codesparks.core.visualization.thread;

import com.intellij.ui.JBColor;
import de.unitrier.st.codesparks.core.data.*;
import de.unitrier.st.codesparks.core.visualization.AArtifactVisualizationLabelFactory;
import de.unitrier.st.codesparks.core.visualization.CodeSparksGraphics;
import de.unitrier.st.codesparks.core.visualization.VisConstants;
import de.unitrier.st.codesparks.core.visualization.VisualizationUtil;
import de.unitrier.st.codesparks.core.visualization.popup.ThreadColor;

import javax.swing.*;
import java.awt.*;

@SuppressWarnings("unused")
public class DefaultThreadVisualizationLabelFactory extends AArtifactVisualizationLabelFactory
{
    @SuppressWarnings("unused")
    public DefaultThreadVisualizationLabelFactory(final AMetricIdentifier primaryMetricIdentifier)
    {
        super(primaryMetricIdentifier);
    }

    @SuppressWarnings("unused")
    public DefaultThreadVisualizationLabelFactory(final AMetricIdentifier primaryMetricIdentifier, final int sequence)
    {
        super(primaryMetricIdentifier, sequence);
    }

    @Override
    public JLabel createArtifactLabel(final AArtifact artifact)
    {
        final int threadsPerColumn = 3;
        int lineHeight = VisualizationUtil.getLineHeightCeil(VisConstants.getLineHeight(), threadsPerColumn);
        int width = 5000; // We don't know the width yet because the viz does not visually scale.

        final CodeSparksGraphics graphics = getGraphics(width, lineHeight);

        /*
         * Draw the threads
         */
        int threadDotXPos = 2;
        final int threadSquareEdgeLength = (lineHeight - 6) / threadsPerColumn;
        int threadSquareYPos = lineHeight - threadSquareEdgeLength - 2;
        final int threadSquareOffset = threadSquareEdgeLength + 1;

        int totalThreadCnt = 0;

        final ThreadArtifactClustering clustering =
                artifact.getSelectedClusteringOrApplyAndSelect(ConstraintKMeansWithAMaximumOfThreeClusters.getInstance(primaryMetricIdentifier));

        final VisualThreadClusterPropertiesManager propertiesManager = VisualThreadClusterPropertiesManager.getInstance(clustering);

        for (int i = 0; i < clustering.size(); i++)
        {
            final JBColor color = ThreadColor.getNextColor(i);
            final ThreadArtifactCluster cluster = clustering.get(i);

//            VisualThreadClusterProperties clusterProperties = new VisualThreadClusterProperties(cluster, color);
            final VisualThreadClusterProperties clusterProperties = new VisualThreadClusterPropertiesBuilder(cluster).setColor(color).get();
            propertiesManager.registerProperties(clusterProperties);

            graphics.setColor(color);
            //int size = cluster.size();
            for (final AThreadArtifact threadArtifact : cluster)
            {
                //boolean filtered = threadArtifact.isFiltered();
                if (threadArtifact.isFiltered())
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

        //noinspection ConstantConditions
        final Rectangle threadVisualisationArea = new Rectangle(
                threadDotXPos - 2, 0, ((threadSquareOffset)
                * ((totalThreadCnt - 1) / 3) + 1)
                + threadSquareOffset + 1, lineHeight - 1);


        graphics.drawRectangle(threadVisualisationArea, VisConstants.BORDER_COLOR);

        final int iconWidth = threadDotXPos + threadVisualisationArea.width;
        final JLabel jLabel = makeLabel(graphics, iconWidth);

        jLabel.addMouseListener(new DefaultThreadVisualizationMouseListener(jLabel, artifact, primaryMetricIdentifier));

        return jLabel;
    }
}
