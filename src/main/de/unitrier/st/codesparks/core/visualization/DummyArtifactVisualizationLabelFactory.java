package de.unitrier.st.codesparks.core.visualization;

import com.intellij.ui.paint.PaintUtil;
import com.intellij.util.ui.UIUtil;
import de.unitrier.st.codesparks.core.CoreUtil;
import de.unitrier.st.codesparks.core.data.AArtifact;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;

/*
 * Copyright (c), Oliver Moseler, 2020
 */
public class DummyArtifactVisualizationLabelFactory extends AArtifactVisualizationLabelFactory
{
    public DummyArtifactVisualizationLabelFactory() { super(null);}

    public DummyArtifactVisualizationLabelFactory(final int sequence)
    {
        super(null, sequence);
    }

    @Override
    public JLabel createArtifactLabel(@NotNull final AArtifact artifact)
    {
        final ImageIcon imageIcon = CoreUtil.getDefaultImageIcon();
        GraphicsConfiguration defaultConfiguration =
                GraphicsEnvironment.getLocalGraphicsEnvironment().getDefaultScreenDevice().getDefaultConfiguration();
        BufferedImage bi = UIUtil.createImage(defaultConfiguration, imageIcon.getIconWidth(), imageIcon.getIconHeight(),
                BufferedImage.TYPE_INT_ARGB, PaintUtil.RoundingMode.CEIL);
        Graphics2D graphics = (Graphics2D) bi.getGraphics();
        VisualizationUtil.drawTransparentBackground(graphics, bi);
        graphics.drawImage(imageIcon.getImage(), 0, 0, null);
        ImageIcon imageIcon1 = new ImageIcon(bi);
        return new JLabel(imageIcon1);
    }
}
