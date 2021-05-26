/*
 * Copyright (c) 2021. Oliver Moseler
 */

package de.unitrier.st.codesparks.core.visualization.thread;

import com.intellij.ui.JBColor;
import de.unitrier.st.codesparks.core.data.ThreadArtifactCluster;

public class VisualThreadClusterPropertiesBuilder
{
    private final VisualThreadClusterProperties properties;

    public VisualThreadClusterPropertiesBuilder(final ThreadArtifactCluster cluster)
    {
        properties = new VisualThreadClusterProperties(cluster);
    }

    public VisualThreadClusterPropertiesBuilder setColor(final JBColor color)
    {
        properties.getOrSetColor(color);//setColor(color);
        return this;
    }

    public VisualThreadClusterPropertiesBuilder setPosition(final int position)
    {
        properties.getOrSetPosition(position);//setPosition(position);
        return this;
    }

    public VisualThreadClusterProperties get()
    {
        return properties;
    }

}
