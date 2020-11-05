package de.unitrier.st.insituprofiling.core.visualization.thread;

import com.intellij.ui.JBColor;
import com.intellij.ui.paint.PaintUtil;
import com.intellij.util.ui.UIUtil;
import de.unitrier.st.insituprofiling.core.data.AProfilingArtifact;
import de.unitrier.st.insituprofiling.core.data.ThreadArtifact;
import de.unitrier.st.insituprofiling.core.data.ThreadArtifactCluster;
import de.unitrier.st.insituprofiling.core.visualization.AArtifactVisualizationLabelFactory;
import de.unitrier.st.insituprofiling.core.visualization.VisConstants;
import de.unitrier.st.insituprofiling.core.visualization.VisualizationUtil;
import de.unitrier.st.insituprofiling.core.visualization.popup.ThreadColor;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.List;

import static com.intellij.ui.JBColor.WHITE;
import static de.unitrier.st.insituprofiling.core.visualization.VisConstants.BORDER_COLOR;

public class DefaultThreadVisualizationLabelFactory extends AArtifactVisualizationLabelFactory
{
    @SuppressWarnings("unused")
    public DefaultThreadVisualizationLabelFactory() { }

    public DefaultThreadVisualizationLabelFactory(int sequence)
    {
        super(sequence);
    }

    @Override
    public JLabel createArtifactLabel(@NotNull AProfilingArtifact artifact)
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

        List<ThreadArtifactCluster> threadArtifactClustering = artifact.getSortedDefaultThreadArtifactClustering();

        for (int i = 0; i < threadArtifactClustering.size(); i++)
        {
            JBColor color = ThreadColor.getNextColor(i);
            ThreadArtifactCluster cluster = threadArtifactClustering.get(i);

            VisualThreadArtifactClusterProperties clusterProperties = new VisualThreadArtifactClusterProperties(cluster, color);
            VisualThreadArtifactClusterPropertiesManager propertiesManager = VisualThreadArtifactClusterPropertiesManager.getInstance();
            propertiesManager.registerProperties(clusterProperties);

            graphics.setColor(color);
            //int size = cluster.size();
            for (ThreadArtifact threadArtifact : cluster)
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
        graphics.setColor(BORDER_COLOR);

        VisualizationUtil.drawRectangle(graphics, threadVisualisationArea);

        int actualIconWidth = threadDotXPos + threadVisualisationArea.width;
        BufferedImage subimage = bi.getSubimage(0, 0, actualIconWidth, bi.getHeight());
        ImageIcon imageIcon = new ImageIcon(subimage);

        JLabel jLabel = new JLabel();
        jLabel.setIcon(imageIcon);

        jLabel.setSize(imageIcon.getIconWidth(), imageIcon.getIconHeight());
        //jLabel.addMouseListener(new DefaultArtifactVisualizationMouseListener(jLabel, artifact));
        jLabel.addMouseListener(new DefaultThreadVisualizationMouseListener(jLabel, artifact));

        return jLabel;
    }
}
