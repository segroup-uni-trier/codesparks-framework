package de.unitrier.st.insituprofiling.core.visualization.callee;

import com.intellij.ui.paint.PaintUtil;
import com.intellij.util.ui.UIUtil;
import de.unitrier.st.insituprofiling.core.data.ANeighborProfilingArtifact;
import de.unitrier.st.insituprofiling.core.data.AProfilingArtifact;

import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.List;

@SuppressWarnings("unused")
public class DummyArtifactCalleeVisualizationLabelFactory extends AArtifactCalleeVisualizationLabelFactory
{
    public DummyArtifactCalleeVisualizationLabelFactory() { }

    public DummyArtifactCalleeVisualizationLabelFactory(int sequence)
    {
        super(sequence);
    }

    @Override
    public JLabel createArtifactCalleeLabel(AProfilingArtifact artifact
            , List<ANeighborProfilingArtifact> threadFilteredNeighborArtifactsOfLine
            , double threadFilteredMetricValue
            , Color metricColor
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
