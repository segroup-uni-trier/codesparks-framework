/*
 * Copyright (c) 2021. Oliver Moseler
 */

package de.unitrier.st.codesparks.core.visualization.thread;

import de.unitrier.st.codesparks.core.data.AArtifact;
import de.unitrier.st.codesparks.core.data.AMetricIdentifier;
import de.unitrier.st.codesparks.core.visualization.AArtifactVisualizationLabelFactory;
import de.unitrier.st.codesparks.core.visualization.CodeSparksGraphics;
import de.unitrier.st.codesparks.core.visualization.VisConstants;

import javax.swing.*;

public class SimpleThreadListLabelFactory extends AArtifactVisualizationLabelFactory
{
    public SimpleThreadListLabelFactory(final AMetricIdentifier primaryMetricIdentifier)
    {
        super(primaryMetricIdentifier);
    }

    public SimpleThreadListLabelFactory(final AMetricIdentifier primaryMetricIdentifier, final int sequence)
    {
        super(primaryMetricIdentifier, sequence);
    }

    public SimpleThreadListLabelFactory(final AMetricIdentifier primaryMetricIdentifier, final int sequence, final int xOffsetLeft)
    {
        super(primaryMetricIdentifier, sequence, xOffsetLeft);
    }

    @Override
    public JLabel createArtifactLabel(final AArtifact artifact)
    {
        final int lineHeight = VisConstants.getLineHeight();
        final int width = 50;


        final CodeSparksGraphics graphics = super.getGraphics(width, lineHeight);

        graphics.setColor(VisConstants.BORDER_COLOR);

        graphics.drawRect(0, 0, width - 1, lineHeight - 1);

        final String text = "threads";

        final int textHeight = graphics.getFontMetrics().getHeight();
        final int textWidth = graphics.getFontMetrics().stringWidth(text);

        graphics.drawString(text, (width - textWidth) / 2, (lineHeight + textHeight) / 2 - 3);

        final JLabel jLabel = makeLabel(graphics);

        jLabel.addMouseListener(new SimpleThreadVisualizationMouseListener(jLabel, artifact, primaryMetricIdentifier));


        return jLabel;
    }
}
