package de.unitrier.st.codesparks.core.visualization.thread;

import com.intellij.ui.JBColor;
import com.intellij.ui.paint.PaintUtil;
import com.intellij.util.ui.UIUtil;
import de.unitrier.st.codesparks.core.data.AArtifact;
import de.unitrier.st.codesparks.core.data.ACodeSparksThread;
import de.unitrier.st.codesparks.core.visualization.AArtifactVisualizationLabelFactory;
import de.unitrier.st.codesparks.core.visualization.VisualizationUtil;
import de.unitrier.st.codesparks.core.CoreUtil;
import de.unitrier.st.codesparks.core.data.CodeSparksThreadCluster;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseListener;
import java.awt.image.BufferedImage;
import java.util.Collection;
import java.util.List;

public class RadialThreadVisualizationLabelFactory extends AArtifactVisualizationLabelFactory
{
    private final IRadialThreadVisualizationDisplayData radialThreadVisualizationPopupData;

    public RadialThreadVisualizationLabelFactory(int sequence, boolean isDefault,
                                                 IRadialThreadVisualizationDisplayData radialThreadVisualizationPopupData)
    {
        super(sequence, isDefault);
        this.radialThreadVisualizationPopupData = radialThreadVisualizationPopupData;
    }

    public RadialThreadVisualizationLabelFactory(int sequence, boolean isDefault)
    {
        this(sequence, isDefault, new DefaultRadialThreadVisualizationDisplayData());
    }

    public RadialThreadVisualizationLabelFactory(int sequence)
    {
        this(sequence, false, new DefaultRadialThreadVisualizationDisplayData());
    }

    public RadialThreadVisualizationLabelFactory(IRadialThreadVisualizationDisplayData radialThreadVisualizationPopupData)
    {
        this(-1, false, radialThreadVisualizationPopupData);
    }

    public RadialThreadVisualizationLabelFactory()
    {
        this(-1, false, new DefaultRadialThreadVisualizationDisplayData());
    }

    @Override
    public JLabel createArtifactLabel(@NotNull AArtifact artifact)
    {
        Collection<ACodeSparksThread> codeSparksThreads = artifact.getThreadArtifacts();

        if (codeSparksThreads.isEmpty())
        {
            return emptyLabel();
        }

        List<CodeSparksThreadCluster> codeSparksThreadClusters = artifact.getSortedDefaultThreadArtifactClustering();
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
            numberOfSelectedThreadTypes = artifact.getThreadTypeLists().size();
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
        Color backgroundColor = CoreUtil.getSelectedFileEditorBackgroundColor();
        g2d.setColor(backgroundColor);
        g2d.fillRect(0, 0, bi.getWidth(), bi.getHeight());

        double threadRationFromRunBefore = 0;
        for (int i = 0; i < codeSparksThreadClusters.size(); i++)
        {
            VisualThreadClusterPropertiesManager propertiesManager = VisualThreadClusterPropertiesManager.getInstance();
            RadialVisualThreadClusterProperties properties =
                    new RadialVisualThreadClusterProperties(codeSparksThreadClusters.get(i), colors[i],
                            artifact.getNumberOfThreads());
            propertiesManager.registerProperties(properties);

            //double filteredRuntimeRatio = properties.calculateFilteredRuntimeRatio(threadArtifactClusters.get(i), useDisabledColors);
            double filteredRuntimeRatio = properties.calculateAvgFilteredRuntimeRatio(codeSparksThreadClusters.get(i), useDisabledColors);

            double filteredThreadRatio = properties.calculateFilteredThreadRatio(codeSparksThreadClusters.get(i),
                    (int) numberOfSelectedArtifactThreads, useDisabledColors);
            double filteredRuntimeRatioSum = properties.calculateFilteredSumRuntimeRatio(codeSparksThreadClusters.get(i), useDisabledColors);

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


            g2d.setColor(VisualizationUtil.getBackgroundPerformanceColor(color, .25f));
            g2d.fillArc(RadialThreadVisualizationConstants.MIDDLEPOINT - (radiusSum / 2), RadialThreadVisualizationConstants.MIDDLEPOINT - (radiusSum / 2), radiusSum, radiusSum, startAngle, angle);
            g2d.setColor(color);
            g2d.fillArc(RadialThreadVisualizationConstants.MIDDLEPOINT - (radius / 2), RadialThreadVisualizationConstants.MIDDLEPOINT - (radius / 2), radius, radius, startAngle, angle);
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
                (RadialThreadVisualizationConstants.CIRCLE_FRAMESIZE / 2) - (RadialThreadVisualizationConstants.CIRCLESIZE / 2), RadialThreadVisualizationConstants.CIRCLESIZE, RadialThreadVisualizationConstants.CIRCLESIZE);


        // draw total number of threads label
        Font currentFont = g2d.getFont();
        Font newFont = currentFont.deriveFont(currentFont.getSize() * RadialThreadVisualizationConstants.CIRCLESIZE * 0.02f);
        g2d.setFont(newFont);
        int labelStartAngle = (int) (ThreadVisualizationUtil.getStartAngle(RadialThreadVisualizationConstants.RADIUS, RadialThreadVisualizationConstants.LABELRADIUS) * -1);//calcStartAngle() * -1; //-65
        int arcAngle = 32;
        int x1 = (int) ((RadialThreadVisualizationConstants.LABELRADIUS) * Math.cos(Math.toRadians(-labelStartAngle - arcAngle))) + RadialThreadVisualizationConstants.CIRCLE_FRAMESIZE / 2;
        int y1 = (int) ((RadialThreadVisualizationConstants.LABELRADIUS) * Math.sin(Math.toRadians(-labelStartAngle - arcAngle))) + RadialThreadVisualizationConstants.CIRCLE_FRAMESIZE / 2;
        int x2 = x1 + labelWidth;
        int y2 = y1;

        int x3 = (int) (RadialThreadVisualizationConstants.LABELRADIUS * Math.cos(Math.toRadians(-labelStartAngle))) + RadialThreadVisualizationConstants.CIRCLE_FRAMESIZE / 2;
        int y3 = (int) (RadialThreadVisualizationConstants.LABELRADIUS * Math.sin(Math.toRadians(-labelStartAngle))) + RadialThreadVisualizationConstants.CIRCLE_FRAMESIZE / 2;
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
        labelStartAngle = (int) ThreadVisualizationUtil.getStartAngle(RadialThreadVisualizationConstants.RADIUS, RadialThreadVisualizationConstants.LABELRADIUS);
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
        jLabel.addMouseListener(new RadialThreadVisualizationMouseListener(jLabel, artifact, radialThreadVisualizationPopupData));

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
