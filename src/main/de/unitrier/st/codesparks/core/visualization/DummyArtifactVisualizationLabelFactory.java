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

import de.unitrier.st.codesparks.core.CoreUtil;
import de.unitrier.st.codesparks.core.data.AArtifact;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;

public class DummyArtifactVisualizationLabelFactory extends AArtifactVisualizationLabelFactory
{
    public DummyArtifactVisualizationLabelFactory() {super(null);}

    public DummyArtifactVisualizationLabelFactory(final int sequence)
    {
        super(null, sequence);
    }

    @Override
    public JLabel createArtifactLabel(@NotNull final AArtifact artifact)
    {
        final ImageIcon imageIcon = CoreUtil.getDefaultImageIcon();
        assert imageIcon != null;
        final CodeSparksGraphics graphics = getGraphics(imageIcon.getIconWidth(), imageIcon.getIconHeight());
        graphics.drawImage(imageIcon.getImage(), 0, 0, null);
        return makeLabel(graphics);
    }
}
