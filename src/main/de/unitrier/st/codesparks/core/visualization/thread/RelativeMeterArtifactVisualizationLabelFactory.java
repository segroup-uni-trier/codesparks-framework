/*
 * Copyright (c) 2021. Oliver Moseler
 */

package de.unitrier.st.codesparks.core.visualization.thread;

import de.unitrier.st.codesparks.core.data.AArtifact;
import de.unitrier.st.codesparks.core.visualization.AArtifactVisualizationLabelFactory;
import de.unitrier.st.codesparks.core.visualization.CodeSparksGraphics;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;
import java.net.URL;

public class RelativeMeterArtifactVisualizationLabelFactory extends AArtifactVisualizationLabelFactory
{
    @SuppressWarnings("unused")
    public RelativeMeterArtifactVisualizationLabelFactory() {super(null);}

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
        final Class<? extends RelativeMeterArtifactVisualizationLabelFactory> theClass = getClass();
        final URL resource = theClass.getResource("/icons/relative-meter.png");
        if (resource == null)
        {
            return emptyLabel();
        }
        final ImageIcon imageIconFromFile = new ImageIcon(resource);
        final int X_OFFSET_LEFT = this.X_OFFSET_LEFT + 1;

        final CodeSparksGraphics graphics = getGraphics(X_OFFSET_LEFT + imageIconFromFile.getIconWidth(), imageIconFromFile.getIconHeight());
        graphics.drawImage(imageIconFromFile.getImage(), X_OFFSET_LEFT, 0, null);

        return makeLabel(graphics);
    }
}
