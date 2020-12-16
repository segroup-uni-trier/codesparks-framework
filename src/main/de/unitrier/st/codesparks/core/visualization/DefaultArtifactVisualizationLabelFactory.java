package de.unitrier.st.codesparks.core.visualization;

import com.intellij.ui.JBColor;
import com.intellij.ui.paint.PaintUtil;
import com.intellij.util.ui.UIUtil;
import de.unitrier.st.codesparks.core.CoreUtil;
import de.unitrier.st.codesparks.core.data.AArtifact;
import de.unitrier.st.codesparks.core.data.DataUtil;
import de.unitrier.st.codesparks.core.data.IMetricIdentifier;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.Collection;

import static de.unitrier.st.codesparks.core.visualization.VisConstants.*;

/*
 * Copyright (c), Oliver Moseler, 2020
 */
public final class DefaultArtifactVisualizationLabelFactory extends AArtifactVisualizationLabelFactory
{
    private final IMetricIdentifier secondaryMetricIdentifier;

    @SuppressWarnings("unused")
    public DefaultArtifactVisualizationLabelFactory(
            final IMetricIdentifier primaryMetricIdentifier
            , final IMetricIdentifier secondaryMetricIdentifier
    )
    {
        this(primaryMetricIdentifier, secondaryMetricIdentifier, 0);
    }

    public DefaultArtifactVisualizationLabelFactory(
            final IMetricIdentifier primaryMetricIdentifier
            , final IMetricIdentifier secondaryMetricIdentifier
            , final int sequence
    )
    {
        super(primaryMetricIdentifier, sequence);
        this.secondaryMetricIdentifier = secondaryMetricIdentifier;
    }

    @Override
    public JLabel createArtifactLabel(@NotNull final AArtifact artifact)
    {
        int lineHeight = VisConstants.getLineHeight();
        GraphicsConfiguration defaultConfiguration =
                GraphicsEnvironment.getLocalGraphicsEnvironment().getDefaultScreenDevice().getDefaultConfiguration();
        BufferedImage bi = UIUtil.createImage(defaultConfiguration, 5000, lineHeight,
                BufferedImage.TYPE_INT_ARGB, PaintUtil.RoundingMode.CEIL);

        Graphics2D graphics = (Graphics2D) bi.getGraphics();
        VisualizationUtil.drawTransparentBackground(graphics, bi);

        final int selfBarHeight = 2;
        final int X_OFFSET = VisConstants.X_OFFSET;
        final int Y_OFFSET = selfBarHeight + 1;

        /*
         * Draw the intensity rectangle
         */
        final Rectangle intensityRectangle = new Rectangle(X_OFFSET, Y_OFFSET, RECTANGLE_WIDTH, lineHeight - 1 - selfBarHeight);
        final double threadFilteredMetricValue = DataUtil.getThreadFilteredMetricValue(artifact, primaryMetricIdentifier);
        final Color metricColor = VisualizationUtil.getMetricColor(threadFilteredMetricValue);
        graphics.setColor(metricColor);
        VisualizationUtil.fillRectangle(graphics, intensityRectangle);
        /*
         * Draw the self metric
         */
        final double threadFilteredMetricValueSelf = DataUtil.getThreadFilteredMetricValue(artifact, secondaryMetricIdentifier);
        final double selfPercentage = threadFilteredMetricValueSelf / threadFilteredMetricValue;
        int selfWidth = 0;
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
        graphics.setColor(VisualizationUtil.getBackgroundMetricColor(metricColor, .1f));
        graphics.drawLine(X_OFFSET + selfWidth, 0, X_OFFSET + RECTANGLE_WIDTH, 0);
        graphics.drawLine(X_OFFSET + selfWidth, 0, X_OFFSET + RECTANGLE_WIDTH, 0);
        graphics.setColor(BORDER_COLOR);
        graphics.drawRect(X_OFFSET, Y_OFFSET, RECTANGLE_WIDTH, lineHeight - Y_OFFSET - 1);
        /*
         * Draw the text
         */
        String percentageText = CoreUtil.formatPercentage(threadFilteredMetricValue);
        double textWidth = graphics.getFontMetrics().stringWidth(percentageText);
        graphics.setColor(JBColor.DARK_GRAY);
        Font font = new Font("Arial", Font.BOLD, 11);  // TODO: support different font sizes
        graphics.setFont(font);
        Color textColor = VisualizationUtil.getTextColor(metricColor);
        graphics.setColor(textColor);
        graphics.drawString(percentageText, X_OFFSET + 1 + (int) ((RECTANGLE_WIDTH / 2d) - (textWidth / 2d)),
                Y_OFFSET + (int) ((lineHeight - Y_OFFSET) * .75d));
        graphics.setColor(STANDARD_FONT_COLOR);
        /*
         * Draw caller and callee triangles
         */
        drawPredecessors(artifact, intensityRectangle, graphics, lineHeight, metricColor);
        drawSuccessors(artifact, intensityRectangle, graphics, lineHeight, metricColor);
        /*
         * Set the actual image icon size
         */
        int actualIconWidth = X_OFFSET + RECTANGLE_WIDTH + 4 * CALLEE_TRIANGLES_WIDTH + 1;
        BufferedImage subImage = bi.getSubimage(0, 0, actualIconWidth, bi.getHeight());
        ImageIcon imageIcon = new ImageIcon(subImage);

        JLabel jLabel = new JLabel();
        jLabel.setIcon(imageIcon);

        jLabel.setSize(imageIcon.getIconWidth(), imageIcon.getIconHeight());
        jLabel.addMouseListener(new DefaultArtifactVisualizationMouseListener(jLabel, artifact, primaryMetricIdentifier, secondaryMetricIdentifier));

        return jLabel;
    }

    private static void drawPredecessors(
            final AArtifact artifact
            , final Rectangle visualizationArea
            , final Graphics graphics
            , final int lineHeight
            , final Color performanceColor
    )
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

    private static void drawSuccessors(
            final AArtifact artifact
            , final Rectangle visualizationArea
            , final Graphics graphics
            , final int lineHeight
            , final Color performanceColor
    )
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

    private static Triangle getTriangle(final int x, final int y, final int size)
    {
        return new Triangle(new int[]{x, x + size, x}, new int[]{y, y + size, y + 2 * size}, 3);
    }

    private static void fillTriangle(final Triangle triangle, final Graphics graphics)
    {
        graphics.fillPolygon(triangle.xPoints, triangle.yPoints, triangle.nPoints);
    }

    private static void drawTriangle(final Triangle triangle, final Graphics graphics)
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