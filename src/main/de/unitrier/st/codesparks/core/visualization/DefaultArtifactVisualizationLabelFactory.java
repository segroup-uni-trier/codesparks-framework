/*
 * Copyright (C) 2020, Oliver Moseler
 */
package de.unitrier.st.codesparks.core.visualization;

import com.intellij.ui.paint.PaintUtil;
import com.intellij.util.ui.UIUtil;
import de.unitrier.st.codesparks.core.data.AArtifact;
import de.unitrier.st.codesparks.core.data.DataUtil;
import de.unitrier.st.codesparks.core.CoreUtil;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.Collection;

import static com.intellij.ui.JBColor.BLACK;
import static de.unitrier.st.codesparks.core.visualization.VisConstants.*;

public final class DefaultArtifactVisualizationLabelFactory extends AArtifactVisualizationLabelFactory
{
    @SuppressWarnings("WeakerAccess")
    public DefaultArtifactVisualizationLabelFactory() { }

    public DefaultArtifactVisualizationLabelFactory(int sequence)
    {
        this(sequence, false);
    }

    public DefaultArtifactVisualizationLabelFactory(int sequence, boolean isDefault)
    {
        super(sequence, isDefault);
    }

//    private static Map<AProfilingArtifact, ImageIcon> artifactImageIconCache = new HashMap<>();
//
//    public static void clearCache()
//    {
//        artifactImageIconCache.clear();
//    }
//
//    public ImageIcon createArtifactImageIcon(@NotNull AProfilingArtifact artifact, boolean fromCache)
//    {
//        if (fromCache)
//        {
//            ImageIcon imageIcon = artifactImageIconCache.get(artifact);
//            if (imageIcon != null)
//            {
//                return imageIcon;
//            }
//        }
//        return createArtifactImageIcon(artifact);
//    }

    @Override
    public JLabel createArtifactLabel(@NotNull AArtifact artifact, String... metricIdentifiers)
    {
        if (metricIdentifiers.length < 2)
        {
            // TODO: return empty label and print message to logger
        }

        int lineHeight = VisConstants.getLineHeight();

        GraphicsConfiguration defaultConfiguration =
                GraphicsEnvironment.getLocalGraphicsEnvironment().getDefaultScreenDevice().getDefaultConfiguration();
        BufferedImage bi = UIUtil.createImage(defaultConfiguration, 5000, lineHeight,
                BufferedImage.TYPE_INT_RGB, PaintUtil.RoundingMode.CEIL);

        Graphics graphics = bi.getGraphics();

        Color backgroundColor = CoreUtil.getSelectedFileEditorBackgroundColor();

        graphics.setColor(backgroundColor);
        graphics.fillRect(0, 0, bi.getWidth(), bi.getHeight());

        int selfBarHeight = 2;
        final int X_OFFSET = VisConstants.X_OFFSET;
        final int Y_OFFSET = selfBarHeight + 1;

        Rectangle artifactVisualizationArea = new Rectangle(X_OFFSET, Y_OFFSET, RECTANGLE_WIDTH, lineHeight - 1 - selfBarHeight);

//        final double metricValue = artifact.getMetricValue();
//        final double threadMetricValueRatio = DataUtil.getThreadMetricValueRatio(artifact, ThreadArtifact::getMetricValue);
//        final double threadFilteredMetricValue = metricValue * threadMetricValueRatio;



        final String primaryMetricIdentifier = metricIdentifiers[0];

        final double threadFilteredMetricValue = DataUtil.getThreadFilteredMetricValue(artifact);

        String percentageText = CoreUtil.formatPercentage(threadFilteredMetricValue);
        /*
         * Draw the intensity rectangle
         */
        Color performanceColor = VisualizationUtil.getPerformanceColor(threadFilteredMetricValue);
        graphics.setColor(performanceColor);
        VisualizationUtil.fillRectangle(graphics, artifactVisualizationArea);
        /*
         * Draw the self metric
         */
        // double log = Math.log(1 + ((Math.E - 1) * metricValueSelf / metricValue));
        // int selfWidth = RECTANGLE_WIDTH * log;
        int selfWidth = 0;

//        final double metricValueSelf = artifact.getMetricValueSelf();
//        final double threadMetricValueSelfRatio = DataUtil.getThreadMetricValueRatio(artifact, ThreadArtifact::getMetricValueSelf);
//        final double threadFilteredMetricValueSelf = metricValueSelf * threadMetricValueSelfRatio;

        final double threadFilteredMetricValueSelf = DataUtil.getThreadFilteredMetricValueSelf(artifact);

        double selfPercentage = threadFilteredMetricValueSelf / threadFilteredMetricValue;
        if (selfPercentage > 0D)
        {
            if (selfPercentage < 1D)
            {
                int discrete = (int) ((selfPercentage * 100) / 10) + 1;
                selfWidth = RECTANGLE_WIDTH / 10 * discrete;
            } else
            {
                selfWidth = RECTANGLE_WIDTH;
            }
        }
        if (selfWidth > 0)
        {
            graphics.drawLine(X_OFFSET, 0, X_OFFSET + selfWidth, 0);
            graphics.drawLine(X_OFFSET, 0, X_OFFSET + selfWidth, 0);
        }
        graphics.setColor(VisualizationUtil.getBackgroundPerformanceColor(performanceColor, .1f));
        graphics.drawLine(X_OFFSET + selfWidth, 0, X_OFFSET + RECTANGLE_WIDTH, 0);
        graphics.drawLine(X_OFFSET + selfWidth, 0, X_OFFSET + RECTANGLE_WIDTH, 0);
        graphics.setColor(BORDER_COLOR);
        graphics.drawRect(X_OFFSET, Y_OFFSET, RECTANGLE_WIDTH, lineHeight - Y_OFFSET - 1);
        /*
         * Draw the text
         */
        double textWidth = graphics.getFontMetrics().stringWidth(percentageText);
        graphics.setColor(BLACK);
        Font font = new Font("Arial", Font.BOLD, 11);  // TODO: support different font sizes
        graphics.setFont(font);
        Color textColor = VisualizationUtil.getTextColor(performanceColor);
        graphics.setColor(textColor);
        graphics.drawString(percentageText, X_OFFSET + 1 + (int) ((RECTANGLE_WIDTH / 2d) - (textWidth / 2d)),
                Y_OFFSET + (int) ((lineHeight - Y_OFFSET) * .75d));
        graphics.setColor(STANDARD_FONT_COLOR);
        /*
         * Draw caller and callee triangles
         */
        drawCallers(artifact, artifactVisualizationArea, graphics, lineHeight, performanceColor);
        drawCallees(artifact, artifactVisualizationArea, graphics, lineHeight, performanceColor);
        /*
         * Set the actual image icon size
         */
        int actualIconWidth = X_OFFSET + RECTANGLE_WIDTH + 4 * CALLEE_TRIANGLES_WIDTH + 1;
        BufferedImage subImage = bi.getSubimage(0, 0, actualIconWidth, bi.getHeight());
        ImageIcon imageIcon = new ImageIcon(subImage);

//        artifactImageIconCache.put(artifact, imageIcon);

        JLabel jLabel = new JLabel();
        jLabel.setIcon(imageIcon);

        jLabel.setSize(imageIcon.getIconWidth(), imageIcon.getIconHeight());
        jLabel.addMouseListener(new DefaultArtifactVisualizationMouseListener(jLabel, artifact));

        return jLabel;
    }

//    private static void fillStripedRectangle(@NotNull Graphics graphics, Rectangle rect,
//                                             double stripesBrightness, Color performanceColor)
//    {
//        graphics.setColor(performanceColor);
//        fillRectangle(graphics, rect);
//        for (int y = rect.y; y <= rect.y + rect.height; y++)
//        {
//            double r = Math.sqrt(1 - Math.abs((y - rect.y)
//                    / ((double) rect.height) * 2 - 1));
//            graphics.setColor(changeColorBrightness(graphics.getColor(),
//                    stripesBrightness + (1 - stripesBrightness) * 0.7 * r));
//            for (int x = rect.x; x <= rect.x + rect.width; x++)
//            {
//                if ((x + y) / 3 % 3 == 0)
//                {
//                    // Same as drawing a point!
//                    graphics.fillRect(x, y, 1, 1);
//                }
//            }
//        }
//    }

//    @NotNull
//    private static Color changeColorBrightness(@NotNull Color c, double factor)
//    {
//        return new JBColor(new Color(
//                Math.min((int) (c.getRed() * factor), 255),
//                Math.min((int) (c.getGreen() * factor), 255),
//                Math.min((int) (c.getBlue() * factor), 255)), Gray._128);
//    }

    private static void drawCallers(@NotNull AArtifact artifact, Rectangle visualizationArea, Graphics graphics,
                                    int lineHeight, Color performanceColor)
    {
        long predecessorSize = artifact.getPredecessors()
                .values()
                .stream()
                .flatMap(Collection::stream)
                .filter(npa -> npa.getThreadArtifacts()
                        .stream()
                        .anyMatch(threadArtifact -> !threadArtifact.isFiltered()))
                .count();
        if (predecessorSize >= 2)
        {
            Triangle trianglePointsExtra1 = getTriangle(
                    visualizationArea.x - 5,
                    visualizationArea.y - 1, 3);
            graphics.setColor(performanceColor);
            fillTriangle(trianglePointsExtra1, graphics);
            graphics.setColor(BORDER_COLOR);
            drawTriangle(trianglePointsExtra1, graphics);
            Triangle trianglePointsExtra2 = getTriangle(
                    visualizationArea.x - 5,
                    visualizationArea.y + 7, 3);
            graphics.setColor(performanceColor);
            fillTriangle(trianglePointsExtra2, graphics);
            graphics.setColor(BORDER_COLOR);
            drawTriangle(trianglePointsExtra2, graphics);
        }
        if (predecessorSize == 1 || predecessorSize > 2)
        {
            Triangle trianglePoints = getTriangle(visualizationArea.x - 6,
                    visualizationArea.y + 15 / 2 - 5, 4);
            graphics.setColor(performanceColor);
            fillTriangle(trianglePoints, graphics);
            graphics.setColor(BORDER_COLOR);
            drawTriangle(trianglePoints, graphics);
        }
    }

    private static void drawCallees(@NotNull AArtifact artifact, Rectangle visualizationArea, Graphics graphics,
                                    int lineHeight, Color performanceColor)
    {
        long successorSize =
                artifact.getSuccessors()
                        .values()
                        .stream()
                        .flatMap(Collection::stream)
                        .filter(npa -> !npa.getName().toLowerCase().startsWith("self"))
                        .filter(npa -> npa.getThreadArtifacts()
                                .stream()
                                .anyMatch(threadArtifact -> !threadArtifact.isFiltered()))
                        .count();
        if (successorSize >= 2)
        {
            Triangle trianglePoints = getTriangle(visualizationArea.x
                            + visualizationArea.width + CALLEE_TRIANGLES_WIDTH,
                    visualizationArea.y - 1, 3);
            graphics.setColor(performanceColor);
            fillTriangle(trianglePoints, graphics);
            graphics.setColor(BORDER_COLOR);
            drawTriangle(trianglePoints, graphics);
            Triangle trianglePointsExtra2 = getTriangle(visualizationArea.x
                            + visualizationArea.width + CALLEE_TRIANGLES_WIDTH,
                    visualizationArea.y + 7, 3);
            graphics.setColor(performanceColor);
            fillTriangle(trianglePointsExtra2, graphics);
            graphics.setColor(BORDER_COLOR);
            drawTriangle(trianglePointsExtra2, graphics);
        }
        if (successorSize == 1 || successorSize > 2)
        {
            Triangle trianglePoints = getTriangle(visualizationArea.x
                            + visualizationArea.width + CALLEE_TRIANGLES_WIDTH + 1,
                    visualizationArea.y + 15 / 2 - 5, 4);
            graphics.setColor(performanceColor);
            fillTriangle(trianglePoints, graphics);
            graphics.setColor(BORDER_COLOR);
            drawTriangle(trianglePoints, graphics);
        }
    }

    @NotNull
    private static Triangle getTriangle(int x, int y, int size)
    {
        return new Triangle(new int[]{x, x + size, x}, new int[]{y, y + size, y + 2 * size}, 3);
    }

    private static void fillTriangle(@NotNull Triangle triangle, @NotNull Graphics graphics)
    {
        graphics.fillPolygon(triangle.xPoints, triangle.yPoints, triangle.nPoints);
    }

    private static void drawTriangle(@NotNull Triangle triangle, @NotNull Graphics graphics)
    {
        graphics.drawPolygon(triangle.xPoints, triangle.yPoints, triangle.nPoints);
    }

    private static class Triangle
    {
        int[] xPoints;
        int[] yPoints;
        int nPoints;

        Triangle(int[] xPoints, int[] yPoints, int nPoints)
        {
            this.xPoints = xPoints;
            this.yPoints = yPoints;
            this.nPoints = nPoints;
        }
    }
}