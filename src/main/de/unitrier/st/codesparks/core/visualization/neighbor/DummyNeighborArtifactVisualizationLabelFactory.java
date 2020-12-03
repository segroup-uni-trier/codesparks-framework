package de.unitrier.st.codesparks.core.visualization.neighbor;

import com.intellij.ui.paint.PaintUtil;
import com.intellij.util.ui.UIUtil;
import de.unitrier.st.codesparks.core.data.ANeighborArtifact;
import de.unitrier.st.codesparks.core.data.ACodeSparksArtifact;

import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.List;

/*
 * Copyright (c), Oliver Moseler, 2020
 */
@SuppressWarnings("unused")
public class DummyNeighborArtifactVisualizationLabelFactory extends ANeighborArtifactVisualizationLabelFactory
{
    public DummyNeighborArtifactVisualizationLabelFactory()
    {
        super(null);
    }

    public DummyNeighborArtifactVisualizationLabelFactory(int sequence)
    {
        super(null, sequence);
    }

    @Override
    public JLabel createArtifactCalleeLabel(
            ACodeSparksArtifact artifact
            , List<ANeighborArtifact> threadFilteredNeighborArtifactsOfLine
    )
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
