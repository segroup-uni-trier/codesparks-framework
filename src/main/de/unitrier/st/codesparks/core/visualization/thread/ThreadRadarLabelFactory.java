package de.unitrier.st.codesparks.core.visualization.thread;

import com.intellij.ui.JBColor;
import com.intellij.ui.paint.PaintUtil;
import com.intellij.util.ui.UIUtil;
import de.unitrier.st.codesparks.core.data.AArtifact;
import de.unitrier.st.codesparks.core.data.AThreadArtifact;
import de.unitrier.st.codesparks.core.data.AMetricIdentifier;
import de.unitrier.st.codesparks.core.data.ThreadArtifactCluster;
import de.unitrier.st.codesparks.core.visualization.AArtifactVisualizationLabelFactory;
import de.unitrier.st.codesparks.core.visualization.VisualizationUtil;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseListener;
import java.awt.image.BufferedImage;
import java.util.Collection;
import java.util.List;
import java.util.Map;

/*
 * Copyright (c), Oliver Moseler, 2020
 */
public class ThreadRadarLabelFactory extends AArtifactVisualizationLabelFactory
{
    private final IThreadRadarDisplayData radialThreadVisualizationPopupData;
    private final AMetricIdentifier secondaryMetricIdentifier;

    public ThreadRadarLabelFactory(
            final AMetricIdentifier primaryMetricIdentifier
            , final AMetricIdentifier secondaryMetricIdentifier
    )
    {
        this(primaryMetricIdentifier, secondaryMetricIdentifier, 0, new DefaultThreadRadarDisplayData(primaryMetricIdentifier));
    }

    public ThreadRadarLabelFactory(
            final AMetricIdentifier primaryMetricIdentifier
            , final AMetricIdentifier secondaryMetricIdentifier
            , final int sequence
    )
    {
        this(primaryMetricIdentifier, secondaryMetricIdentifier, sequence, new DefaultThreadRadarDisplayData(primaryMetricIdentifier));
    }

    public ThreadRadarLabelFactory(
            final AMetricIdentifier primaryMetricIdentifier
            , final AMetricIdentifier secondaryMetricIdentifier
            , final int sequence
            , final IThreadRadarDisplayData radialThreadVisualizationPopupData
    )
    {
        super(primaryMetricIdentifier, sequence);
        this.secondaryMetricIdentifier = secondaryMetricIdentifier;
        this.radialThreadVisualizationPopupData = radialThreadVisualizationPopupData;
    }

    @Override
    public JLabel createArtifactLabel(
            final AArtifact artifact
    )
    {
        final Collection<AThreadArtifact> codeSparksThreads = artifact.getThreadArtifacts();

        if (codeSparksThreads.isEmpty())
        {
            return emptyLabel();
        }

        final List<ThreadArtifactCluster> threadArtifactClusters = artifact.getSortedDefaultThreadArtifactClustering(primaryMetricIdentifier);
        int startAngle = 90;
        boolean useDisabledColors = false;
        JBColor[] colors = {new JBColor(Color.decode("#5F4E95"), Color.decode("#5F4E95")), new JBColor(Color.decode("#B25283"),
                Color.decode("#B25283")), new JBColor(Color.decode("#3E877F"), Color.decode("#3E877F"))};
        JBColor[] disabledColors = {new JBColor(Color.decode("#999999"), Color.decode("#999999")), new JBColor(Color.decode("#777777"),
                Color.decode("#777777")), new JBColor(Color.decode("#555555"), Color.decode("#555555"))};

        long numberOfSelectedArtifactThreads = artifact.getThreadArtifacts().stream().filter(t -> !t.isFiltered()).count();
        int numberOfSelectedThreadTypes = ThreadVisualizationUtil.getNumberOfSelectedThreadTypes(artifact, null);

        if (numberOfSelectedArtifactThreads == 0)
        {
            numberOfSelectedArtifactThreads = artifact.getNumberOfThreads();
            Map<String, List<AThreadArtifact>> threadTypeLists = artifact.getThreadTypeLists();
            numberOfSelectedThreadTypes = threadTypeLists == null ? 0 : threadTypeLists.size();
            useDisabledColors = true;
        }

        String completeNumberOfThreadsString = numberOfSelectedArtifactThreads + "";

        final int frameSize = ThreadRadarConstants.FRAME + completeNumberOfThreadsString.length() * 5;
        final int labelWidth = 5 + completeNumberOfThreadsString.length() * 5;

        GraphicsConfiguration defaultConfiguration =
                GraphicsEnvironment.getLocalGraphicsEnvironment().getDefaultScreenDevice().getDefaultConfiguration();
        BufferedImage bi = UIUtil.createImage(defaultConfiguration, frameSize, ThreadRadarConstants.CIRCLE_FRAMESIZE,
                BufferedImage.TYPE_INT_ARGB, PaintUtil.RoundingMode.CEIL);

        //Graphics graphics = bi.getGraphics();
        Graphics2D imgG2 = (Graphics2D) bi.getGraphics();
        imgG2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        // Draw the fully transparent background
        VisualizationUtil.drawTransparentBackground(imgG2, bi);
        //
        double threadRationFromRunBefore = 0;
        for (int i = 0; i < threadArtifactClusters.size(); i++)
        {
            VisualThreadClusterPropertiesManager propertiesManager = VisualThreadClusterPropertiesManager.getInstance();
            RadialVisualThreadClusterProperties properties =
                    new RadialVisualThreadClusterProperties(threadArtifactClusters.get(i), colors[i],
                            artifact.getNumberOfThreads(), primaryMetricIdentifier);
            propertiesManager.registerProperties(properties);

            //double filteredRuntimeRatio = properties.calculateFilteredRuntimeRatio(threadArtifactClusters.get(i), useDisabledColors);
            double filteredRuntimeRatio = properties.calculateAvgFilteredNumericalMetricRatio(threadArtifactClusters.get(i), primaryMetricIdentifier,
                    useDisabledColors);

            double filteredThreadRatio = properties.calculateFilteredThreadRatio(threadArtifactClusters.get(i),
                    (int) numberOfSelectedArtifactThreads, useDisabledColors);
            double filteredRuntimeRatioSum = properties.calculateFilteredSumNumericalMetricRatio(threadArtifactClusters.get(i), primaryMetricIdentifier,
                    useDisabledColors);

            if (i != 0)
            {
                startAngle -= (int) (threadRationFromRunBefore * 360);
            }
            int angle = (int) (360 * filteredThreadRatio) * -1;
            int radius = ThreadVisualizationUtil.metricToDiscreteMetric(filteredRuntimeRatio, ThreadRadarConstants.CIRCLESIZE);
            int radiusSum = ThreadVisualizationUtil.metricToDiscreteMetric(filteredRuntimeRatioSum, ThreadRadarConstants.CIRCLESIZE);

            Color color;
            if (!useDisabledColors)
            {
                //imgG2.setColor(colors[i]);
                color = colors[i];
            } else
            {
                //imgG2.setColor(disabledColors[i]);
                color = disabledColors[i];
            }

            imgG2.setColor(VisualizationUtil.getBackgroundMetricColor(color, .25f));
            imgG2.fillArc(ThreadRadarConstants.MIDDLEPOINT - (radiusSum / 2), ThreadRadarConstants.MIDDLEPOINT - (radiusSum / 2),
                    radiusSum, radiusSum, startAngle, angle);
            imgG2.setColor(color);
            imgG2.fillArc(ThreadRadarConstants.MIDDLEPOINT - (radius / 2), ThreadRadarConstants.MIDDLEPOINT - (radius / 2), radius,
                    radius, startAngle, angle);
            imgG2.setColor(JBColor.BLACK);

            //if (startAngle < 0)
            //    properties.setArcStartAngle(360+startAngle);
            //else
            properties.setArcStartAngle(startAngle);
            properties.setArcAngle(angle);
            threadRationFromRunBefore = filteredThreadRatio;
        }

        imgG2.setColor(JBColor.DARK_GRAY);
        imgG2.drawOval((ThreadRadarConstants.CIRCLE_FRAMESIZE / 2) - (ThreadRadarConstants.CIRCLESIZE / 2),
                (ThreadRadarConstants.CIRCLE_FRAMESIZE / 2) - (ThreadRadarConstants.CIRCLESIZE / 2),
                ThreadRadarConstants.CIRCLESIZE, ThreadRadarConstants.CIRCLESIZE);


        // draw total number of threads label
        Font currentFont = imgG2.getFont();
        Font newFont = currentFont.deriveFont(currentFont.getSize() * ThreadRadarConstants.CIRCLESIZE * 0.02f);
        imgG2.setFont(newFont);
        int labelStartAngle = (int) (ThreadVisualizationUtil.getStartAngle(ThreadRadarConstants.RADIUS,
                ThreadRadarConstants.LABELRADIUS) * -1);//calcStartAngle() * -1; //-65
        int arcAngle = 32;
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

        imgG2.drawLine(x1, y1, x2, y2);
        imgG2.drawLine(x3, y3, x4, y3);
        imgG2.drawLine(x4, y4, x2, y2);

        imgG2.drawArc((ThreadRadarConstants.CIRCLE_FRAMESIZE / 2) - ((ThreadRadarConstants.CIRCLESIZE + 2 * ThreadRadarConstants.LABELDISTANCE) / 2),
                (ThreadRadarConstants.CIRCLE_FRAMESIZE / 2) - ((ThreadRadarConstants.CIRCLESIZE + 2 * ThreadRadarConstants.LABELDISTANCE) / 2),
                ThreadRadarConstants.CIRCLESIZE + 2 * ThreadRadarConstants.LABELDISTANCE,
                ThreadRadarConstants.CIRCLESIZE + 2 * ThreadRadarConstants.LABELDISTANCE, labelStartAngle, arcAngle);

        imgG2.drawString(numberOfSelectedArtifactThreads + "", x1 + 2, y3 - 1);

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

        imgG2.drawLine(x1, y1, x2, y2);
        imgG2.drawLine(x3, y3, x4, y3);
        imgG2.drawLine(x4, y4, x2, y2);

        imgG2.drawArc((ThreadRadarConstants.CIRCLE_FRAMESIZE / 2) - ((ThreadRadarConstants.CIRCLESIZE + 2 * ThreadRadarConstants.LABELDISTANCE) / 2),
                (ThreadRadarConstants.CIRCLE_FRAMESIZE / 2) - ((ThreadRadarConstants.CIRCLESIZE + 2 * ThreadRadarConstants.LABELDISTANCE) / 2),
                ThreadRadarConstants.CIRCLESIZE + 2 * ThreadRadarConstants.LABELDISTANCE,
                ThreadRadarConstants.CIRCLESIZE + 2 * ThreadRadarConstants.LABELDISTANCE, labelStartAngle - arcAngle, arcAngle - 4);

        imgG2.drawString(numberOfSelectedThreadTypes + "", x1 + 2, y3 + 8);

        ImageIcon imageIcon = new ImageIcon(bi);


        JLabel jLabel = new JLabel();
//        {
//            @Override
//            protected void paintComponent(final Graphics g)
//            {
////                g.setColor(getBackground());
////                g.fillRect(0, 0, getWidth(), getHeight());
//                g.drawImage(bi, 0, 0, null);
//                super.paintComponent(g);
//            }
//        };
        jLabel.setOpaque(false);
//        jLabel.getGraphics().drawImage(bi, 0, 0, null);

        jLabel.setIcon(imageIcon);
        jLabel.setSize(imageIcon.getIconWidth(), imageIcon.getIconHeight());
        for (MouseListener mouseListener : jLabel.getMouseListeners())
        {// Is necessary since there is some kind of caching implemented in the jetbrains ide core. Otherwise every listener would
            // trigger each time a click occurs. For each click a new listener will be attached!
            jLabel.removeMouseListener(mouseListener);
        }
        jLabel.addMouseListener(new ThreadRadarMouseListener(jLabel, artifact, radialThreadVisualizationPopupData, primaryMetricIdentifier,
                secondaryMetricIdentifier));

        return jLabel;
    }
}
