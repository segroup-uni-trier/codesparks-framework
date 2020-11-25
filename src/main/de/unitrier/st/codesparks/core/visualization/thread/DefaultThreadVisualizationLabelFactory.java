package de.unitrier.st.codesparks.core.visualization.thread;

import com.intellij.ui.JBColor;
import com.intellij.ui.paint.PaintUtil;
import com.intellij.util.ui.UIUtil;
import de.unitrier.st.codesparks.core.data.AArtifact;
import de.unitrier.st.codesparks.core.data.ACodeSparksThread;
import de.unitrier.st.codesparks.core.data.IMetricIdentifier;
import de.unitrier.st.codesparks.core.visualization.AArtifactVisualizationLabelFactory;
import de.unitrier.st.codesparks.core.visualization.VisConstants;
import de.unitrier.st.codesparks.core.visualization.VisualizationUtil;
import de.unitrier.st.codesparks.core.visualization.popup.ThreadColor;
import de.unitrier.st.codesparks.core.data.CodeSparksThreadCluster;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.List;

import static com.intellij.ui.JBColor.WHITE;

public class DefaultThreadVisualizationLabelFactory extends AArtifactVisualizationLabelFactory
{
    @SuppressWarnings("unused")
    public DefaultThreadVisualizationLabelFactory(final IMetricIdentifier primaryMetricIdentifier)
    {
        super(primaryMetricIdentifier);
    }

    public DefaultThreadVisualizationLabelFactory(final IMetricIdentifier primaryMetricIdentifier, final int sequence)
    {
        super(primaryMetricIdentifier, sequence);
    }

    @Override
    public JLabel createArtifactLabel(
            @NotNull final AArtifact artifact
    )
    {
        final int threadsPerColumn = 3;
        int lineHeight = VisualizationUtil.getLineHeightCeil(VisConstants.getLineHeight(), threadsPerColumn);
        int width = 5000;
        GraphicsConfiguration defaultConfiguration =
                GraphicsEnvironment.getLocalGraphicsEnvironment().getDefaultScreenDevice().getDefaultConfiguration();
        BufferedImage bi = UIUtil.createImage(defaultConfiguration, width, lineHeight,
                BufferedImage.TYPE_INT_RGB, PaintUtil.RoundingMode.CEIL);
        Graphics graphics = bi.getGraphics();
        graphics.setColor(WHITE);
        graphics.fillRect(0, 0, width, lineHeight);
        /*
         * Draw the threads
         */
        int threadDotXPos = 2;
        final int threadSquareEdgeLength = (lineHeight - 6) / threadsPerColumn;
        int threadSquareYPos = lineHeight - threadSquareEdgeLength - 2;
        final int threadSquareOffset = threadSquareEdgeLength + 1;

        int totalThreadCnt = 0;

        List<CodeSparksThreadCluster> codeSparksThreadClustering = artifact.getSortedDefaultThreadArtifactClustering(primaryMetricIdentifier);

        for (int i = 0; i < codeSparksThreadClustering.size(); i++)
        {
            JBColor color = ThreadColor.getNextColor(i);
            CodeSparksThreadCluster cluster = codeSparksThreadClustering.get(i);

            VisualThreadClusterProperties clusterProperties = new VisualThreadClusterProperties(cluster, color);
            VisualThreadClusterPropertiesManager propertiesManager = VisualThreadClusterPropertiesManager.getInstance();
            propertiesManager.registerProperties(clusterProperties);

            graphics.setColor(color);
            //int size = cluster.size();
            for (ACodeSparksThread codeSparksThread : cluster)
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

//        for (Map<String, Double> map : artifact.getCategoryThreadMetricValueMap().values())
//        {
//            graphics.setColor(ThreadColor.getNextColor(color++));
//            int len = map.size();
//            for (int i = 0; i < len; i++)
//            {
//                graphics.fillRect(threadDotXPos + (totalThreadCnt / 3)
//                                * threadSquareOffset, threadSquareYPos
//                                - (totalThreadCnt % 3) * threadSquareOffset,
//                        threadSquareEdgeLength, threadSquareEdgeLength);
//                totalThreadCnt++;
//            }
//        }

        Rectangle threadVisualisationArea = new Rectangle(
                threadDotXPos - 2, 0, ((threadSquareOffset)
                * ((totalThreadCnt - 1) / 3) + 1)
                + threadSquareOffset + 1, lineHeight - 1);
        graphics.setColor(VisConstants.BORDER_COLOR);

        VisualizationUtil.drawRectangle(graphics, threadVisualisationArea);

        int actualIconWidth = threadDotXPos + threadVisualisationArea.width;
        BufferedImage subimage = bi.getSubimage(0, 0, actualIconWidth, bi.getHeight());
        ImageIcon imageIcon = new ImageIcon(subimage);

        JLabel jLabel = new JLabel();
        jLabel.setIcon(imageIcon);

        jLabel.setSize(imageIcon.getIconWidth(), imageIcon.getIconHeight());
        //jLabel.addMouseListener(new DefaultArtifactVisualizationMouseListener(jLabel, artifact));
        jLabel.addMouseListener(new DefaultThreadVisualizationMouseListener(jLabel, artifact, primaryMetricIdentifier));

        return jLabel;
    }
}
