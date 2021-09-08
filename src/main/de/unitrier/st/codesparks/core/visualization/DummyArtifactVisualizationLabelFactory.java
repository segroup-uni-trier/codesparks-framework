/*
 * Copyright (c) 2021. Oliver Moseler
 */
package de.unitrier.st.codesparks.core.visualization;

import de.unitrier.st.codesparks.core.CoreUtil;
import de.unitrier.st.codesparks.core.data.AArtifact;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;

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
        final ImageIcon imageIconFile = CoreUtil.getDefaultImageIcon();
        final CodeSparksGraphics graphics = getGraphics(imageIconFile.getIconWidth(), imageIconFile.getIconHeight());
        graphics.drawImage(imageIconFile.getImage(), 0, 0, null);
        return makeLabel(graphics);
    }
}
