/*
 * Copyright (c) 2021. Oliver Moseler
 */
package de.unitrier.st.codesparks.core.visualization.thread;

import com.intellij.ui.JBColor;
import de.unitrier.st.codesparks.core.data.AArtifact;
import de.unitrier.st.codesparks.core.data.AMetricIdentifier;
import de.unitrier.st.codesparks.core.data.AThreadArtifact;
import de.unitrier.st.codesparks.core.data.ThreadArtifactCluster;
import de.unitrier.st.codesparks.core.visualization.AArtifactVisualizationLabelFactory;
import de.unitrier.st.codesparks.core.visualization.CodeSparksGraphics;
import de.unitrier.st.codesparks.core.visualization.VisualizationUtil;
import de.unitrier.st.codesparks.core.visualization.popup.ThreadColor;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseListener;
import java.util.Collection;
import java.util.List;
import java.util.Map;

public class ThreadRadarLabelFactory extends AArtifactVisualizationLabelFactory
{
    private final IThreadArtifactsDisplayData threadArtifactsDisplayData;
    private final AMetricIdentifier secondaryMetricIdentifier;

    @SuppressWarnings("unused")
    public ThreadRadarLabelFactory(
            final AMetricIdentifier primaryMetricIdentifier
            , final AMetricIdentifier secondaryMetricIdentifier
    )
    {
        this(primaryMetricIdentifier, secondaryMetricIdentifier, 0, new DefaultThreadArtifactsDisplayData(primaryMetricIdentifier));
    }

    public ThreadRadarLabelFactory(
            final AMetricIdentifier primaryMetricIdentifier
            , final AMetricIdentifier secondaryMetricIdentifier
            , final int sequence
    )
    {
        this(primaryMetricIdentifier, secondaryMetricIdentifier, sequence, new DefaultThreadArtifactsDisplayData(primaryMetricIdentifier));
    }

    public ThreadRadarLabelFactory(
            final AMetricIdentifier primaryMetricIdentifier
            , final AMetricIdentifier secondaryMetricIdentifier
            , final int sequence
            , final IThreadArtifactsDisplayData threadArtifactsDisplayData
    )
    {
        super(primaryMetricIdentifier, sequence);
        this.secondaryMetricIdentifier = secondaryMetricIdentifier;
        this.threadArtifactsDisplayData = threadArtifactsDisplayData;
    }

    @Override
    public JLabel createArtifactLabel(final AArtifact artifact)
    {
        final Collection<AThreadArtifact> threadArtifacts = artifact.getThreadArtifacts();
        if (threadArtifacts.isEmpty())
        {
            return emptyLabel();
        }

        final List<ThreadArtifactCluster> threadArtifactClusters =
                artifact.getSortedConstraintKMeansWithAMaximumOfThreeClustersThreadArtifactClustering(primaryMetricIdentifier);

        boolean createDisabledViz = false;

        long numberOfSelectedArtifactThreads =
                artifact.getThreadArtifacts().stream().filter(t -> t.getNumericalMetricValue(primaryMetricIdentifier) > 0 && !t.isFiltered()).count();
        int numberOfSelectedThreadTypes = ThreadVisualizationUtil.getNumberOfFilteredThreadTypesWithNumericMetricValueInSelection(artifact,
                primaryMetricIdentifier);

        if (numberOfSelectedArtifactThreads == 0)
        { // In case any thread is deselected, i.e. where for all threads thr the method call thr.isFiltered() yields true
            numberOfSelectedArtifactThreads = artifact.getNumberOfThreads();
            Map<String, List<AThreadArtifact>> threadTypeLists = artifact.getThreadTypeLists();
            numberOfSelectedThreadTypes = threadTypeLists == null ? 0 : threadTypeLists.size();
            createDisabledViz = true;
        }

        final String completeNumberOfThreadsString = numberOfSelectedArtifactThreads + "";
        final int frameSize = ThreadRadarConstants.FRAME + completeNumberOfThreadsString.length() * 5;
        final int labelWidth = 5 + completeNumberOfThreadsString.length() * 5;

        final CodeSparksGraphics graphics = getGraphics(frameSize, ThreadRadarConstants.CIRCLE_FRAMESIZE);

        double threadRationFromRunBefore = 0;
        for (int i = 0; i < threadArtifactClusters.size(); i++)
        {
            final JBColor color = ThreadColor.getNextColor(i, createDisabledViz);

            final VisualThreadClusterPropertiesManager propertiesManager = VisualThreadClusterPropertiesManager.getInstance();
            final RadialVisualThreadClusterProperties properties =
                    new RadialVisualThreadClusterProperties(threadArtifactClusters.get(i), color,
                            artifact.getNumberOfThreads(), primaryMetricIdentifier);
            propertiesManager.registerProperties(properties);

            //double filteredRuntimeRatio = properties.calculateFilteredRuntimeRatio(threadArtifactClusters.get(i), createDisabledViz);
            final double filteredRuntimeRatio = properties.calculateAvgFilteredNumericalMetricRatio(threadArtifactClusters.get(i), primaryMetricIdentifier,
                    createDisabledViz);
            final double filteredThreadRatio = properties.calculateFilteredThreadRatio(threadArtifactClusters.get(i),
                    (int) numberOfSelectedArtifactThreads, createDisabledViz);
            final double filteredRuntimeRatioSum = properties.calculateFilteredSumNumericalMetricRatio(threadArtifactClusters.get(i), primaryMetricIdentifier,
                    createDisabledViz);

            int startAngle = 90;
            if (i != 0)
            {
                startAngle -= (int) (threadRationFromRunBefore * 360);
            }
            final int angle = (int) (360 * filteredThreadRatio) * -1;
            final int radius = ThreadVisualizationUtil.metricToDiscreteMetric(filteredRuntimeRatio, ThreadRadarConstants.CIRCLESIZE);
            final int radiusSum = ThreadVisualizationUtil.metricToDiscreteMetric(filteredRuntimeRatioSum, ThreadRadarConstants.CIRCLESIZE);

            graphics.setColor(VisualizationUtil.getBackgroundMetricColor(color, .25f));
            graphics.fillArc(ThreadRadarConstants.MIDDLEPOINT - (radiusSum / 2), ThreadRadarConstants.MIDDLEPOINT - (radiusSum / 2),
                    radiusSum, radiusSum, startAngle, angle);
            graphics.setColor(color);
            graphics.fillArc(ThreadRadarConstants.MIDDLEPOINT - (radius / 2), ThreadRadarConstants.MIDDLEPOINT - (radius / 2), radius,
                    radius, startAngle, angle);
            graphics.setColor(JBColor.BLACK);

            //if (startAngle < 0)
            //    properties.setArcStartAngle(360+startAngle);
            //else
            properties.setArcStartAngle(startAngle);
            properties.setArcAngle(angle);
            threadRationFromRunBefore = filteredThreadRatio;
        }

        graphics.setColor(JBColor.DARK_GRAY);
        graphics.drawOval((ThreadRadarConstants.CIRCLE_FRAMESIZE / 2) - (ThreadRadarConstants.CIRCLESIZE / 2),
                (ThreadRadarConstants.CIRCLE_FRAMESIZE / 2) - (ThreadRadarConstants.CIRCLESIZE / 2),
                ThreadRadarConstants.CIRCLESIZE, ThreadRadarConstants.CIRCLESIZE);


        // draw total number of threads label
        final Font currentFont = graphics.getFont();
        final Font newFont = currentFont.deriveFont(currentFont.getSize() * ThreadRadarConstants.CIRCLESIZE * 0.02f);
        graphics.setFont(newFont);
        int labelStartAngle = (int) (ThreadVisualizationUtil.getStartAngle(ThreadRadarConstants.RADIUS,
                ThreadRadarConstants.LABELRADIUS) * -1);//calcStartAngle() * -1; //-65
        final int arcAngle = 32;
        int x1 =
                (int) ((ThreadRadarConstants.LABELRADIUS) * Math.cos(Math.toRadians(-labelStartAngle - arcAngle))) + ThreadRadarConstants.CIRCLE_FRAMESIZE / 2;
        int y1 =
                (int) ((ThreadRadarConstants.LABELRADIUS) * Math.sin(Math.toRadians(-labelStartAngle - arcAngle))) + ThreadRadarConstants.CIRCLE_FRAMESIZE / 2;
        int x2 = x1 + labelWidth;
        int y2 = y1;

        int x3 =
                (int) (ThreadRadarConstants.LABELRADIUS * Math.cos(Math.toRadians(-labelStartAngle))) + ThreadRadarConstants.CIRCLE_FRAMESIZE / 2;
        int y3 =
                (int) (ThreadRadarConstants.LABELRADIUS * Math.sin(Math.toRadians(-labelStartAngle))) + ThreadRadarConstants.CIRCLE_FRAMESIZE / 2;
        int x4 = x3 + (x1 - x3) + labelWidth;
        int y4 = y3;

        graphics.drawLine(x1, y1, x2, y2);
        graphics.drawLine(x3, y3, x4, y3);
        graphics.drawLine(x4, y4, x2, y2);

        graphics.drawArc((ThreadRadarConstants.CIRCLE_FRAMESIZE / 2) - ((ThreadRadarConstants.CIRCLESIZE + 2 * ThreadRadarConstants.LABELDISTANCE) / 2),
                (ThreadRadarConstants.CIRCLE_FRAMESIZE / 2) - ((ThreadRadarConstants.CIRCLESIZE + 2 * ThreadRadarConstants.LABELDISTANCE) / 2),
                ThreadRadarConstants.CIRCLESIZE + 2 * ThreadRadarConstants.LABELDISTANCE,
                ThreadRadarConstants.CIRCLESIZE + 2 * ThreadRadarConstants.LABELDISTANCE, labelStartAngle, arcAngle);

        graphics.drawString(numberOfSelectedArtifactThreads + "", x1 + 2, y3 - 1);

        // draw number of different classes label
        labelStartAngle = (int) ThreadVisualizationUtil.getStartAngle(ThreadRadarConstants.RADIUS,
                ThreadRadarConstants.LABELRADIUS);
        x1 = (int) ((ThreadRadarConstants.LABELRADIUS) * Math.cos(Math.toRadians(-(labelStartAngle - arcAngle)))) + ThreadRadarConstants.CIRCLE_FRAMESIZE / 2;
        y1 = (int) ((ThreadRadarConstants.LABELRADIUS) * Math.sin(Math.toRadians(-(labelStartAngle - arcAngle)))) + ThreadRadarConstants.CIRCLE_FRAMESIZE / 2;
        x2 = x1 + labelWidth;
        y2 = y1;

        x3 = (int) (ThreadRadarConstants.LABELRADIUS * Math.cos(Math.toRadians(-labelStartAngle))) + ThreadRadarConstants.CIRCLE_FRAMESIZE / 2;
        y3 = (int) (ThreadRadarConstants.LABELRADIUS * Math.sin(Math.toRadians(-labelStartAngle))) + ThreadRadarConstants.CIRCLE_FRAMESIZE / 2;
        x4 = x3 + (x1 - x3) + labelWidth;
        y4 = y3;

        graphics.drawLine(x1, y1, x2, y2);
        graphics.drawLine(x3, y3, x4, y3);
        graphics.drawLine(x4, y4, x2, y2);

        graphics.drawArc((ThreadRadarConstants.CIRCLE_FRAMESIZE / 2) - ((ThreadRadarConstants.CIRCLESIZE + 2 * ThreadRadarConstants.LABELDISTANCE) / 2),
                (ThreadRadarConstants.CIRCLE_FRAMESIZE / 2) - ((ThreadRadarConstants.CIRCLESIZE + 2 * ThreadRadarConstants.LABELDISTANCE) / 2),
                ThreadRadarConstants.CIRCLESIZE + 2 * ThreadRadarConstants.LABELDISTANCE,
                ThreadRadarConstants.CIRCLESIZE + 2 * ThreadRadarConstants.LABELDISTANCE, labelStartAngle - arcAngle, arcAngle - 4);

        graphics.drawString(numberOfSelectedThreadTypes + "", x1 + 2, y3 + 8);

        final JLabel jLabel = makeLabel(graphics);

        for (final MouseListener mouseListener : jLabel.getMouseListeners())
        {// Is necessary since there is some kind of caching implemented in the jetbrains ide core. Otherwise every listener would
            // trigger each time a click occurs. For each click a new listener will be attached!
            jLabel.removeMouseListener(mouseListener);
        }
        jLabel.addMouseListener(new ThreadRadarMouseListener(jLabel, artifact, threadArtifactsDisplayData, primaryMetricIdentifier,
                secondaryMetricIdentifier));
        return jLabel;
    }
}
