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
import java.util.ArrayList;
import java.util.List;

public final class TotalNumberOfThreadTypesLabelFactory extends AArtifactVisualizationLabelFactory
{
    public TotalNumberOfThreadTypesLabelFactory(final AMetricIdentifier primaryMetricIdentifier)
    {
        super(primaryMetricIdentifier);
    }

    public TotalNumberOfThreadTypesLabelFactory(final AMetricIdentifier primaryMetricIdentifier, final int sequence)
    {
        super(primaryMetricIdentifier, sequence);
    }

    public TotalNumberOfThreadTypesLabelFactory(final AMetricIdentifier primaryMetricIdentifier, final int sequence, final int xOffsetLeft)
    {
        super(primaryMetricIdentifier, sequence, xOffsetLeft);
    }

    @Override
    public JLabel createArtifactLabel(final AArtifact artifact)
    {
        int numberOfSelectedThreadTypes = ThreadVisualizationUtil.getNumberOfSelectedThreadTypes(artifact, null);
        if (numberOfSelectedThreadTypes == 0)
        {
            numberOfSelectedThreadTypes = artifact.getThreadTypeLists().size();
        }
        if (numberOfSelectedThreadTypes == 0)
        {
            return emptyLabel();
        }

        final int lineHeight = VisConstants.getLineHeight();

        final CodeSparksGraphics graphics = getGraphics(300, lineHeight);

        int totalWidth = X_OFFSET_LEFT;

        graphics.setColor(VisConstants.BORDER_COLOR);

//        final Font formerFont = graphics.getFont();
//        Font newFont = formerFont.deriveFont(formerFont.getSize() * 0.9f);
//        graphics.setFont(newFont);

        final int textHeight = graphics.getFontMetrics().getHeight();
        final int halfLineHeight = (int) Math.ceil(lineHeight / 2D);
        final int halfTextHeight = (int) Math.ceil(textHeight / 2D);
        final int textYPos = halfLineHeight + halfTextHeight- (int)(Math.floor(halfTextHeight / 2D) - 1);

        final String str = "/" + numberOfSelectedThreadTypes;
        graphics.drawString(str, X_OFFSET_LEFT, textYPos + 1);
        totalWidth += graphics.stringWidth(str);



        return makeLabel(graphics, totalWidth);
    }
}
