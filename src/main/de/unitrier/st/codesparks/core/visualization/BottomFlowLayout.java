/*
 *  Copyright 2023 Oliver Moseler
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */
package de.unitrier.st.codesparks.core.visualization;

import java.awt.*;

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
