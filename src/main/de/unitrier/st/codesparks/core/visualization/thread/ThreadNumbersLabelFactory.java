/*
 * Copyright (c) 2021. Oliver Moseler
 */

package de.unitrier.st.codesparks.core.visualization.thread;

import de.unitrier.st.codesparks.core.data.AArtifact;
import de.unitrier.st.codesparks.core.data.AThreadArtifact;
import de.unitrier.st.codesparks.core.visualization.AArtifactVisualizationLabelFactory;
import de.unitrier.st.codesparks.core.visualization.CodeSparksGraphics;
import de.unitrier.st.codesparks.core.visualization.VisConstants;
import de.unitrier.st.codesparks.core.visualization.VisualizationUtil;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public final class ThreadNumbersLabelFactory extends AArtifactVisualizationLabelFactory
{
    public ThreadNumbersLabelFactory()
    {
        super(null);
    }

    public ThreadNumbersLabelFactory(final int sequence)
    {
        super(null, sequence);
    }

    public ThreadNumbersLabelFactory(final int sequence, final int xOffsetLeft)
    {
        super(null, sequence, xOffsetLeft);
    }

    @Override
    public JLabel createArtifactLabel(final AArtifact artifact)
    {
        final List<AThreadArtifact> threadArtifacts = new ArrayList<>(artifact.getThreadArtifacts());
        if (threadArtifacts.isEmpty())
        {
            return emptyLabel();
        }

        long numberOfSelectedArtifactThreads = artifact.getThreadArtifacts().stream().filter(t -> !t.isFiltered()).count();
        int numberOfSelectedThreadTypes = ThreadVisualizationUtil.getNumberOfSelectedThreadTypes(artifact, null);

        if (numberOfSelectedArtifactThreads == 0)
        { // In case any thread is deselected, i.e. where for all threads thr the method call thr.isFiltered() yields true
            numberOfSelectedArtifactThreads = artifact.getNumberOfThreads();
            Map<String, List<AThreadArtifact>> threadTypeLists = artifact.getThreadTypeLists();
            numberOfSelectedThreadTypes = threadTypeLists == null ? 0 : threadTypeLists.size();
        }

        final String sumAndColonString = "\u2211 \u003a";

        final int threadsPerColumn = 3;
        final int X_OFFSET_LEFT = this.X_OFFSET_LEFT + 0;
        final int TEXT_START_OFFSET_LEFT = 1;

        final int lineHeight = VisualizationUtil.getLineHeightCeil(VisConstants.getLineHeight(), threadsPerColumn);

        final CodeSparksGraphics graphics = getGraphics(300, lineHeight);

        int textWidth = 0;

        graphics.setColor(VisConstants.BORDER_COLOR);

        final Font formerFont = graphics.getFont();
        Font newFont = formerFont.deriveFont(formerFont.getSize() * 0.9f);
        graphics.setFont(newFont);

        final int textHeight = graphics.getFontMetrics().getHeight();
        final int halfLineHeight = (int) Math.ceil(lineHeight / 2D);
        final int halfTextHeight = (int) Math.ceil(textHeight / 2D);
        final int textYPos = halfLineHeight + halfTextHeight- (int)(Math.floor(halfTextHeight / 2D) - 1);

        graphics.drawString(sumAndColonString, X_OFFSET_LEFT + TEXT_START_OFFSET_LEFT, textYPos);

        textWidth += graphics.getFontMetrics().stringWidth(sumAndColonString);

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
