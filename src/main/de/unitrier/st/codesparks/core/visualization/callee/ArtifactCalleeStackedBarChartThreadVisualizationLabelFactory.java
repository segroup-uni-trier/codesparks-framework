package de.unitrier.st.codesparks.core.visualization.callee;

import com.intellij.ui.JBColor;
import com.intellij.ui.paint.PaintUtil;
import com.intellij.util.ui.UIUtil;
import de.unitrier.st.codesparks.core.CoreUtil;
import de.unitrier.st.codesparks.core.data.*;
import de.unitrier.st.codesparks.core.visualization.VisConstants;
import de.unitrier.st.codesparks.core.visualization.VisualizationUtil;
import de.unitrier.st.codesparks.core.visualization.popup.ThreadColor;
import de.unitrier.st.codesparks.core.visualization.thread.VisualThreadArtifactClusterProperties;
import de.unitrier.st.codesparks.core.visualization.thread.VisualThreadArtifactClusterPropertiesManager;

import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.List;
import java.util.*;
import java.util.stream.Collectors;

@SuppressWarnings("unused")
public class ArtifactCalleeStackedBarChartThreadVisualizationLabelFactory extends AArtifactCalleeVisualizationLabelFactory
{
    public ArtifactCalleeStackedBarChartThreadVisualizationLabelFactory() {}

    public ArtifactCalleeStackedBarChartThreadVisualizationLabelFactory(int sequence)
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
        final double totalThreadFilteredCalleeTime = summedThreadMetricValuesOfNeighbors(threadFilteredNeighborArtifactsOfLine);

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
        Graphics graphics = bi.getGraphics();

        Color backgroundColor = CoreUtil.getSelectedFileEditorBackgroundColor();

        graphics.setColor(backgroundColor);
        graphics.fillRect(0, 0, totalWidth, lineHeight);

        int yPos = lineHeight;

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

    private int totalThreads(Map<ThreadArtifactCluster, Set<ThreadArtifact>> neighborClusterSets)
    {
        return neighborClusterSets.values().stream().mapToInt(Set::size).sum();
    }

    private double summedThreadMetricValues(Collection<ThreadArtifact> threadArtifacts)
    {
        return threadArtifacts
                .stream()
                .filter(threadArtifact -> !threadArtifact.isFiltered())
                .map(ThreadArtifact::getMetricValue).reduce(0d, Double::sum);
    }

    private double summedThreadMetricValuesOfNeighbors(Collection<ANeighborProfilingArtifact> neighborProfilingArtifacts)
    {
        return neighborProfilingArtifacts.stream().map(aNeighborProfilingArtifact ->
                summedThreadMetricValues(aNeighborProfilingArtifact.getThreadArtifacts())
        ).reduce(0d, Double::sum);
    }
}
