package de.unitrier.st.codesparks.core.visualization.thread;

import com.intellij.ui.JBColor;
import de.unitrier.st.codesparks.core.data.AProfilingArtifact;
import de.unitrier.st.codesparks.core.data.ThreadArtifact;
import de.unitrier.st.codesparks.core.visualization.popup.IThreadSelectable;
import de.unitrier.st.codesparks.core.data.ThreadArtifactCluster;

import java.awt.*;
import java.util.List;
import java.util.Set;

public class RadialZoomedThreadArtifactVisualization extends ARadialThreadVisualization
{
    private final List<IThreadSelectable> threadSelectables;
    private final IThreadSelectableIndexProvider indexProvider;
    private int hoveredCluster = -1;

    RadialZoomedThreadArtifactVisualization(AProfilingArtifact artifact,
                                            IThreadSelectableIndexProvider indexProvider,
                                            List<IThreadSelectable> threadSelectables)
    {
        setUpVisualizationParameter(154, 50);
        this.indexProvider = indexProvider;
        this.artifact = artifact;
        this.threadSelectables = threadSelectables;
    }

    @Override
    protected void paintComponent(Graphics g)
    {
        super.paintComponent(g);
        paintVisualization();
    }

    private void paintVisualization()
    {
        if (artifact == null)
        {
            return;
        }
        List<ThreadArtifactCluster> threadArtifactClusters = artifact.getSortedDefaultThreadArtifactClustering();
        int startAngle = 90; //set start angle to 90 for starting at 12 o'clock
        JBColor[] colors = {new JBColor(Color.decode("#5F4E95"), Color.decode("#5F4E95")), new JBColor(Color.decode("#B25283"),
                Color.decode("#B25283")), new JBColor(Color.decode("#3E877F"), Color.decode("#3E877F"))};
        //drawRectangleBackground();

        double threadRationFromRunBefore = 0;
        int markedStartAngle = -1;
        int markedAngle = -1;
        int markedRadius = -1;

        int index = indexProvider.getThreadSelectableIndex();

        final Set<ThreadArtifact> filteredThreadArtifacts = threadSelectables.get(index).getFilteredThreadArtifacts();
        final Set<ThreadArtifact> selectedThreadArtifacts = threadSelectables.get(index).getSelectedThreadArtifacts();
        final int numberOfSelectedArtifactThreads = selectedThreadArtifacts.size();

        String completeNumberOfThreadsString = numberOfSelectedArtifactThreads + "";
        int labelWidth;
        labelWidth = 5 + completeNumberOfThreadsString.length() * 13;

        drawOverallCircle();
        drawInnerCircle();
        for (int i = 0; i < threadArtifactClusters.size(); i++)
        {
            VisualThreadArtifactClusterPropertiesManager propertiesManager = VisualThreadArtifactClusterPropertiesManager.getInstance();
            RadialVisualThreadArtifactClusterProperties properties =
                    new RadialVisualThreadArtifactClusterProperties(threadArtifactClusters.get(i), colors[i],
                            artifact.getNumberOfThreads());

            double filteredRuntimeRatio =
                    ThreadVisualizationUtil.calculateFilteredAvgRuntimeRatioForZoomVisualization(threadArtifactClusters.get(i),
                            selectedThreadArtifacts, false);
            double filteredRuntimeRatioSum =
                    ThreadVisualizationUtil.calculateFilteredSumRuntimeRatioForZoomVisualisation(threadArtifactClusters.get(i), selectedThreadArtifacts,
                            false);

            final ThreadArtifactCluster clusterArtifacts = (ThreadArtifactCluster) threadArtifactClusters.get(i).clone();
            clusterArtifacts.removeAll(filteredThreadArtifacts);
            double filteredThreadRatio = clusterArtifacts.size() / (double) numberOfSelectedArtifactThreads;
            double completeFilteredRuntimeDurationOfCluster = getFilteredMetricSumOfCluster(threadArtifactClusters.get(i),
                    filteredThreadArtifacts);

            properties.setRuntimeRatio(filteredRuntimeRatio);
            properties.setThreadRatio(filteredThreadRatio);
            properties.setCompleteFilteredRuntime(completeFilteredRuntimeDurationOfCluster);
            properties.setRuntimeRationSum(filteredRuntimeRatioSum);
            propertiesManager.registerProperties(properties);

            if (i != 0)
            {
                startAngle -= threadRationFromRunBefore * 360;
            }

            int angle = (int) (360 * filteredThreadRatio * -1);
            int radius = getRadius(filteredRuntimeRatio);
            int radiusSum = getSumRadius(filteredRuntimeRatioSum);

            drawArcForSumAndAvg(colors[i], radiusSum, radius, startAngle, angle);
            if (hoveredCluster == threadArtifactClusters.get(i).getId())
            {
                markedStartAngle = startAngle;
                markedAngle = angle;
                //markedRadius = radius;
                markedRadius = radiusSum;
            }

            g2d.setColor(JBColor.BLACK);

            int startAngleTmp = startAngle - Math.abs(angle);
            if (startAngleTmp < 0)
                startAngleTmp = 360 - Math.abs(startAngleTmp);

            properties.setArcStartAngle(startAngleTmp);
            properties.setArcAngle(Math.abs(angle));
            threadRationFromRunBefore = filteredThreadRatio;
        }

        final int yOffsetForTotalThreadsText = -5;
        final int yOffsetForDifferentClassesText = 18;
        final float fontSize = 16f;

        drawHoverCluster(markedStartAngle, markedAngle, numberOfSelectedArtifactThreads, markedRadius);
        drawNumberOfThreadsLabel(labelWidth, fontSize, numberOfSelectedArtifactThreads, yOffsetForTotalThreadsText);
        drawNumberOfDifferentThreadTypesLabel(labelWidth, fontSize,
                ThreadVisualizationUtil.getNumberOfSelectedThreadTypes(artifact, selectedThreadArtifacts),
                yOffsetForDifferentClassesText);
    }

    void onHoverCluster(int clusterId)
    {
        hoveredCluster = clusterId;
        repaint();
    }

    void unHoverCluster()
    {
        hoveredCluster = -1;
        repaint();
    }
}
