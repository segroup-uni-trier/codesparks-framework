/*
 * Copyright (c) 2021. Oliver Moseler
 */
package de.unitrier.st.codesparks.core.visualization.thread;

import com.intellij.ui.JBColor;
import de.unitrier.st.codesparks.core.data.*;
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
    private final IThreadArtifactsDisplayDataProvider threadArtifactsDisplayData;
    private final AMetricIdentifier secondaryMetricIdentifier;

    public ThreadRadarLabelFactory(
            final AMetricIdentifier primaryMetricIdentifier
            , final AMetricIdentifier secondaryMetricIdentifier
    )
    {
        super(primaryMetricIdentifier, 0);
        this.secondaryMetricIdentifier = secondaryMetricIdentifier;
        this.threadArtifactsDisplayData = new DefaultThreadArtifactsDisplayDataProvider(primaryMetricIdentifier);
    }

    public ThreadRadarLabelFactory(
            final AMetricIdentifier primaryMetricIdentifier
            , final AMetricIdentifier secondaryMetricIdentifier
            , final int sequence
    )
    {
        super(primaryMetricIdentifier, sequence);
        this.secondaryMetricIdentifier = secondaryMetricIdentifier;
        this.threadArtifactsDisplayData = new DefaultThreadArtifactsDisplayDataProvider(primaryMetricIdentifier);
    }

    public ThreadRadarLabelFactory(
            final AMetricIdentifier primaryMetricIdentifier
            , final AMetricIdentifier secondaryMetricIdentifier
            , final int sequence
            , final IThreadArtifactsDisplayDataProvider threadArtifactsDisplayData
    )
    {
        super(primaryMetricIdentifier, sequence);
        this.secondaryMetricIdentifier = secondaryMetricIdentifier;
        this.threadArtifactsDisplayData = threadArtifactsDisplayData;
    }

    @Override
    public JLabel createArtifactLabel(final AArtifact artifact)
    {
        final Collection<AThreadArtifact> threadArtifacts = artifact.getThreadArtifactsWithNumericMetricValue(primaryMetricIdentifier);
        final int totalNumberOfThreads = threadArtifacts.size();
        if (totalNumberOfThreads < 1)
        {
            return emptyLabel();
        }

        final ThreadArtifactClustering clustering =
                artifact.clusterThreadArtifacts(ConstraintKMeansWithAMaximumOfThreeClusters.getInstance(primaryMetricIdentifier), true);

        boolean createDisabledViz = false;
        long numberOfSelectedArtifactThreads = threadArtifacts.stream().filter(t -> !t.isFiltered()).count();

        int numberOfSelectedThreadTypes = ThreadVisualizationUtil.getNumberOfSelectedThreadTypesWithNumericMetricValueInSelection(
                artifact, primaryMetricIdentifier);

        if (numberOfSelectedArtifactThreads == 0)
        { // In case any thread is deselected, i.e. where for all threads thr the method call thr.isFiltered() yields true
            numberOfSelectedArtifactThreads = totalNumberOfThreads;
            Map<String, List<AThreadArtifact>> threadTypeLists = artifact.getThreadTypeLists();
            numberOfSelectedThreadTypes = threadTypeLists == null ? 0 : threadTypeLists.size();
            createDisabledViz = true;
        }

        final String completeNumberOfThreadsString = numberOfSelectedArtifactThreads + "";
        final int frameSize = ThreadRadarConstants.FRAME + completeNumberOfThreadsString.length() * 5;
        final int labelWidth = 5 + completeNumberOfThreadsString.length() * 5;

        final CodeSparksGraphics graphics = getGraphics(frameSize, ThreadRadarConstants.CIRCLE_FRAMESIZE);
        final VisualThreadClusterPropertiesManager propertiesManager = VisualThreadClusterPropertiesManager.getInstance(clustering);

        int startAngle = 90;
        for (int i = 0; i < clustering.size(); i++)
        {
            final ThreadArtifactCluster cluster = clustering.get(i);
            final JBColor color = ThreadColor.getNextColor(i, createDisabledViz);

            final RadialVisualThreadClusterProperties properties = new RadialVisualThreadClusterProperties(cluster, color, artifact.getNumberOfThreads(),
                    primaryMetricIdentifier);
            propertiesManager.registerProperties(properties);

            final double averageMetricValueOfSelectedThreads = properties.getAverageMetricValueOfSelectedThreads(cluster, primaryMetricIdentifier,
                    createDisabledViz);

            final double numberOfSelectedThreadsRatio = properties.getNumberOfSelectedThreadsRatio(cluster,
                    (int) numberOfSelectedArtifactThreads, createDisabledViz);

            final double sumMetricValueOfSelectedThreads = properties.getSumMetricValueOfSelectedThreads(cluster, primaryMetricIdentifier,
                    createDisabledViz);

            final int arcRatio = (int) (360 * numberOfSelectedThreadsRatio);

            final int angle = arcRatio * -1;
            final int radius = ThreadVisualizationUtil.metricToDiscreteMetric(averageMetricValueOfSelectedThreads, ThreadRadarConstants.CIRCLESIZE);
            final int radiusSum = ThreadVisualizationUtil.metricToDiscreteMetric(sumMetricValueOfSelectedThreads, ThreadRadarConstants.CIRCLESIZE);

            graphics.setColor(VisualizationUtil.getBackgroundMetricColor(color, .25f));
            graphics.fillArc(ThreadRadarConstants.MIDDLEPOINT - (radiusSum / 2), ThreadRadarConstants.MIDDLEPOINT - (radiusSum / 2),
                    radiusSum, radiusSum, startAngle, angle);
            graphics.setColor(color);
            graphics.fillArc(ThreadRadarConstants.MIDDLEPOINT - (radius / 2), ThreadRadarConstants.MIDDLEPOINT - (radius / 2), radius,
                    radius, startAngle, angle);
            graphics.setColor(JBColor.BLACK);

            properties.setArcStartAngle(startAngle);
            properties.setArcAngle(angle);

            startAngle -= arcRatio; // rotate the startAngle
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
