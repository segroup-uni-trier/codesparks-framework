/*
 * Copyright (c) 2021. Oliver Moseler
 */

package de.unitrier.st.codesparks.core.visualization.thread;

import de.unitrier.st.codesparks.core.data.AArtifact;
import de.unitrier.st.codesparks.core.data.AMetricIdentifier;
import de.unitrier.st.codesparks.core.data.AThreadArtifact;
import de.unitrier.st.codesparks.core.visualization.AArtifactVisualizationLabelFactory;
import de.unitrier.st.codesparks.core.visualization.CodeSparksGraphics;
import de.unitrier.st.codesparks.core.visualization.VisConstants;
import de.unitrier.st.codesparks.core.visualization.VisualizationUtil;

import javax.swing.*;
import java.awt.*;
import java.util.Collection;

public final class TextualTotalNumberOfThreadsLabelFactory extends AArtifactVisualizationLabelFactory
{
    @SuppressWarnings("unused")
    public TextualTotalNumberOfThreadsLabelFactory(final AMetricIdentifier metricIdentifier)
    {
        super(metricIdentifier);
    }

    public TextualTotalNumberOfThreadsLabelFactory(final AMetricIdentifier metricIdentifier, final int sequence)
    {
        super(metricIdentifier, sequence);
    }

    @SuppressWarnings("unused")
    public TextualTotalNumberOfThreadsLabelFactory(final AMetricIdentifier metricIdentifier, final int sequence, final int xOffsetLeft)
    {
        super(metricIdentifier, sequence, xOffsetLeft);
    }

    @Override
    public JLabel createArtifactLabel(final AArtifact artifact)
    {
        final Collection<AThreadArtifact> artifactsWithNumericMetricValue = artifact.getThreadArtifactsWithNumericMetricValue(primaryMetricIdentifier);
        long numberOfSelectedArtifactThreads =
                artifactsWithNumericMetricValue.stream().filter(AThreadArtifact::isSelected).count();
        if (numberOfSelectedArtifactThreads == 0)
        { // In case any thread is deselected, i.e. where for all threads thr the method call thr.isFiltered() yields true
            numberOfSelectedArtifactThreads = artifactsWithNumericMetricValue.size();
        }
        if (numberOfSelectedArtifactThreads == 0)
        {
            return emptyLabel();
        }

        final String sumAndColonString = "";//"\u2211 \u003a";

        final int threadsPerColumn = 3;
        //noinspection PointlessArithmeticExpression
        final int X_OFFSET_LEFT = this.X_OFFSET_LEFT + 0;
        final int TEXT_START_OFFSET_LEFT = 1;

        final int lineHeight = VisualizationUtil.getLineHeightCeil(VisConstants.getLineHeight(), threadsPerColumn);

        final CodeSparksGraphics graphics = getGraphics(300, lineHeight);

        int textWidth = 0;

        graphics.setColor(VisConstants.BORDER_COLOR);

        final Font formerFont = graphics.getFont();
        Font newFont = formerFont.deriveFont(formerFont.getSize() * 0.9f);
        graphics.setFont(newFont);

        final int fontHeight = graphics.fontHeight();
        final int halfLineHeight = (int) Math.ceil(lineHeight / 2D);
        final int halfFontHeight = (int) Math.ceil(fontHeight / 2D);
        final int textYPos = halfLineHeight + halfFontHeight - (int) (Math.floor(halfFontHeight / 2D) - 1);

        graphics.drawString(sumAndColonString, X_OFFSET_LEFT + TEXT_START_OFFSET_LEFT, textYPos - 1);

        textWidth += graphics.stringWidth(sumAndColonString);

        newFont = formerFont.deriveFont(formerFont.getSize() * 1.0f);
        graphics.setFont(newFont);

        final String totalNumberOfThreadsString = String.valueOf(numberOfSelectedArtifactThreads);
        graphics.drawString(totalNumberOfThreadsString, X_OFFSET_LEFT + TEXT_START_OFFSET_LEFT + textWidth, textYPos + 1);

        textWidth += graphics.getFontMetrics().stringWidth(totalNumberOfThreadsString);

        int totalWidth = X_OFFSET_LEFT + TEXT_START_OFFSET_LEFT + textWidth;

        // Creation of the label

        return makeLabel(graphics, totalWidth);
    }
}