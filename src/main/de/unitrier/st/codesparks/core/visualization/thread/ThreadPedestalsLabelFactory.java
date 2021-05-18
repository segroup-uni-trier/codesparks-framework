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
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public final class ThreadPedestalsLabelFactory extends AArtifactVisualizationLabelFactory
{
    public ThreadPedestalsLabelFactory(final AMetricIdentifier metricIdentifier)
    {
        super(metricIdentifier);
    }

    public ThreadPedestalsLabelFactory(final AMetricIdentifier metricIdentifier, final int sequence)
    {
        super(metricIdentifier, sequence);
    }

    public ThreadPedestalsLabelFactory(final AMetricIdentifier metricIdentifier, final int sequence, final int xOffsetLeft)
    {
        super(metricIdentifier, sequence, xOffsetLeft);
    }

    @Override
    public JLabel createArtifactLabel(final AArtifact artifact)
    {
        final List<AThreadArtifact> threadArtifacts = new ArrayList<>(artifact.getThreadArtifacts());

        if (threadArtifacts.isEmpty())
        {
            return emptyLabel();
        }

        long numberOfSelectedArtifactThreads =
                artifact.getThreadArtifacts().stream().filter(t -> t.getNumericalMetricValue(primaryMetricIdentifier) > 0 && !t.isFiltered()).count();
        int numberOfSelectedThreadTypes = ThreadVisualizationUtil.getNumberOfSelectedThreadTypesWithNumericMetricValueInSelection(artifact,
                primaryMetricIdentifier);

        if (numberOfSelectedArtifactThreads == 0)
        { // In case any thread is deselected, i.e. where for all threads thr the method call thr.isFiltered() yields true
            numberOfSelectedArtifactThreads = artifact.getNumberOfThreads();
            Map<String, List<AThreadArtifact>> threadTypeLists = artifact.getThreadTypeLists();
            numberOfSelectedThreadTypes = threadTypeLists == null ? 0 : threadTypeLists.size();
        }

        final String numberOfSelectedArtifactThreadsString =
                numberOfSelectedThreadTypes +
                        //"\u00a6"
                        "/"
                        //"\u01c0 "
                        + numberOfSelectedArtifactThreads;

        final int threadsPerColumn = 3;
        final int X_OFFSET_LEFT = this.X_OFFSET_LEFT + 0;
        final int PEDESTAL_START_OFFSET_LEFT = 2;
        final int PEDESTAL_WIDTH_TO_TEXT = 15;

        final int lineHeight = VisualizationUtil.getLineHeightFloor(VisConstants.getLineHeight(), threadsPerColumn);

//        final GraphicsConfiguration defaultConfiguration =
//                GraphicsEnvironment.getLocalGraphicsEnvironment().getDefaultScreenDevice().getDefaultConfiguration();
//        final BufferedImage bi = UIUtil.createImage(defaultConfiguration, 300, lineHeight,
//                BufferedImage.TYPE_INT_ARGB, PaintUtil.RoundingMode.CEIL);
//
//        final Graphics2D graphics = (Graphics2D) bi.getGraphics();
//        graphics.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
//        // Draw the fully transparent background
//        VisualizationUtil.drawTransparentBackground(graphics, bi);

        final CodeSparksGraphics graphics = getGraphics(300, lineHeight);

        graphics.setColor(VisConstants.BORDER_COLOR);
        final Font currentFont = graphics.getFont();
        final Font newFont = currentFont.deriveFont(currentFont.getSize() * 0.9f);
        graphics.setFont(newFont);

        final int textHeight = graphics.getFontMetrics().getHeight();

        final String hashSymbol = "\u0023"; // '\u0023' equals the '#' symbol
        graphics.drawString(hashSymbol, X_OFFSET_LEFT, lineHeight / 2 + (textHeight / 2) - 2);

        final int textLength = graphics.getFontMetrics().stringWidth(numberOfSelectedArtifactThreadsString);
        int pedestalWidth = PEDESTAL_WIDTH_TO_TEXT + textLength;

        // Draw bottom line
        graphics.drawLine(X_OFFSET_LEFT + PEDESTAL_START_OFFSET_LEFT, lineHeight - 1, X_OFFSET_LEFT + PEDESTAL_START_OFFSET_LEFT + pedestalWidth,
                lineHeight - 1);
        // Draw ceil line
        graphics.drawLine(X_OFFSET_LEFT + PEDESTAL_START_OFFSET_LEFT, 0, X_OFFSET_LEFT + PEDESTAL_START_OFFSET_LEFT + pedestalWidth, 0);
        // Draw back line
        graphics.drawLine(X_OFFSET_LEFT + PEDESTAL_START_OFFSET_LEFT + pedestalWidth, 0, X_OFFSET_LEFT + PEDESTAL_START_OFFSET_LEFT + pedestalWidth,
                lineHeight - 1);

        final int diameter = lineHeight - 1;
        final int radius = lineHeight / 2;

        graphics.drawArc(-radius + X_OFFSET_LEFT + PEDESTAL_START_OFFSET_LEFT, 0, diameter, diameter, -90, 180);

        graphics.drawString(numberOfSelectedArtifactThreadsString, X_OFFSET_LEFT + PEDESTAL_START_OFFSET_LEFT + radius + 3,
                lineHeight / 2 + (textHeight / 2) - 3);

        // Creation of the label

//        BufferedImage subimage = bi.getSubimage(0, 0, X_OFFSET_LEFT + PEDESTAL_START_OFFSET_LEFT + pedestalWidth + 1, bi.getHeight());
//        ImageIcon imageIcon = new ImageIcon(subimage);
//
//        JLabel jLabel = new JLabel();
//        jLabel.setIcon(imageIcon);
//
//        jLabel.setSize(imageIcon.getIconWidth(), imageIcon.getIconHeight());

        //noinspection UnnecessaryLocalVariable -> debugging purpose
        final JLabel jLabel = makeLabel(graphics, X_OFFSET_LEFT + PEDESTAL_START_OFFSET_LEFT + pedestalWidth + 1);

        return jLabel;
    }
}
