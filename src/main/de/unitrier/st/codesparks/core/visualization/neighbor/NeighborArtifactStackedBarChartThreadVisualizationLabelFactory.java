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
public class NeighborArtifactStackedBarChartThreadVisualizationLabelFactory extends ANeighborArtifactVisualizationLabelFactory
{
    public NeighborArtifactStackedBarChartThreadVisualizationLabelFactory(final IMetricIdentifier primaryMetricIdentifier)
    {
        super(primaryMetricIdentifier);
    }

    public NeighborArtifactStackedBarChartThreadVisualizationLabelFactory(final IMetricIdentifier primaryMetricIdentifier, final int sequence)
    {
        super(primaryMetricIdentifier, sequence);
    }

    @Override
    public JLabel createArtifactCalleeLabel(
            ACodeSparksArtifact artifact
            , List<ANeighborArtifact> threadFilteredNeighborArtifactsOfLine
    )
    {
        final double totalThreadFilteredCalleeTime = summedThreadMetricValuesOfNeighbors(threadFilteredNeighborArtifactsOfLine);

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

        final int lineHeight = VisualizationUtil.getLineHeightCeil(VisConstants.getLineHeight(), 3);

//        int totalThreads = totalThreads(neighborClusterSets);
        final int X_OFFSET_LEFT = 1;
        final int X_OFFSET_RIGHT = 1;

        final int visWidth = 5;

        final int totalWidth = X_OFFSET_LEFT + visWidth + X_OFFSET_RIGHT;

        GraphicsConfiguration defaultConfiguration =
                GraphicsEnvironment.getLocalGraphicsEnvironment().getDefaultScreenDevice().getDefaultConfiguration();
        BufferedImage bi = UIUtil.createImage(defaultConfiguration, totalWidth, lineHeight, BufferedImage.TYPE_INT_RGB,
                PaintUtil.RoundingMode.CEIL);
        Graphics2D graphics = (Graphics2D) bi.getGraphics();

        VisualizationUtil.drawTransparentBackground(graphics, bi);

        int yPos = lineHeight;

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

            double clusterThreadArtifactMetric = summedThreadMetricValues(threadArtifactClusterSetEntry.getValue());

            graphics.setColor(color);

            int clusterHeight = (int) (Math.ceil(clusterThreadArtifactMetric / totalThreadFilteredCalleeTime * lineHeight));

            graphics.fillRect(X_OFFSET_LEFT, yPos - clusterHeight, visWidth, clusterHeight);

            yPos = yPos - clusterHeight;

//            int size = threadArtifactClusterSetEntry.getValue().size();
//
//            graphics.setColor(color);
//
//            int clusterHeight = (int) ((size / (double) totalThreads) * lineHeight);
//
//            graphics.fillRect(xPos, yPos - clusterHeight, width, clusterHeight);
//
//            yPos = yPos - clusterHeight;

        }

        BufferedImage subimage = bi.getSubimage(0, 0, bi.getWidth(), bi.getHeight());
        ImageIcon imageIcon = new ImageIcon(subimage);

        JLabel jLabel = new JLabel();
        jLabel.setIcon(imageIcon);

        jLabel.setSize(imageIcon.getIconWidth(), imageIcon.getIconHeight());
        // jLabel.addMouseListener(??);

        return jLabel;
    }

    private int totalThreads(Map<ThreadArtifactCluster, Set<AThreadArtifact>> neighborClusterSets)
    {
        return neighborClusterSets.values().stream().mapToInt(Set::size).sum();
    }

    private double summedThreadMetricValues(Collection<AThreadArtifact> codeSparksThreads)
    {
        return codeSparksThreads
                .stream()
                .filter(codeSparksThread -> !codeSparksThread.isFiltered())
                .map(codeSparksThread -> codeSparksThread.getNumericalMetricValue(primaryMetricIdentifier)).reduce(0d, Double::sum);
    }

    private double summedThreadMetricValuesOfNeighbors(Collection<ANeighborArtifact> neighborProfilingArtifacts)
    {
        return neighborProfilingArtifacts.stream().map(aNeighborProfilingArtifact ->
                summedThreadMetricValues(aNeighborProfilingArtifact.getThreadArtifacts())
        ).reduce(0d, Double::sum);
    }
}
