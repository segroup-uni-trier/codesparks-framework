/*
 * Copyright (c) 2021. Oliver Moseler
 */

package de.unitrier.st.codesparks.core.visualization.thread;

import de.unitrier.st.codesparks.core.data.AArtifact;
import de.unitrier.st.codesparks.core.data.AThreadArtifact;
import de.unitrier.st.codesparks.core.visualization.AArtifactVisualizationLabelFactory;
import de.unitrier.st.codesparks.core.visualization.CodeSparksGraphics;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;
import java.net.URL;
import java.util.Collection;

public class RelativeMeterArtifactVisualizationLabelFactory extends AArtifactVisualizationLabelFactory
{
    @SuppressWarnings("unused")
    public RelativeMeterArtifactVisualizationLabelFactory() { super(null);}

    public RelativeMeterArtifactVisualizationLabelFactory(final int sequence)
    {
        super(null, sequence);
    }

    @SuppressWarnings("unused")
    public RelativeMeterArtifactVisualizationLabelFactory(final int sequence, final int xOffsetLeft)
    {
        super(null, sequence, xOffsetLeft);
    }

    @Override
    public JLabel createArtifactLabel(@NotNull final AArtifact artifact)
    {
        final Collection<AThreadArtifact> threadArtifacts = artifact.getThreadArtifactsWithNumericMetricValue(primaryMetricIdentifier);
        if (threadArtifacts.isEmpty())
        {
            return emptyLabel();
        }
        final Class<? extends RelativeMeterArtifactVisualizationLabelFactory> theClass = getClass();
        final URL resource = theClass.getResource("/icons/relative-meter.png");
        if (resource == null)
        {
            return emptyLabel();
        }
        final ImageIcon imageIconFromFile = new ImageIcon(resource);
        final int X_OFFSET_LEFT = this.X_OFFSET_LEFT + 1;
//        final GraphicsConfiguration defaultConfiguration =
//                GraphicsEnvironment.getLocalGraphicsEnvironment().getDefaultScreenDevice().getDefaultConfiguration();
//        final BufferedImage bi = UIUtil.createImage(
//                defaultConfiguration
//                , X_OFFSET_LEFT + imageIconFromFile.getIconWidth()
//                , imageIconFromFile.getIconHeight()
//                , BufferedImage.TYPE_INT_ARGB
//                , PaintUtil.RoundingMode.CEIL
//        );
//        final Graphics2D graphics = (Graphics2D) bi.getGraphics();
//        VisualizationUtil.drawTransparentBackground(graphics, bi);

        final CodeSparksGraphics graphics = getGraphics(X_OFFSET_LEFT + imageIconFromFile.getIconWidth(), imageIconFromFile.getIconHeight());
        graphics.drawImage(imageIconFromFile.getImage(), X_OFFSET_LEFT, 0, null);

//        final ImageIcon imageIcon = new ImageIcon(bi);
//        final JLabel jLabel = new JLabel(imageIcon);
//        jLabel.setSize(imageIcon.getIconWidth(), imageIcon.getIconHeight());
        return makeLabel(graphics);
    }
}
