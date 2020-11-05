package de.unitrier.st.insituprofiling.core.visualization.callee;

import com.intellij.ui.JBColor;
import com.intellij.ui.paint.PaintUtil;
import com.intellij.util.ui.UIUtil;
import de.unitrier.st.insituprofiling.core.CoreUtil;
import de.unitrier.st.insituprofiling.core.data.*;
import de.unitrier.st.insituprofiling.core.visualization.VisConstants;
import de.unitrier.st.insituprofiling.core.visualization.VisualizationUtil;
import de.unitrier.st.insituprofiling.core.visualization.popup.ThreadColor;
import de.unitrier.st.insituprofiling.core.visualization.thread.VisualThreadArtifactClusterProperties;
import de.unitrier.st.insituprofiling.core.visualization.thread.VisualThreadArtifactClusterPropertiesManager;

import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.List;
import java.util.*;
import java.util.stream.Collectors;

import static de.unitrier.st.insituprofiling.core.visualization.VisConstants.BORDER_COLOR;

@SuppressWarnings("unused")
public class ArtifactCalleeThreadDotVisualizationLabelFactory extends AArtifactCalleeVisualizationLabelFactory
{
    public ArtifactCalleeThreadDotVisualizationLabelFactory() {}

    public ArtifactCalleeThreadDotVisualizationLabelFactory(int sequence)
    {
        super(sequence);
    }

    @Override
    public JLabel createArtifactCalleeLabel(AProfilingArtifact artifact
            , List<ANeighborProfilingArtifact> threadFilteredNeighborArtifactsOfLine
            , double threadFilteredMetricValue
            , Color metricColor
    )
    {
        List<ThreadArtifactCluster> threadClusters =
                artifact.getDefaultThreadArtifactClustering()
                        .stream()
                        .sorted(ThreadArtifactClusterComparator.getInstance())
                        .filter(cluster -> !cluster.isEmpty())
                        .collect(Collectors.toList());

        SortedMap<ThreadArtifactCluster, Set<String>> artifactClusterSets =
                new TreeMap<>(ThreadArtifactClusterComparator.getInstance());
        SortedMap<ThreadArtifactCluster, Set<ThreadArtifact>> neighborClusterSets =
                new TreeMap<>(ThreadArtifactClusterComparator.getInstance());

        for (ThreadArtifactCluster threadCluster : threadClusters)
        {
            artifactClusterSets.put(threadCluster,
                    new HashSet<>(threadCluster.stream().map(ThreadArtifact::getIdentifier).collect(Collectors.toList())));
            neighborClusterSets.put(threadCluster, new HashSet<>());
        }

        for (ANeighborProfilingArtifact neighborArtifact : threadFilteredNeighborArtifactsOfLine)
        {
            for (ThreadArtifact neighborThreadArtifact :
                    neighborArtifact.getThreadArtifacts()
                            .stream()
                            .filter(threadArtifact -> !threadArtifact.isFiltered())
                            .collect(Collectors.toList()))
            {
                String threadArtifactIdentifier = neighborThreadArtifact.getIdentifier();
                for (Map.Entry<ThreadArtifactCluster, Set<String>> artifactClusterSetEntry : artifactClusterSets.entrySet())
                {
                    if (artifactClusterSetEntry.getValue().contains(threadArtifactIdentifier))
                    {
                        neighborClusterSets.get(artifactClusterSetEntry.getKey()).add(neighborThreadArtifact);
                    }
                }
            }
        }

        final int X_OFFSET_LEFT = 1;
        final int X_OFFSET_RIGHT = 2;

        final int visWidth = 7;

        final int threadsPerColumn = 3;
        final int lineHeight = VisualizationUtil.getLineHeightCeil(VisConstants.getLineHeight(), threadsPerColumn);

        final int threadSquareEdgeLength = (lineHeight - 6) / threadsPerColumn;

        final int BORDER_RECTANGLE_STROKE_WIDTH = 1;
        final int THREAD_DOT_X_OFFSET = X_OFFSET_LEFT + BORDER_RECTANGLE_STROKE_WIDTH + 1;

        int threadSquareYPos = lineHeight - threadSquareEdgeLength - 2;
        final int threadSquareOffset = threadSquareEdgeLength + 1;

        int totalWidth = X_OFFSET_LEFT + visWidth + X_OFFSET_RIGHT;

        GraphicsConfiguration defaultConfiguration =
                GraphicsEnvironment.getLocalGraphicsEnvironment().getDefaultScreenDevice().getDefaultConfiguration();
        BufferedImage bi = UIUtil.createImage(defaultConfiguration, totalWidth, lineHeight, BufferedImage.TYPE_INT_RGB,
                PaintUtil.RoundingMode.CEIL);
        Graphics graphics = bi.getGraphics();

        Color backgroundColor = CoreUtil.getSelectedFileEditorBackgroundColor();

        graphics.setColor(backgroundColor);
        graphics.fillRect(0, 0, totalWidth, lineHeight);

        Rectangle threadVisualisationArea = new Rectangle(
                X_OFFSET_LEFT, 0, visWidth, lineHeight - 1);
        graphics.setColor(BORDER_COLOR);

        VisualizationUtil.drawRectangle(graphics, threadVisualisationArea);
        int clusterCnt = 0;
        VisualThreadArtifactClusterPropertiesManager clusterPropertiesManager = VisualThreadArtifactClusterPropertiesManager.getInstance();
        for (Map.Entry<ThreadArtifactCluster, Set<ThreadArtifact>> threadArtifactClusterSetEntry : neighborClusterSets.entrySet())
        {
            ThreadArtifactCluster cluster = threadArtifactClusterSetEntry.getKey();
            VisualThreadArtifactClusterProperties properties = clusterPropertiesManager.getProperties(cluster);
            JBColor color;
            if (properties != null)
            {
                color = properties.getColor();
            } else
            {
                color = ThreadColor.getNextColor(clusterCnt++);
            }

            if (threadArtifactClusterSetEntry.getValue().size() > 0)
            {
                graphics.setColor(color);
                graphics.fillRect(THREAD_DOT_X_OFFSET, threadSquareYPos,
                        threadSquareEdgeLength, threadSquareEdgeLength);
            }
            threadSquareYPos -= threadSquareOffset;
        }

        BufferedImage subimage = bi.getSubimage(0, 0, bi.getWidth(), bi.getHeight());
        ImageIcon imageIcon = new ImageIcon(subimage);

        JLabel jLabel = new JLabel();
        jLabel.setIcon(imageIcon);

        jLabel.setSize(imageIcon.getIconWidth(), imageIcon.getIconHeight());
        // jLabel.addMouseListener(??);

        return jLabel;
    }
}
