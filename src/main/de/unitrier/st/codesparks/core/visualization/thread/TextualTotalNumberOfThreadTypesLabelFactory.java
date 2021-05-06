package de.unitrier.st.codesparks.core.visualization.thread;

import de.unitrier.st.codesparks.core.data.AArtifact;
import de.unitrier.st.codesparks.core.visualization.AArtifactVisualizationLabelFactory;
import de.unitrier.st.codesparks.core.visualization.CodeSparksGraphics;
import de.unitrier.st.codesparks.core.visualization.VisConstants;

import javax.swing.*;

public final class TextualTotalNumberOfThreadTypesLabelFactory extends AArtifactVisualizationLabelFactory
{

    public TextualTotalNumberOfThreadTypesLabelFactory(final int sequence)
    {
        super(null, sequence);
    }

    public TextualTotalNumberOfThreadTypesLabelFactory(final int sequence, final int xOffsetLeft)
    {
        super(null, sequence, xOffsetLeft);
    }

    @Override
    public JLabel createArtifactLabel(final AArtifact artifact)
    {
        int numberOfSelectedThreadTypes = ThreadVisualizationUtil.getNumberOfFilteredThreadTypesInSelection(artifact, null);
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
        final int textYPos = halfLineHeight + halfTextHeight - (int) (Math.floor(halfTextHeight / 2D) - 1);

        final String str = "/" + numberOfSelectedThreadTypes;
        graphics.drawString(str, X_OFFSET_LEFT, textYPos + 1);
        totalWidth += graphics.stringWidth(str);


        return makeLabel(graphics, totalWidth);
    }
}
