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

import de.unitrier.st.codesparks.core.data.AMetricIdentifier;

import javax.swing.*;
import java.awt.image.BufferedImage;

public abstract class AVisualizationLabelFactory extends AVisualizationSequence
{
    protected final AMetricIdentifier primaryMetricIdentifier;
    protected final int X_OFFSET_LEFT;

    public AMetricIdentifier getPrimaryMetricIdentifier()
    {
        return primaryMetricIdentifier;
    }

    protected AVisualizationLabelFactory(final AMetricIdentifier primaryMetricIdentifier)
    {
        this.primaryMetricIdentifier = primaryMetricIdentifier;
        this.X_OFFSET_LEFT = 0;
    }

    protected AVisualizationLabelFactory(final AMetricIdentifier primaryMetricIdentifier, final int sequence)
    {
        super(sequence);
        this.primaryMetricIdentifier = primaryMetricIdentifier;
        this.X_OFFSET_LEFT = 0;
    }

    protected AVisualizationLabelFactory(final AMetricIdentifier primaryMetricIdentifier, final int sequence, final int xOffsetLeft)
    {
        super(sequence);
        this.primaryMetricIdentifier = primaryMetricIdentifier;
        this.X_OFFSET_LEFT = xOffsetLeft;
    }

    protected JLabel emptyLabel()
    {
        final JLabel jLabel = new JLabel();
        jLabel.setSize(0, 0);
        return jLabel;
    }

    /**
     * Use if the width of teh glyph to create is already known.
     * @param width The width of the glyph image.
     * @param height The height of the glyph image. Typically, this corresponds to the line height source-code editor.
     * @return An instance of CodeSparksGraphics.
     */
    protected CodeSparksGraphics getGraphics(final int width, final int height)
    {
        return new CodeSparksGraphics(width, height);
    }

    /**
     * Use if the width of teh glyph to create is unknown.
     * @param height The height of the glyph image. Typically, this corresponds to the line height source-code editor.
     * @return An instance of CodeSparksGraphics.
     */
    protected CodeSparksGraphics getGraphics(final int height)
    {
        return new CodeSparksGraphics(5000, height);
    }

    protected JLabel makeLabel(final CodeSparksGraphics codeSparksGraphics, final int width)
    {
        final BufferedImage bufferedImage = codeSparksGraphics.getBufferedImage();
        final BufferedImage subImage = bufferedImage.getSubimage(0, 0, width, bufferedImage.getHeight());
        final ImageIcon imageIcon = new ImageIcon(subImage);

        final JLabel jLabel = new JLabel();
        jLabel.setIcon(imageIcon);
        jLabel.setSize(imageIcon.getIconWidth(), imageIcon.getIconHeight());
        return jLabel;
    }

    protected JLabel makeLabel(final CodeSparksGraphics codeSparksGraphics)
    {
        return makeLabel(codeSparksGraphics, codeSparksGraphics.getBufferedImage().getWidth());
    }
}
