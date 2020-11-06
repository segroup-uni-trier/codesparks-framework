package de.unitrier.st.codesparks.core.visualization.thread;

import com.intellij.ui.JBColor;
import com.intellij.ui.paint.PaintUtil;
import com.intellij.util.ui.UIUtil;
import de.unitrier.st.codesparks.core.data.AProfilingArtifact;
import de.unitrier.st.codesparks.core.data.ThreadArtifact;
import de.unitrier.st.codesparks.core.visualization.VisualizationUtil;
import de.unitrier.st.codesparks.core.data.ThreadArtifactCluster;

import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.Arrays;
import java.util.Set;

public abstract class ARadialThreadVisualization extends JPanel
{
    protected Graphics2D g2d;
    protected AProfilingArtifact artifact;
    private int circleDiameter = 0;
    private int circleFrameSize = 0;
    private int panelHeight = 0;
    private int panelWidth = 0;
    private int middlePoint = 0;
    private int radiusZoomed = 0;
    private int labelDistance = 0;
    private int labelRadius = 0;

    protected void setUpVisualizationParameter(int diameter, int panelWidthOffset)
    {
        circleFrameSize = diameter;
        panelHeight = diameter + 4;
        panelWidth = panelHeight + panelWidthOffset;
        circleDiameter = diameter;
        middlePoint = circleFrameSize / 2;
        radiusZoomed = circleDiameter / 2;
        labelDistance = 5;
        labelRadius = radiusZoomed + labelDistance;
        initGraphics2D();
    }

    @Override
    protected void paintComponent(Graphics g)
    {
        super.paintComponent(g);
        drawRectangleBackground();
    }

    private static final ImageIcon defaultIcon = new ImageIcon(ARadialThreadVisualization.class.getResource("/icons/pluginIcon.png"));

    private void initGraphics2D()
    {
        panelHeight = Math.max(panelHeight, defaultIcon.getIconHeight());
        GraphicsConfiguration defaultConfiguration =
                GraphicsEnvironment.getLocalGraphicsEnvironment().getDefaultScreenDevice().getDefaultConfiguration();
        BufferedImage bi = UIUtil.createImage(defaultConfiguration, panelWidth, panelHeight, BufferedImage.TYPE_INT_RGB,
                PaintUtil.RoundingMode.CEIL);
        ImageIcon imageIcon = new ImageIcon(bi);
        add(new JLabel(imageIcon));
        Graphics graphics = bi.getGraphics();
        g2d = (Graphics2D) graphics;
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        paintDefault();
    }

    protected void paintDefault()
    {
        g2d.drawImage(defaultIcon.getImage(), (panelWidth - defaultIcon.getIconWidth()) / 2,
                (panelHeight - defaultIcon.getIconHeight()) / 2, null);
    }

    private void drawRectangleBackground()
    {
        g2d.setColor(Color.decode("#F0F0F0"));
        g2d.fillRect(0, 0, panelWidth, panelHeight);
    }

    protected void drawOverallCircle()
    {
        g2d.setColor(JBColor.DARK_GRAY);
        g2d.drawOval((circleFrameSize / 2) - (circleDiameter / 2), (circleFrameSize / 2) - (circleDiameter / 2), circleDiameter,
                circleDiameter);
    }

    void drawInnerCircle()
    {
        g2d.setColor(Color.decode("#e4e4e4"));
        g2d.drawOval((int) ((circleFrameSize / 2) - (circleDiameter * 0.65 / 2)),
                (int) ((circleFrameSize / 2) - (circleDiameter * 0.65 / 2)),
                (int) (circleDiameter * 0.65), (int) (circleDiameter * 0.65));
        g2d.setColor(JBColor.DARK_GRAY);
    }

    protected void drawArcForSumAndAvg(Color color, int radiusSum, int radius, int startAngle, int angle)
    {
        g2d.setColor(VisualizationUtil.getBackgroundPerformanceColor(color, .25f));
        g2d.fillArc(middlePoint - (radiusSum / 2), middlePoint - (radiusSum / 2), radiusSum, radiusSum,
                startAngle, angle);
        g2d.setColor(color);
        g2d.fillArc(middlePoint - (radius / 2), middlePoint - (radius / 2), radius, radius, startAngle,
                angle);
    }

    protected void drawHoverCluster(int markedStartAngle, int markedAngle, long numberOfFilteredArtifactThreads, int markedRadius)
    {
        Stroke defaultStroke = g2d.getStroke();
        if (markedStartAngle != -1 && markedAngle != -360 && numberOfFilteredArtifactThreads > 0)
        {
            g2d.setColor(new JBColor(new Color(203, 119, 48), new Color(203, 119, 48)));
            Stroke stroke = new BasicStroke(2);
            g2d.setStroke(stroke);

            int x1 = middlePoint;
            int y1 = middlePoint;
            int x2 = (int) (middlePoint + (markedRadius / 2 - 1) * Math.cos(Math.toRadians(360 - markedStartAngle)));
            int y2 = (int) (middlePoint + (markedRadius / 2 - 1) * Math.sin(Math.toRadians(360 - markedStartAngle)));
            int x3 = (int) (middlePoint + (markedRadius / 2 - 1) * Math.cos(Math.toRadians(360 - (markedStartAngle + markedAngle))));
            int y3 = (int) (middlePoint + (markedRadius / 2 - 1) * Math.sin(Math.toRadians(360 - (markedStartAngle + markedAngle))));

            g2d.drawArc((middlePoint - (markedRadius / 2)), (middlePoint - (markedRadius / 2)), markedRadius,
                    markedRadius, markedStartAngle+1, markedAngle-1);
            g2d.drawLine(x1, y1, x2, y2);
            g2d.drawLine(x1, y1, x3, y3);
            g2d.setStroke(defaultStroke);
        }

        if (markedAngle == -360)
        {
            final Stroke oldStroke = g2d.getStroke();
            Stroke stroke = new BasicStroke(3);
            g2d.setStroke(stroke);
            g2d.setColor(new JBColor(new Color(203, 119, 48), new Color(203, 119, 48)));
            g2d.drawOval((middlePoint - (markedRadius / 2)+1), (middlePoint - (markedRadius / 2)+1), markedRadius-3,
                    markedRadius-3);
            g2d.setStroke(oldStroke);
        }

        g2d.setColor(JBColor.BLACK);
    }

    protected void drawNumberOfThreadsLabel(int labelWidth, float fontSize, long numberOfFilteredArtifactThreads, int yOffsetForLabelText)
    {
        drawPedestal(labelWidth, false, fontSize, numberOfFilteredArtifactThreads, yOffsetForLabelText);
    }

    protected void drawNumberOfDifferentThreadTypesLabel(int labelWidth, float fontSize, long numberOfFilteredArtifactThreads,
                                                         int yOffsetForLabelText)
    {
        drawPedestal(labelWidth, true, fontSize, numberOfFilteredArtifactThreads, yOffsetForLabelText);
    }

    private void drawPedestal(int labelWidth, boolean top, float fontSize, long number, int offset) {
        final int factor = top ? -1 : 1;
        final int adjustment = top ? -1 : 0;
        int labelStartAngle = (int) ThreadVisualizationUtil.getStartAngle(radiusZoomed, labelRadius);

        int arcAngle = 32;
        final double angle = Math.toRadians(factor * (labelStartAngle - arcAngle));
        int x1 = (int) ((labelRadius) * Math.cos(angle)+adjustment) + circleFrameSize / 2;
        int y1 = (int) ((labelRadius) * Math.sin(angle)+adjustment) + circleFrameSize / 2;
        int x2 = x1 + labelWidth;
        @SuppressWarnings("UnnecessaryLocalVariable") int y2 = y1;

        int x3 = (int) (labelRadius * Math.cos(Math.toRadians(factor * labelStartAngle))+adjustment) + circleFrameSize / 2;
        int y3 = (int) (labelRadius * Math.sin(Math.toRadians(factor * labelStartAngle))+adjustment) + circleFrameSize / 2;
        int x4 = x3 + (x1 - x3) + labelWidth;
        @SuppressWarnings("UnnecessaryLocalVariable") int y4 = y3;

        g2d.drawLine(x1, y1, x2, y2);
        g2d.drawLine(x3, y3, x4, y3);
        g2d.drawLine(x4, y4, x2, y2);

        int start;
        if (top) {
            start = labelStartAngle-arcAngle;
        } else {
            start = -labelStartAngle;
        }
        g2d.drawArc((circleFrameSize / 2) - ((circleDiameter + 2 * labelDistance) / 2),
                (circleFrameSize / 2) - ((circleDiameter + 2 * labelDistance) / 2), circleDiameter + 2 * labelDistance,
                circleDiameter + 2 * labelDistance, start, arcAngle+adjustment);

        g2d.setFont(g2d.getFont().deriveFont(fontSize));
        g2d.drawString(number + "", x1+2, y3+offset);
    }

    protected int getRadius(double filteredRuntimeRatio)
    {
        return ThreadVisualizationUtil.metricToDiscreteMetric(filteredRuntimeRatio, circleDiameter);
    }

    protected int getSumRadius(double filteredRuntimeRatioSum)
    {
        return ThreadVisualizationUtil.metricToDiscreteMetric(filteredRuntimeRatioSum, circleDiameter);
    }

    @SuppressWarnings("unused")
    static double calculateFilteredMedianRuntimeRatio(ThreadArtifactCluster cluster)
    {
        int unfilteredThreads = 0;
        for (ThreadArtifact threadArtifact : cluster)
        {
            if (threadArtifact.isFiltered())
                continue;

            unfilteredThreads++;
        }

        Double[] metricArray = new Double[cluster.size()];
        for (int i = 0; i < unfilteredThreads; i++)
        {
            if (cluster.get(i).isFiltered())
                continue;

            metricArray[i] = cluster.get(i).getMetricValue();
        }

        Arrays.sort(metricArray);
        double median;
        if (metricArray.length % 2 == 0)
            median = (metricArray[metricArray.length / 2] + metricArray[metricArray.length / 2 - 1]) / 2;
        else
            median = metricArray[metricArray.length / 2];

        return median;
    }

    static double getFilteredMetricSumOfCluster(ThreadArtifactCluster cluster, Set<ThreadArtifact> filteredThreads)
    {
        double metric = 0;
        for (ThreadArtifact threadArtifact : cluster)
        {
            if (filteredThreads.contains(threadArtifact))
                continue;
            metric += threadArtifact.getMetricValue();
        }
        return metric;
    }
}
