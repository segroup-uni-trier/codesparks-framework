package de.unitrier.st.codesparks.core.visualization.thread;

import com.intellij.ui.JBColor;
import com.intellij.ui.paint.PaintUtil;
import com.intellij.util.ui.UIUtil;
import de.unitrier.st.codesparks.core.data.*;
import de.unitrier.st.codesparks.core.logging.CodeSparksLogger;
import de.unitrier.st.codesparks.core.visualization.AArtifactVisualizationLabelFactory;
import de.unitrier.st.codesparks.core.visualization.VisualizationUtil;
import org.jetbrains.annotations.NotNull;

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
    private final IMetricIdentifier secondaryMetricIdentifier;

    public ThreadRadarLabelFactory(
            final IMetricIdentifier primaryMetricIdentifier
            , final IMetricIdentifier secondaryMetricIdentifier
    )
    {
        this(primaryMetricIdentifier, secondaryMetricIdentifier, 0, false, new DefaultThreadRadarDisplayData(primaryMetricIdentifier));
    }

    public ThreadRadarLabelFactory(
            final IMetricIdentifier primaryMetricIdentifier
            , final IMetricIdentifier secondaryMetricIdentifier
            , final int sequence
    )
    {
        this(primaryMetricIdentifier, secondaryMetricIdentifier, sequence, false, new DefaultThreadRadarDisplayData(primaryMetricIdentifier));
    }

    public ThreadRadarLabelFactory(
            final IMetricIdentifier primaryMetricIdentifier
            , final IMetricIdentifier secondaryMetricIdentifier
            , final int sequence
            , boolean isDefault
            , final IThreadRadarDisplayData radialThreadVisualizationPopupData
    )
    {
        super(primaryMetricIdentifier, sequence, isDefault);
        this.secondaryMetricIdentifier = secondaryMetricIdentifier;
        this.radialThreadVisualizationPopupData = radialThreadVisualizationPopupData;
    }

//    public ThreadRadarLabelFactory(
//            final int sequence
//            , final boolean isDefault
//            , final String primaryMetricIdentifier
//            , final String secondaryMetricIdentifier
//    )
//    {
//        super(sequence, isDefault, primaryMetricIdentifier);
//        this.radialThreadVisualizationPopupData = new DefaultThreadRadarDisplayData(primaryMetricIdentifier);
//        this.secondaryMetricIdentifier = secondaryMetricIdentifier;
//    }


    @Override
    public JLabel createArtifactLabel(
            @NotNull final AArtifact artifact
    )
    {
        if (!(artifact instanceof ASourceCodeArtifact))
        {
            CodeSparksLogger.addText("%s: The artifact has to be of type '%s' but is of type '%s'.",
                    getClass()
                    , ASourceCodeArtifact.class.getSimpleName()
                    , artifact.getClass().getSimpleName()
            );
            return new JLabel();
        }

        final ASourceCodeArtifact scArtifact = (ASourceCodeArtifact) artifact;


        Collection<AThreadArtifact> codeSparksThreads = scArtifact.getThreadArtifacts();

        if (codeSparksThreads.isEmpty())
        {
            return emptyLabel();
        }

        List<CodeSparksThreadCluster> codeSparksThreadClusters = scArtifact.getSortedDefaultThreadArtifactClustering(primaryMetricIdentifier);
        int startAngle = 90;
        boolean useDisabledColors = false;
        JBColor[] colors = {new JBColor(Color.decode("#5F4E95"), Color.decode("#5F4E95")), new JBColor(Color.decode("#B25283"),
                Color.decode("#B25283")), new JBColor(Color.decode("#3E877F"), Color.decode("#3E877F"))};
        JBColor[] disabledColors = {new JBColor(Color.decode("#999999"), Color.decode("#999999")), new JBColor(Color.decode("#777777"),
                Color.decode("#777777")), new JBColor(Color.decode("#555555"), Color.decode("#555555"))};

        long numberOfSelectedArtifactThreads = scArtifact.getThreadArtifacts().stream().filter(t -> !t.isFiltered()).count();
        int numberOfSelectedThreadTypes = ThreadVisualizationUtil.getNumberOfSelectedThreadTypes(scArtifact, null);

        if (numberOfSelectedArtifactThreads == 0)
        {
            numberOfSelectedArtifactThreads = scArtifact.getNumberOfThreads();
            Map<String, List<AThreadArtifact>> threadTypeLists = scArtifact.getThreadTypeLists();
            numberOfSelectedThreadTypes = threadTypeLists == null ? 0 : threadTypeLists.size();
            useDisabledColors = true;
        }

        String completeNumberOfThreadsString = numberOfSelectedArtifactThreads + "";

        final int frameSize = RadialThreadVisualizationConstants.FRAME + completeNumberOfThreadsString.length() * 5;
        final int labelWidth = 5 + completeNumberOfThreadsString.length() * 5;

        GraphicsConfiguration defaultConfiguration =
                GraphicsEnvironment.getLocalGraphicsEnvironment().getDefaultScreenDevice().getDefaultConfiguration();
        BufferedImage bi = UIUtil.createImage(defaultConfiguration, frameSize, RadialThreadVisualizationConstants.CIRCLE_FRAMESIZE,
                BufferedImage.TYPE_INT_RGB, PaintUtil.RoundingMode.CEIL);

        Graphics graphics = bi.getGraphics();
        Graphics2D g2d = (Graphics2D) graphics;
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        Color backgroundColor = VisualizationUtil.getSelectedFileEditorBackgroundColor();
        g2d.setColor(backgroundColor);
        g2d.fillRect(0, 0, bi.getWidth(), bi.getHeight());

        double threadRationFromRunBefore = 0;
        for (int i = 0; i < codeSparksThreadClusters.size(); i++)
        {
            VisualThreadClusterPropertiesManager propertiesManager = VisualThreadClusterPropertiesManager.getInstance();
            RadialVisualThreadClusterProperties properties =
                    new RadialVisualThreadClusterProperties(codeSparksThreadClusters.get(i), colors[i],
                            scArtifact.getNumberOfThreads(), primaryMetricIdentifier);
            propertiesManager.registerProperties(properties);

            //double filteredRuntimeRatio = properties.calculateFilteredRuntimeRatio(threadArtifactClusters.get(i), useDisabledColors);
            double filteredRuntimeRatio = properties.calculateAvgFilteredNumericalMetricRatio(codeSparksThreadClusters.get(i), primaryMetricIdentifier,
                    useDisabledColors);

            double filteredThreadRatio = properties.calculateFilteredThreadRatio(codeSparksThreadClusters.get(i),
                    (int) numberOfSelectedArtifactThreads, useDisabledColors);
            double filteredRuntimeRatioSum = properties.calculateFilteredSumNumericalMetricRatio(codeSparksThreadClusters.get(i), primaryMetricIdentifier,
                    useDisabledColors);

            if (i != 0)
            {
                startAngle -= (int) (threadRationFromRunBefore * 360);
            }
            int angle = (int) (360 * filteredThreadRatio) * -1;
            int radius = ThreadVisualizationUtil.metricToDiscreteMetric(filteredRuntimeRatio, RadialThreadVisualizationConstants.CIRCLESIZE);
            int radiusSum = ThreadVisualizationUtil.metricToDiscreteMetric(filteredRuntimeRatioSum, RadialThreadVisualizationConstants.CIRCLESIZE);

            Color color;
            if (!useDisabledColors)
            {
                //g2d.setColor(colors[i]);
                color = colors[i];
            } else
            {
                //g2d.setColor(disabledColors[i]);
                color = disabledColors[i];
            }


            g2d.setColor(VisualizationUtil.getBackgroundMetricColor(color, .25f));
            g2d.fillArc(RadialThreadVisualizationConstants.MIDDLEPOINT - (radiusSum / 2), RadialThreadVisualizationConstants.MIDDLEPOINT - (radiusSum / 2),
                    radiusSum, radiusSum, startAngle, angle);
            g2d.setColor(color);
            g2d.fillArc(RadialThreadVisualizationConstants.MIDDLEPOINT - (radius / 2), RadialThreadVisualizationConstants.MIDDLEPOINT - (radius / 2), radius,
                    radius, startAngle, angle);
            g2d.setColor(JBColor.BLACK);

            //if (startAngle < 0)
            //    properties.setArcStartAngle(360+startAngle);
            //else
            properties.setArcStartAngle(startAngle);
            properties.setArcAngle(angle);
            threadRationFromRunBefore = filteredThreadRatio;
        }

        g2d.setColor(JBColor.DARK_GRAY);
        g2d.drawOval((RadialThreadVisualizationConstants.CIRCLE_FRAMESIZE / 2) - (RadialThreadVisualizationConstants.CIRCLESIZE / 2),
                (RadialThreadVisualizationConstants.CIRCLE_FRAMESIZE / 2) - (RadialThreadVisualizationConstants.CIRCLESIZE / 2),
                RadialThreadVisualizationConstants.CIRCLESIZE, RadialThreadVisualizationConstants.CIRCLESIZE);


        // draw total number of threads label
        Font currentFont = g2d.getFont();
        Font newFont = currentFont.deriveFont(currentFont.getSize() * RadialThreadVisualizationConstants.CIRCLESIZE * 0.02f);
        g2d.setFont(newFont);
        int labelStartAngle = (int) (ThreadVisualizationUtil.getStartAngle(RadialThreadVisualizationConstants.RADIUS,
                RadialThreadVisualizationConstants.LABELRADIUS) * -1);//calcStartAngle() * -1; //-65
        int arcAngle = 32;
        int x1 =
                (int) ((RadialThreadVisualizationConstants.LABELRADIUS) * Math.cos(Math.toRadians(-labelStartAngle - arcAngle))) + RadialThreadVisualizationConstants.CIRCLE_FRAMESIZE / 2;
        int y1 =
                (int) ((RadialThreadVisualizationConstants.LABELRADIUS) * Math.sin(Math.toRadians(-labelStartAngle - arcAngle))) + RadialThreadVisualizationConstants.CIRCLE_FRAMESIZE / 2;
        int x2 = x1 + labelWidth;
        int y2 = y1;

        int x3 =
                (int) (RadialThreadVisualizationConstants.LABELRADIUS * Math.cos(Math.toRadians(-labelStartAngle))) + RadialThreadVisualizationConstants.CIRCLE_FRAMESIZE / 2;
        int y3 =
                (int) (RadialThreadVisualizationConstants.LABELRADIUS * Math.sin(Math.toRadians(-labelStartAngle))) + RadialThreadVisualizationConstants.CIRCLE_FRAMESIZE / 2;
        int x4 = x3 + (x1 - x3) + labelWidth;
        int y4 = y3;

        g2d.drawLine(x1, y1, x2, y2);
        g2d.drawLine(x3, y3, x4, y3);
        g2d.drawLine(x4, y4, x2, y2);

        g2d.drawArc((RadialThreadVisualizationConstants.CIRCLE_FRAMESIZE / 2) - ((RadialThreadVisualizationConstants.CIRCLESIZE + 2 * RadialThreadVisualizationConstants.LABELDISTANCE) / 2),
                (RadialThreadVisualizationConstants.CIRCLE_FRAMESIZE / 2) - ((RadialThreadVisualizationConstants.CIRCLESIZE + 2 * RadialThreadVisualizationConstants.LABELDISTANCE) / 2), RadialThreadVisualizationConstants.CIRCLESIZE + 2 * RadialThreadVisualizationConstants.LABELDISTANCE,
                RadialThreadVisualizationConstants.CIRCLESIZE + 2 * RadialThreadVisualizationConstants.LABELDISTANCE, labelStartAngle, arcAngle);

        g2d.drawString(numberOfSelectedArtifactThreads + "", x1 + 2, y3 - 1);

        // draw number of different classes label
        labelStartAngle = (int) ThreadVisualizationUtil.getStartAngle(RadialThreadVisualizationConstants.RADIUS,
                RadialThreadVisualizationConstants.LABELRADIUS);
        x1 = (int) ((RadialThreadVisualizationConstants.LABELRADIUS) * Math.cos(Math.toRadians(-(labelStartAngle - arcAngle)))) + RadialThreadVisualizationConstants.CIRCLE_FRAMESIZE / 2;
        y1 = (int) ((RadialThreadVisualizationConstants.LABELRADIUS) * Math.sin(Math.toRadians(-(labelStartAngle - arcAngle)))) + RadialThreadVisualizationConstants.CIRCLE_FRAMESIZE / 2;
        x2 = x1 + labelWidth;
        y2 = y1;

        x3 = (int) (RadialThreadVisualizationConstants.LABELRADIUS * Math.cos(Math.toRadians(-labelStartAngle))) + RadialThreadVisualizationConstants.CIRCLE_FRAMESIZE / 2;
        y3 = (int) (RadialThreadVisualizationConstants.LABELRADIUS * Math.sin(Math.toRadians(-labelStartAngle))) + RadialThreadVisualizationConstants.CIRCLE_FRAMESIZE / 2;
        x4 = x3 + (x1 - x3) + labelWidth;
        y4 = y3;

        g2d.drawLine(x1, y1, x2, y2);
        g2d.drawLine(x3, y3, x4, y3);
        g2d.drawLine(x4, y4, x2, y2);

        g2d.drawArc((RadialThreadVisualizationConstants.CIRCLE_FRAMESIZE / 2) - ((RadialThreadVisualizationConstants.CIRCLESIZE + 2 * RadialThreadVisualizationConstants.LABELDISTANCE) / 2),
                (RadialThreadVisualizationConstants.CIRCLE_FRAMESIZE / 2) - ((RadialThreadVisualizationConstants.CIRCLESIZE + 2 * RadialThreadVisualizationConstants.LABELDISTANCE) / 2), RadialThreadVisualizationConstants.CIRCLESIZE + 2 * RadialThreadVisualizationConstants.LABELDISTANCE,
                RadialThreadVisualizationConstants.CIRCLESIZE + 2 * RadialThreadVisualizationConstants.LABELDISTANCE, labelStartAngle - arcAngle, arcAngle - 4);

        g2d.drawString(numberOfSelectedThreadTypes + "", x1 + 2, y3 + 8);

        ImageIcon imageIcon = new ImageIcon(bi);
        JLabel jLabel = new JLabel();
        jLabel.setIcon(imageIcon);
        jLabel.setSize(imageIcon.getIconWidth(), imageIcon.getIconHeight());
        for (MouseListener mouseListener : jLabel.getMouseListeners())
        {// Is necessary since there is some kind of caching implemented in the jetbrains ide core. Otherwise every listener would
            // trigger each time a click occurs. For each click a new listener will be attached!
            jLabel.removeMouseListener(mouseListener);
        }
        jLabel.addMouseListener(new ThreadRadarMouseListener(jLabel, scArtifact, radialThreadVisualizationPopupData, primaryMetricIdentifier,
                secondaryMetricIdentifier));

        return jLabel;
    }

    private JLabel emptyLabel()
    {
        JLabel jLabel = new JLabel();
        jLabel.setIcon(new ImageIcon());
        jLabel.setSize(0, 0);
        return jLabel;
    }
}
