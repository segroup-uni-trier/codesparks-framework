package de.unitrier.st.codesparks.core.visualization.neighbor;

import com.intellij.ui.JBColor;
import com.intellij.ui.paint.PaintUtil;
import com.intellij.util.ui.UIUtil;
import de.unitrier.st.codesparks.core.data.*;
import de.unitrier.st.codesparks.core.visualization.VisConstants;
import de.unitrier.st.codesparks.core.visualization.VisualizationUtil;
import de.unitrier.st.codesparks.core.visualization.popup.ThreadColor;
import de.unitrier.st.codesparks.core.visualization.thread.VisualThreadClusterProperties;
import de.unitrier.st.codesparks.core.visualization.thread.VisualThreadClusterPropertiesManager;

import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.List;
import java.util.*;
import java.util.stream.Collectors;

/*
 * Copyright (c), Oliver Moseler, 2020
 */
@SuppressWarnings("unused")
public class NeighborArtifactThreadDotVisualizationLabelFactory extends ANeighborArtifactVisualizationLabelFactory
{
    public NeighborArtifactThreadDotVisualizationLabelFactory(final AMetricIdentifier primaryMetricIdentifier)
    {
        super(primaryMetricIdentifier);
    }

    public NeighborArtifactThreadDotVisualizationLabelFactory(final AMetricIdentifier primaryMetricIdentifier, final int sequence)
    {
        super(primaryMetricIdentifier, sequence);
    }

    @Override
    public JLabel createNeighborArtifactLabel(
            final AArtifact artifact
            , final List<ANeighborArtifact> threadFilteredNeighborArtifactsOfLine
    )
    {
        Comparator<ThreadArtifactCluster> codeSparksThreadClusterComparator = ThreadArtifactClusterComparator.getInstance(primaryMetricIdentifier);
        List<ThreadArtifactCluster> threadClusters =
                artifact.getDefaultThreadArtifactClustering(primaryMetricIdentifier)
                        .stream()
                        .sorted(codeSparksThreadClusterComparator)
                        .filter(cluster -> !cluster.isEmpty())
                        .collect(Collectors.toList());

        SortedMap<ThreadArtifactCluster, Set<String>> artifactClusterSets =
                new TreeMap<>(codeSparksThreadClusterComparator);
        SortedMap<ThreadArtifactCluster, Set<AThreadArtifact>> neighborClusterSets =
                new TreeMap<>(codeSparksThreadClusterComparator);

        for (ThreadArtifactCluster threadCluster : threadClusters)
        {
            artifactClusterSets.put(threadCluster,
                    new HashSet<>(threadCluster.stream().map(AThreadArtifact::getIdentifier).collect(Collectors.toList())));
            neighborClusterSets.put(threadCluster, new HashSet<>());
        }

        for (ANeighborArtifact neighborArtifact : threadFilteredNeighborArtifactsOfLine)
        {
            for (AThreadArtifact neighborCodeSparksThread :
                    neighborArtifact.getThreadArtifacts()
                            .stream()
                            .filter(threadArtifact -> !threadArtifact.isFiltered())
                            .collect(Collectors.toList()))
            {
                String threadArtifactIdentifier = neighborCodeSparksThread.getIdentifier();
                for (Map.Entry<ThreadArtifactCluster, Set<String>> artifactClusterSetEntry : artifactClusterSets.entrySet())
                {
                    if (artifactClusterSetEntry.getValue().contains(threadArtifactIdentifier))
                    {
                        neighborClusterSets.get(artifactClusterSetEntry.getKey()).add(neighborCodeSparksThread);
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
        BufferedImage bi = UIUtil.createImage(defaultConfiguration, totalWidth, lineHeight, BufferedImage.TYPE_INT_ARGB,
                PaintUtil.RoundingMode.CEIL);
        Graphics2D graphics = (Graphics2D) bi.getGraphics();

        VisualizationUtil.drawTransparentBackground(graphics, bi);

        Rectangle threadVisualisationArea = new Rectangle(
                X_OFFSET_LEFT, 0, visWidth, lineHeight - 1);
        graphics.setColor(VisConstants.BORDER_COLOR);

        VisualizationUtil.drawRectangle(graphics, threadVisualisationArea);
        int clusterCnt = 0;
        VisualThreadClusterPropertiesManager clusterPropertiesManager = VisualThreadClusterPropertiesManager.getInstance();
        for (Map.Entry<ThreadArtifactCluster, Set<AThreadArtifact>> threadArtifactClusterSetEntry : neighborClusterSets.entrySet())
        {
            ThreadArtifactCluster cluster = threadArtifactClusterSetEntry.getKey();
            VisualThreadClusterProperties properties = clusterPropertiesManager.getProperties(cluster);
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
