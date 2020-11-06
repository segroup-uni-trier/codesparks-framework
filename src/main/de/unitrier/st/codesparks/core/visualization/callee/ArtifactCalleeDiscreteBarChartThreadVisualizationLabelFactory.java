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

public class ArtifactCalleeDiscreteBarChartThreadVisualizationLabelFactory extends AArtifactCalleeVisualizationLabelFactory
{
    @SuppressWarnings("unused")
    public ArtifactCalleeDiscreteBarChartThreadVisualizationLabelFactory() {}

    public ArtifactCalleeDiscreteBarChartThreadVisualizationLabelFactory(int sequence)
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
        final int threadsPerColumn = 3;
        final int lineHeight = VisualizationUtil.getLineHeightFloor(VisConstants.getLineHeight(), threadsPerColumn);

        final int X_OFFSET_LEFT = 2;
        final int X_OFFSET_RIGHT = 1;

        final int threadMetaphorWidth = 24;
        final int barChartWidth = 24;

        final int clusterBarMaxWidth = 20;

        final int totalWidth = X_OFFSET_LEFT + threadMetaphorWidth + barChartWidth + X_OFFSET_RIGHT;

        final int threadSquareEdgeLength = 3;//(lineHeight - 6) / threadsPerColumn;

        int threadSquareYPos = lineHeight - threadSquareEdgeLength - 2;
        final int threadSquareOffset = threadSquareEdgeLength + 1;

        GraphicsConfiguration defaultConfiguration =
                GraphicsEnvironment.getLocalGraphicsEnvironment().getDefaultScreenDevice().getDefaultConfiguration();
        BufferedImage bi = UIUtil.createImage(defaultConfiguration, totalWidth, lineHeight, BufferedImage.TYPE_INT_RGB,
                PaintUtil.RoundingMode.CEIL);
        Graphics graphics = bi.getGraphics();

        Color backgroundColor = CoreUtil.getSelectedFileEditorBackgroundColor();
        graphics.setColor(backgroundColor);
        graphics.fillRect(0, 0, totalWidth, lineHeight);

        // Thread metaphor
        graphics.setColor(VisConstants.BORDER_COLOR);

        //graphics.fillRect(X_OFFSET_LEFT - 1, lineHeight / 2 - 1, 3, 3); // Leading square

        final int barrierXPos = threadMetaphorWidth / 2;

        // Leading arrow
        graphics.fillRect(X_OFFSET_LEFT, lineHeight / 2, barrierXPos - 1, 1);
        graphics.drawLine(X_OFFSET_LEFT + barrierXPos - 3, lineHeight / 2 - 3, X_OFFSET_LEFT + barrierXPos, lineHeight / 2);
        graphics.drawLine(X_OFFSET_LEFT + barrierXPos - 3, lineHeight / 2 + 3, X_OFFSET_LEFT + barrierXPos, lineHeight / 2);

        // Vertical bar or barrier, respectively
        final int barrierWidth = 3;
        graphics.fillRect(X_OFFSET_LEFT + barrierXPos, 0, barrierWidth, lineHeight);


//        graphics.fillRect(X_OFFSET_LEFT + threadMetaphorWidth / 2, 2, 1, lineHeight - 4); // vertical
//        graphics.fillRect(X_OFFSET_LEFT + threadMetaphorWidth / 2, 2, threadMetaphorWidth / 2, 1); // top horizontal
//        graphics.fillRect(X_OFFSET_LEFT + threadMetaphorWidth / 2, lineHeight - 3, threadMetaphorWidth / 2, 1); // bottom horizontal

        //
        Rectangle threadVisualisationArea = new Rectangle(
                X_OFFSET_LEFT + threadMetaphorWidth, 0, barChartWidth - 1, lineHeight - 1);


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

            double clusterThreadArtifactMetric = summedThreadMetricValues(threadArtifactClusterSetEntry.getValue());

            graphics.setColor(color);

            int clusterWidth;
            double percent = clusterThreadArtifactMetric / totalThreadFilteredCalleeTime;

            if (percent > 0D)
            {
                int discrete = (int) (percent * 100 / 10 + 0.9999);
                clusterWidth = clusterBarMaxWidth / 10 * discrete;
            } else
            {
                clusterWidth = 0;
            }
            graphics.fillRect(X_OFFSET_LEFT + threadMetaphorWidth + 2, threadSquareYPos, clusterWidth, threadSquareEdgeLength);

            if (clusterWidth > 0)
            {
                // Arrows after barrier
                graphics.fillRect(X_OFFSET_LEFT + barrierXPos + barrierWidth, threadSquareYPos + 1, barrierXPos - 1, 1);
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
