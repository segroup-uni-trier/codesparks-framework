/*
 * Copyright (c) 2021. Oliver Moseler
 */
package de.unitrier.st.codesparks.core.visualization.thread;

import de.unitrier.st.codesparks.core.CoreUtil;
import de.unitrier.st.codesparks.core.data.AArtifact;
import de.unitrier.st.codesparks.core.data.AMetricIdentifier;
import de.unitrier.st.codesparks.core.visualization.AArtifactVisualizationLabelFactory;
import de.unitrier.st.codesparks.core.visualization.CodeSparksGraphics;
import de.unitrier.st.codesparks.core.visualization.VisConstants;

import javax.swing.*;
import java.net.URL;

@SuppressWarnings("unused")
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

        final Class<? extends DiscreteNumberOfThreadTypesLabelFactory> aClass = getClass();
        if (aClass == null)
        {
            return emptyLabel();
        }

        URL resource = aClass.getResource("/icons/thread-type.png");
        ImageIcon firstThread;
        if (resource != null)
        {
            firstThread = new ImageIcon(resource);
        } else
        {
            firstThread = CoreUtil.getDefaultImageIcon();
        }

        graphics.drawImage(firstThread.getImage(), X_OFFSET_LEFT, 2, null);
        totalWidth += firstThread.getIconWidth();

        int numberOfSelectedThreadTypes = ThreadVisualizationUtil.getNumberOfSelectedThreadTypesWithNumericMetricValueInSelection(artifact,
                primaryMetricIdentifier);
        //getNumberOfSelectedThreadTypesInSelection(artifact);
        if (numberOfSelectedThreadTypes == 0)
        {
            numberOfSelectedThreadTypes = artifact.getThreadTypeLists().size();
        }
        // numberOfSelectedThreadTypes = 4;

        if (numberOfSelectedThreadTypes > 1)
        {
            resource = aClass.getResource("/icons/thread-type_2.png");
            ImageIcon secondThread;
            if (resource != null)
            {
                secondThread = new ImageIcon(resource);
            } else
            {
                secondThread = CoreUtil.getDefaultImageIcon();
            }

            //graphics.setColor(Color.decode("#297b48"));
            graphics.drawImage(secondThread.getImage(), X_OFFSET_LEFT + firstThread.getIconWidth(), 2, null);
            totalWidth += secondThread.getIconWidth();

            if (numberOfSelectedThreadTypes > 2)
            {
                resource = aClass.getResource("/icons/thread-type_3.png");
                ImageIcon thirdThread;
                if (resource != null)
                {
                    thirdThread = new ImageIcon(resource);
                } else
                {
                    thirdThread = CoreUtil.getDefaultImageIcon();
                }
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
