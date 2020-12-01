package de.unitrier.st.codesparks.core.visualization;

import java.awt.*;

/*
 * Copyright (c), Oliver Moseler, 2020
 */
public final class BottomFlowLayout extends FlowLayout
{
    public BottomFlowLayout()
    {
        super(FlowLayout.LEFT, 0, 0);
    }

    @Override
    public void layoutContainer(Container container)
    {
        super.layoutContainer(container);
        int height = container.getHeight();
        for (Component component : container.getComponents())
        {
            Point location = component.getLocation();
            int componentHeight = component.getHeight();
            location.y = height - componentHeight;
            component.setLocation(location);
        }
    }
}
