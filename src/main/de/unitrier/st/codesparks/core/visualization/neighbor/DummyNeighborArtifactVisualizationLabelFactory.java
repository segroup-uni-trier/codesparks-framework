/*
 * Copyright (c) 2021. Oliver Moseler
 */
package de.unitrier.st.codesparks.core.visualization.neighbor;

import de.unitrier.st.codesparks.core.CoreUtil;
import de.unitrier.st.codesparks.core.data.AArtifact;
import de.unitrier.st.codesparks.core.data.ANeighborArtifact;
import de.unitrier.st.codesparks.core.visualization.CodeSparksGraphics;

import javax.swing.*;
import java.util.List;

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
    public JLabel createNeighborArtifactLabel(
            final AArtifact artifact,
            final List<ANeighborArtifact> threadFilteredNeighborArtifactsOfLine
    )
    {
        final ImageIcon imageIcon = CoreUtil.getDefaultImageIcon();
        assert imageIcon != null;
        final CodeSparksGraphics graphics = getGraphics(imageIcon.getIconWidth(), imageIcon.getIconHeight());
        graphics.drawImage(imageIcon.getImage(), 0, 0, null);
        return makeLabel(graphics);
    }
}
