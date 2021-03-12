/*
 * Copyright (c) 2021. Oliver Moseler
 */

package de.unitrier.st.codesparks.core.visualization.thread;

import com.intellij.ui.JBColor;
import de.unitrier.st.codesparks.core.data.ThreadArtifactCluster;

public class VisualThreadClusterPropertiesBuilder
{
    private final VisualThreadClusterProperties properties;

    VisualThreadClusterPropertiesBuilder(final ThreadArtifactCluster cluster)
    {
        properties = new VisualThreadClusterProperties(cluster);
    }

    VisualThreadClusterPropertiesBuilder setColor(final JBColor color)
    {
        properties.setColor(color);
        return this;
    }

    VisualThreadClusterPropertiesBuilder setPosition(final int position)
    {
        properties.setPosition(position);
        return this;
    }

    VisualThreadClusterProperties get()
    {
        return properties;
    }

}
