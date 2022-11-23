/*
 * Copyright (c) 2022. Oliver Moseler
 */
package de.unitrier.st.codesparks.core.visualization;

import com.intellij.ui.JBColor;
import de.unitrier.st.codesparks.core.data.AArtifact;
import de.unitrier.st.codesparks.core.data.AMetricIdentifier;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;
import java.awt.*;

import static de.unitrier.st.codesparks.core.visualization.VisConstants.*;

public class DefaultArtifactVisualizationLabelFactory extends AArtifactVisualizationLabelFactory
{
    private final IMetricValueColorCodingStrategy metricValueColorCodingStrategy;

    @SafeVarargs
    public DefaultArtifactVisualizationLabelFactory(
            final AMetricIdentifier primaryMetricIdentifier,
            final int sequence,
            final int xOffsetLeft,
            final IMetricValueColorCodingStrategy metricValueColorCodingStrategy,
            final Class<? extends AArtifact>... artifactClasses
    )
    {
        super(primaryMetricIdentifier, sequence, xOffsetLeft, artifactClasses);
        this.metricValueColorCodingStrategy = metricValueColorCodingStrategy;
    }

    public DefaultArtifactVisualizationLabelFactory(
            final AMetricIdentifier primaryMetricIdentifier,
            final IMetricValueColorCodingStrategy metricValueColorCodingStrategy
    )
    {
        super(primaryMetricIdentifier);
        this.metricValueColorCodingStrategy = metricValueColorCodingStrategy;
    }

    public DefaultArtifactVisualizationLabelFactory(
            final AMetricIdentifier primaryMetricIdentifier
    )
    {
        super(primaryMetricIdentifier);
        this.metricValueColorCodingStrategy = null;
    }

    @Override
    public JLabel createArtifactLabel(@NotNull final AArtifact artifact)
    {
        // The default line height of Intellij IDEA source-code editors.
        final int lineHeight = VisConstants.getLineHeight();
        final int X_OFFSET = VisConstants.X_OFFSET;
        final int Y_OFFSET = 3;
        /*
         * Retrieve the metric value, its textual representation and its text width.
         */

        final Object metricValue = artifact.getMetricValue(primaryMetricIdentifier);
        final String metricValueText = primaryMetricIdentifier.getShortDisplayString() + ": " + primaryMetricIdentifier.getValueDisplayString(metricValue);
        final CodeSparksGraphics graphics = getGraphics(lineHeight);
        final double metricValueTextWidth = graphics.stringWidth(metricValueText);
        /*
         * Draw the rectangular colored area
         */
        final int HORIZONTAL_PADDING = 6;
        final int rectangleWidth = (int) (HORIZONTAL_PADDING + metricValueTextWidth + HORIZONTAL_PADDING);
        final Rectangle frame = new Rectangle(X_OFFSET, Y_OFFSET, rectangleWidth, lineHeight - 3);
        final Color metricValueColor;
        if (metricValueColorCodingStrategy != null)
        {
            metricValueColor = metricValueColorCodingStrategy.getMetricValueColor(metricValue);
        } else
        {
            metricValueColor = JBColor.LIGHT_GRAY;
        }
        graphics.setColor(metricValueColor);
        graphics.fillRectangle(frame);
        /*
         * Draw the rectangular border (frame)
         */
        graphics.setColor(BORDER_COLOR);
        graphics.drawRect(X_OFFSET, Y_OFFSET, rectangleWidth, lineHeight - Y_OFFSET - 1);
        /*
         * Draw the text, centered
         */
        final Color textColor = VisualizationUtil.getTextColor(metricValueColor);
        graphics.setColor(textColor);
        graphics.drawString(metricValueText,
                X_OFFSET + (int) ((rectangleWidth / 2d) - (metricValueTextWidth / 2d)),
                Y_OFFSET + (int) ((lineHeight - Y_OFFSET) * .75d)
        );
        graphics.setColor(STANDARD_FONT_COLOR);
        /*
         * Create the actual label.
         */
        final JLabel jLabel = makeLabel(graphics, X_OFFSET + rectangleWidth + 2); // +2 for the border. 1 left + 1 right
        /*
         * Add details on demand via popup accessible by a mouse click.
         */
        jLabel.addMouseListener(new DefaultArtifactVisualizationMouseListener(jLabel, artifact, primaryMetricIdentifier));
        return jLabel;
    }

}