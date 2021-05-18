package de.unitrier.st.codesparks.core.visualization.thread;

import de.unitrier.st.codesparks.core.data.AArtifact;
import de.unitrier.st.codesparks.core.data.AMetricIdentifier;
import de.unitrier.st.codesparks.core.visualization.AArtifactVisualizationLabelFactory;
import de.unitrier.st.codesparks.core.visualization.CodeSparksGraphics;
import de.unitrier.st.codesparks.core.visualization.VisConstants;

import javax.swing.*;

public class DiscreteNumberOfThreadTypesLabelFactory extends AArtifactVisualizationLabelFactory
{
    public DiscreteNumberOfThreadTypesLabelFactory(final AMetricIdentifier primaryMetricIdentifier)
    {
        super(primaryMetricIdentifier);
    }

    public DiscreteNumberOfThreadTypesLabelFactory(final AMetricIdentifier primaryMetricIdentifier, final int sequence)
    {
        super(primaryMetricIdentifier, sequence);
    }

    public DiscreteNumberOfThreadTypesLabelFactory(final AMetricIdentifier primaryMetricIdentifier, final int sequence, final int xOffsetLeft)
    {
        super(primaryMetricIdentifier, sequence, xOffsetLeft);
    }

    @Override
    public JLabel createArtifactLabel(final AArtifact artifact)
    {
        final int width = 200;
        final int height = VisConstants.getLineHeight();
        int totalWidth = X_OFFSET_LEFT;

        final CodeSparksGraphics graphics = getGraphics(width, height);

        final ImageIcon firstThread = new ImageIcon(getClass().getResource("/icons/thread-type.png"));

        graphics.drawImage(firstThread.getImage(), X_OFFSET_LEFT, 2, null);
        totalWidth += firstThread.getIconWidth();

        int numberOfSelectedThreadTypes = ThreadVisualizationUtil.getNumberOfSelectedThreadTypesWithNumericMetricValueInSelection(artifact,
                primaryMetricIdentifier);
        //getNumberOfSelectedThreadTypesInSelection(artifact);
        if (numberOfSelectedThreadTypes == 0)
        {
            numberOfSelectedThreadTypes = artifact.getThreadTypeLists().size();
        }
        numberOfSelectedThreadTypes = 4;

        if (numberOfSelectedThreadTypes > 1)
        {
            final ImageIcon secondThread = new ImageIcon(getClass().getResource("/icons/thread-type_2.png"));

            //graphics.setColor(Color.decode("#297b48"));
            graphics.drawImage(secondThread.getImage(), X_OFFSET_LEFT + firstThread.getIconWidth(), 2, null);
            totalWidth += secondThread.getIconWidth();

            if (numberOfSelectedThreadTypes > 2)
            {
                final ImageIcon thirdThread = new ImageIcon(getClass().getResource("/icons/thread-type_3.png"));
                graphics.drawImage(thirdThread.getImage(), X_OFFSET_LEFT + firstThread.getIconWidth() + secondThread.getIconWidth(), 2, null);
                totalWidth += thirdThread.getIconWidth();

                if (numberOfSelectedThreadTypes > 3)
                {
                    graphics.setColor(VisConstants.BORDER_COLOR);
                    final String plusSymbol = "\u002b";
                    graphics.drawString(plusSymbol, X_OFFSET_LEFT + firstThread.getIconWidth() + secondThread.getIconWidth() + thirdThread.getIconWidth(), 7);
                    totalWidth += graphics.stringWidth(plusSymbol);
                }
            }
        }

        return makeLabel(graphics, totalWidth);
    }
}
