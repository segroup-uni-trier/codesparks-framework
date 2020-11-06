package de.unitrier.st.codesparks.core.overview;

import com.intellij.ui.JBColor;
import de.unitrier.st.codesparks.core.data.ThreadArtifact;
import de.unitrier.st.codesparks.core.data.ThreadArtifactCluster;
import de.unitrier.st.codesparks.core.visualization.thread.*;

import java.awt.*;
import java.awt.event.MouseListener;
import java.util.List;

public class GlobalRadialThreadVisualization extends ARadialThreadVisualization
{
    //    private ARadialThreadArtifactVisualizationDisplayData radialThreadArtifactVisualizationDisplayData;
    private final ProfilingArtifactOverview profilingArtifactOverview;

    GlobalRadialThreadVisualization(ProfilingArtifactOverview profilingArtifactOverview)
    {
        setUpVisualizationParameter(30, 25);
//        this.radialThreadArtifactVisualizationDisplayData = new DefaultRadialThreadArtifactVisualizationDisplayData();
        this.profilingArtifactOverview = profilingArtifactOverview;
    }

    @Override
    protected void paintComponent(Graphics g)
    {
        super.paintComponent(g);
        paintVisualization();
    }

    private void paintVisualization()
    {
        artifact = profilingArtifactOverview.getProfilingResult().getGlobalArtifact();
        if (artifact == null)
        {
            paintDefault();
            //ProfilingLogger.addText("%s: Global artifact is null!", getClass());
            return;
        }
        List<ThreadArtifactCluster> threadArtifactClusters = artifact.getSortedDefaultThreadArtifactClustering();
        double startAngle = 90;
        boolean usingGrayColors = false;
        JBColor[] colors = {new JBColor(Color.decode("#5F4E95"), Color.decode("#5F4E95")), new JBColor(Color.decode("#B25283"),
                Color.decode("#B25283")), new JBColor(Color.decode("#3E877F"), Color.decode("#3E877F"))};
        JBColor[] grayColors = {new JBColor(Color.decode("#999999"), Color.decode("#999999")), new JBColor(Color.decode("#777777"),
                Color.decode("#777777")), new JBColor(Color.decode("#555555"), Color.decode("#555555"))};
//        drawRectangleBackground();

        double threadRationFromRunBefore = 0;
        int markedStartAngle = -1;
        int markedAngle = -1;
        int markedRadius = -1;


        int numberOfSelectedThreadTypes = ThreadVisualizationUtil.getNumberOfSelectedThreadTypes(artifact, null);
        long numberOfSelectedArtifactThreads = artifact.getThreadArtifacts().stream().filter(t -> !t.isFiltered()).count();

        if (numberOfSelectedArtifactThreads == 0)
        {
            numberOfSelectedArtifactThreads = artifact.getNumberOfThreads();
            numberOfSelectedThreadTypes = artifact.getThreadTypeLists().size();
            usingGrayColors = true;
        }

        String completeNumberOfThreadsAsString = numberOfSelectedArtifactThreads + "";
        final int labelWidth = 5 + completeNumberOfThreadsAsString.length() * 5;

        drawOverallCircle();

        for (int i = 0; i < threadArtifactClusters.size(); i++)
        {
            Color color = (!usingGrayColors) ? colors[i] : grayColors[i];
            VisualThreadArtifactClusterPropertiesManager propertiesManager = VisualThreadArtifactClusterPropertiesManager.getInstance();
            RadialVisualThreadArtifactClusterProperties properties =
                    new RadialVisualThreadArtifactClusterProperties(threadArtifactClusters.get(i), colors[i],
                            artifact.getNumberOfThreads());

            double filteredRuntimeRatio = ThreadVisualizationUtil.calculateFilteredAvgRuntimeRatio(threadArtifactClusters.get(i),
                    usingGrayColors);
            double filteredRuntimeRatioSum = ThreadVisualizationUtil.calculateFilteredSumRuntimeRatio(threadArtifactClusters.get(i),
                    usingGrayColors);
            final ThreadArtifactCluster clusterArtifacts = threadArtifactClusters.get(i);
            int numberOfNonFilteredArtifactsInCluster = (usingGrayColors) ? clusterArtifacts.size() :
                    (int) clusterArtifacts.stream().filter(ca -> !ca.isFiltered()).count();
            //int numberOfNonFilteredArtifactsInCluster = (int) clusterArtifacts.stream().filter(ca -> !ca.isFiltered()).count();
            double filteredThreadRatio = numberOfNonFilteredArtifactsInCluster / (double) numberOfSelectedArtifactThreads;

            // Calculate filtered thread ratio
//            double completeFilteredRuntimeDurationOfCluster =
//                    clusterArtifacts.stream().filter(c -> !c.isFiltered()).mapToDouble(ThreadArtifact::getMetricValue).sum();
            double completeFilteredRuntimeDurationOfCluster = (usingGrayColors) ?
                    clusterArtifacts.stream().mapToDouble(ThreadArtifact::getMetricValue).sum() :
                    clusterArtifacts.stream().filter(c -> !c.isFiltered()).mapToDouble(ThreadArtifact::getMetricValue).sum();

            properties.setRuntimeRatio(filteredRuntimeRatio);
            properties.setThreadRatio(filteredThreadRatio);
            properties.setCompleteFilteredRuntime(completeFilteredRuntimeDurationOfCluster);
            properties.setRuntimeRationSum(filteredRuntimeRatioSum);
            propertiesManager.registerProperties(properties);

            if (i != 0)
            {
                startAngle -= threadRationFromRunBefore * 360;
            }
            double angle = 360 * filteredThreadRatio;
            double radius = getRadius(filteredRuntimeRatio);
            double radiusSum = getSumRadius(filteredRuntimeRatioSum);


            drawArcForSumAndAvg(color, (int) radiusSum, (int) radius, (int) startAngle, (int) -angle);
            g2d.setColor(JBColor.BLACK);

            properties.setArcStartAngle(startAngle);
            properties.setArcAngle(angle);
            threadRationFromRunBefore = filteredThreadRatio;
        }

        final int yOffsetForLabelText = -1;
        final float fontSize = 8f;

        drawHoverCluster(markedStartAngle, markedAngle, numberOfSelectedArtifactThreads, markedRadius);
        drawNumberOfThreadsLabel(labelWidth, fontSize, numberOfSelectedArtifactThreads, yOffsetForLabelText);
        drawNumberOfDifferentThreadTypesLabel(labelWidth, fontSize, numberOfSelectedThreadTypes, 8);
        for (MouseListener mouseListener : getMouseListeners())
        { // Is necessary since there is some kind of caching implemented in the Jetbrains ide core. Otherwise every listener would
            // trigger each time a click occurs. For each click a new listener will be attached!
            removeMouseListener(mouseListener);
        }

        DefaultRadialThreadArtifactVisualizationDisplayData threadArtifactVisualizationDisplayData =
                new DefaultRadialThreadArtifactVisualizationDisplayData();

        addMouseListener(new RadialThreadVisualizationMouseListener(this, artifact, threadArtifactVisualizationDisplayData));
    }
}

