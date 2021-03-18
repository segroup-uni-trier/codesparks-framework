/*
 * Copyright (c) 2021. Oliver Moseler
 */

package de.unitrier.st.codesparks.core.visualization.thread;

import com.intellij.ui.JBColor;
import com.intellij.ui.paint.PaintUtil;
import com.intellij.util.ui.UIUtil;
import de.unitrier.st.codesparks.core.data.AArtifact;
import de.unitrier.st.codesparks.core.data.AMetricIdentifier;
import de.unitrier.st.codesparks.core.data.AThreadArtifact;
import de.unitrier.st.codesparks.core.data.ThreadArtifactCluster;
import de.unitrier.st.codesparks.core.visualization.AArtifactVisualizationLabelFactory;
import de.unitrier.st.codesparks.core.visualization.VisConstants;
import de.unitrier.st.codesparks.core.visualization.VisualizationUtil;
import de.unitrier.st.codesparks.core.visualization.popup.ThreadColor;

import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public final class ThreadPedestalsLabelFactory extends AArtifactVisualizationLabelFactory
{
    public ThreadPedestalsLabelFactory(final AMetricIdentifier primaryMetricIdentifier)
    {
        super(primaryMetricIdentifier);
    }

    public ThreadPedestalsLabelFactory(final AMetricIdentifier primaryMetricIdentifier, final int sequence)
    {
        super(primaryMetricIdentifier, sequence);
    }

    @Override
    public JLabel createArtifactLabel(final AArtifact artifact)
    {
        final List<AThreadArtifact> threadArtifacts = new ArrayList<>(artifact.getThreadArtifacts());

        if (threadArtifacts.isEmpty())
        {
            return emptyLabel();
        }

        final GraphicsConfiguration defaultConfiguration =
                GraphicsEnvironment.getLocalGraphicsEnvironment().getDefaultScreenDevice().getDefaultConfiguration();

        long numberOfSelectedArtifactThreads = artifact.getThreadArtifacts().stream().filter(t -> !t.isFiltered()).count();
        int numberOfSelectedThreadTypes = ThreadVisualizationUtil.getNumberOfSelectedThreadTypes(artifact, null);

        if (numberOfSelectedArtifactThreads == 0)
        { // In case any thread is deselected, i.e. where for all threads thr the method call thr.isFiltered() yields true
            numberOfSelectedArtifactThreads = artifact.getNumberOfThreads();
            Map<String, List<AThreadArtifact>> threadTypeLists = artifact.getThreadTypeLists();
            numberOfSelectedThreadTypes = threadTypeLists == null ? 0 : threadTypeLists.size();
        }

        final String numberOfSelectedArtifactThreadsString =
                String.valueOf(numberOfSelectedThreadTypes) + "|" + String.valueOf(numberOfSelectedArtifactThreads);


        final int threadsPerColumn = 3;
        final int X_OFFSET_LEFT = 4;
        final int PEDESTAL_OFFSET_LEFT = 15;
        final int PEDESTAL_OFFSET_RIGHT = 5;


        final int pedestalWidth = PEDESTAL_OFFSET_LEFT + numberOfSelectedArtifactThreadsString.length() * 3 + PEDESTAL_OFFSET_RIGHT;
        final int lineHeight = VisualizationUtil.getLineHeightFloor(VisConstants.getLineHeight(), threadsPerColumn);

        final BufferedImage bi = UIUtil.createImage(defaultConfiguration, X_OFFSET_LEFT + pedestalWidth, lineHeight,
                BufferedImage.TYPE_INT_ARGB, PaintUtil.RoundingMode.CEIL);

        final Graphics2D graphics = (Graphics2D) bi.getGraphics();
        graphics.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        // Draw the fully transparent background
        VisualizationUtil.drawTransparentBackground(graphics, bi);
        graphics.setColor(VisConstants.BORDER_COLOR);
        final Font currentFont = graphics.getFont();
        final Font newFont = currentFont.deriveFont(currentFont.getSize() * 0.9f);
        graphics.setFont(newFont);
        graphics.getFontMetrics().stringWidth(numberOfSelectedArtifactThreadsString);
        final int textHeight = graphics.getFontMetrics().getHeight();

        graphics.drawString("\u0023", 1, lineHeight / 2 + (textHeight / 2) - 2 );




        // Draw bottom line
        graphics.drawLine(X_OFFSET_LEFT, lineHeight - 1, X_OFFSET_LEFT + pedestalWidth, lineHeight - 1);
        // Draw ceil line
        graphics.drawLine(X_OFFSET_LEFT, 0, X_OFFSET_LEFT + pedestalWidth, 0);
        // Draw back line
        graphics.drawLine(X_OFFSET_LEFT + pedestalWidth - 1, 0, X_OFFSET_LEFT + pedestalWidth - 1, lineHeight - 1);

        //graphics.drawArc();

        final int diameter = lineHeight - 1;
        final int radius = lineHeight / 2;

        graphics.drawArc(-radius + X_OFFSET_LEFT, 0, diameter, diameter, -90, 180);



        graphics.drawString(numberOfSelectedArtifactThreadsString, X_OFFSET_LEFT + radius + 2, lineHeight / 2 + (textHeight / 2) - 3);


        // Creation of the label

        BufferedImage subimage = bi.getSubimage(0, 0, bi.getWidth(), bi.getHeight());
        ImageIcon imageIcon = new ImageIcon(subimage);

        JLabel jLabel = new JLabel();
        jLabel.setIcon(imageIcon);

        jLabel.setSize(imageIcon.getIconWidth(), imageIcon.getIconHeight());
        jLabel.addMouseListener(new DefaultThreadVisualizationMouseListener(jLabel, artifact, primaryMetricIdentifier));

        return jLabel;
    }
}
