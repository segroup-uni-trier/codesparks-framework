/*
 * Copyright (C) 2020, Oliver Moseler
 */
package de.unitrier.st.insituprofiling.core.visualization;

import com.intellij.ui.paint.PaintUtil;
import com.intellij.util.ui.UIUtil;
import de.unitrier.st.insituprofiling.core.data.AProfilingArtifact;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;

@SuppressWarnings("unused")
public class DummyArtifactVisualizationLabelFactory extends AArtifactVisualizationLabelFactory
{
    public DummyArtifactVisualizationLabelFactory() { }

    public DummyArtifactVisualizationLabelFactory(int sequence)
    {
        super(sequence);
    }

    @Override
    public JLabel createArtifactLabel(@NotNull AProfilingArtifact artifact)
    {
        ImageIcon imageIcon = new ImageIcon(getClass().getResource("/icons/pluginIcon.png"));
        GraphicsConfiguration defaultConfiguration =
                GraphicsEnvironment.getLocalGraphicsEnvironment().getDefaultScreenDevice().getDefaultConfiguration();
        BufferedImage image = UIUtil.createImage(defaultConfiguration, imageIcon.getIconWidth(), imageIcon.getIconHeight(),
                BufferedImage.TYPE_INT_RGB, PaintUtil.RoundingMode.CEIL);
        Graphics graphics = image.getGraphics();
        graphics.fillRect(0, 0, imageIcon.getIconWidth(), imageIcon.getIconHeight());
        graphics.drawImage(imageIcon.getImage(), 0, 0, null);
        ImageIcon imageIcon1 = new ImageIcon(image);
        return new JLabel(imageIcon1);
    }
}
