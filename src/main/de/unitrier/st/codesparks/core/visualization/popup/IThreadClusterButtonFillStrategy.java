/*
 * Copyright (c) 2021. Oliver Moseler
 */

package de.unitrier.st.codesparks.core.visualization.popup;

import de.unitrier.st.codesparks.core.data.ThreadArtifactClustering;

import java.awt.*;

public interface IThreadClusterButtonFillStrategy
{
    void fillThreadClusterButton(final ThreadClusterButton threadClusterButton, final ThreadArtifactClustering clustering, final Graphics g);
}
