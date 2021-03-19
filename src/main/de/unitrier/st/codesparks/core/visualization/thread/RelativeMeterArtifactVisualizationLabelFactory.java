/*
 * Copyright (c) 2021. Oliver Moseler
 */

package de.unitrier.st.codesparks.core.visualization.thread;

import com.intellij.ui.paint.PaintUtil;
import com.intellij.util.ui.UIUtil;
import de.unitrier.st.codesparks.core.data.AArtifact;
import de.unitrier.st.codesparks.core.visualization.AArtifactVisualizationLabelFactory;
import de.unitrier.st.codesparks.core.visualization.VisualizationUtil;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;

public class RelativeMeterArtifactVisualizationLabelFactory extends AArtifactVisualizationLabelFactory
{
    public RelativeMeterArtifactVisualizationLabelFactory() { super(null);}

    public RelativeMeterArtifactVisualizationLabelFactory(final int sequence)
    {
        super(null, sequence);
    }

    public RelativeMeterArtifactVisualizationLabelFactory(final int sequence, final int xOffsetLeft)
    {
        super(null, sequence, xOffsetLeft);
    }

    @Override
    public JLabel createArtifactLabel(@NotNull final AArtifact artifact)
    {
        final ImageIcon imageIcon = new ImageIcon(getClass().getResource("/icons/relative-meter.png"));
        GraphicsConfiguration defaultConfiguration =
                GraphicsEnvironment.getLocalGraphicsEnvironment().getDefaultScreenDevice().getDefaultConfiguration();
        BufferedImage bi = UIUtil.createImage(defaultConfiguration, imageIcon.getIconWidth(), imageIcon.getIconHeight(),
                BufferedImage.TYPE_INT_ARGB, PaintUtil.RoundingMode.CEIL);
        Graphics2D graphics = (Graphics2D) bi.getGraphics();
        VisualizationUtil.drawTransparentBackground(graphics, bi);
        final int X_OFFSET_LEFT = this.X_OFFSET_LEFT + 1;
        graphics.drawImage(imageIcon.getImage(), X_OFFSET_LEFT, 0, null);
        ImageIcon imageIcon1 = new ImageIcon(bi);
        return new JLabel(imageIcon1);
    }
}
