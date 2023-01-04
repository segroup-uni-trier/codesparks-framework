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

import com.intellij.openapi.editor.ex.EditorEx;
import com.intellij.openapi.project.Project;
import com.intellij.ui.Gray;
import com.intellij.ui.JBColor;
import com.intellij.util.ui.UIUtil;
import de.unitrier.st.codesparks.core.CoreUtil;

import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.Objects;

public final class VisualizationUtil
{
    private VisualizationUtil() {}

    public static void setCursorRecursively(JComponent component, Cursor cursor)
    {
        if (component == null)
        {
            return;
        }
        component.setCursor(cursor);
        for (Component subComp : component.getComponents())
        {
            setCursorRecursively((JComponent) subComp, cursor);
        }
    }

    public static Color getSelectedFileEditorBackgroundColor()
    {
        Color backgroundColor = null;
        final Project currentlyOpenedProject = CoreUtil.getCurrentlyOpenedProject();
        if (currentlyOpenedProject != null)
        {
            final EditorEx selectedFileEditor = CoreUtil.getSelectedFileEditor(currentlyOpenedProject);
            if (selectedFileEditor != null)
            {
                backgroundColor = selectedFileEditor.getBackgroundColor();
            }
        }
        return Objects.requireNonNullElseGet(backgroundColor, () -> UIUtil.isUnderDarcula() ? Color.decode("#2b2b2b") : Color.decode("#ffffff"));
    }

    public static Color getMetricColor(final double metricValue)
    {
        double colorParameter = Math.pow(metricValue, .25);
        return ColorScale.getColor(colorParameter);
    }

    public static Color getBackgroundMetricColor(final Color metricColor, final float alpha)
    {
        //noinspection UseJBColor
        final Color color = new Color(metricColor.getRed() / 255f
                , metricColor.getGreen() / 255f
                , metricColor.getBlue() / 255f
                , alpha);
        return new JBColor(color, color);
    }

    public static Color getTextColor(final Color metricColor)
    {
        return getTextColor(metricColor, 1f);
    }

    public static Color getTextColor(final Color metricColor, final float alpha)
    {  // Color space YIQ. Only the Y value is of interest since it determines the brightness.
        final double y = (299 * metricColor.getRed() + 587 * metricColor.getGreen() + 114 * metricColor.getBlue()) / 1000D;
        //System.out.println("y value = " + y);
        if (UIUtil.isUnderDarcula())
        {
            if (y < 100)// Rather dark color
            {
                return Gray._222; // Is close to white
            } else
            {
                if (y < 200)
                {
                    return Gray._5; // Is close to black
                } else
                {
                    return Gray._40;
                }
            }
        } else
        {
            if (y < 140)
            {
                return Gray._242; // Is close to white
            } else
            {
                if (y < 200)
                {
                    return Gray._40; // Is close to black
                } else
                {
                    return Gray._5; // Is close to black
                }
            }
        }
    }

    public static void drawTransparentBackground(final Graphics2D graphics, final BufferedImage bi)
    {
        final Composite composite = graphics.getComposite();
        graphics.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0f));
        graphics.fillRect(0, 0, bi.getWidth(), bi.getHeight());
        graphics.setComposite(composite);
    }

    public static void drawTransparentBackground(final Graphics2D graphics, final int width, final int height)
    {
        final Composite composite = graphics.getComposite();
        graphics.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0f));
        graphics.fillRect(0, 0, width, height);
        graphics.setComposite(composite);
    }

    public static void drawTransparentBackground(final Graphics2D graphics, final int width, final int height, final int alphaComposite)
    {
        final Composite composite = graphics.getComposite();
        graphics.setComposite(AlphaComposite.getInstance(alphaComposite, 0f));
        graphics.fillRect(0, 0, width, height);
        graphics.setComposite(composite);
    }

    public static void clearAndDrawTransparentBackground(final Graphics2D graphics, final int width, final int height)
    {
        final Composite composite = graphics.getComposite();
        graphics.setComposite(AlphaComposite.getInstance(AlphaComposite.CLEAR, 0f)); // Will fix the problem that on repainting, the old graphics paintings
        // remain and the paintings overlap.
        graphics.fillRect(0, 0, width, height);
        graphics.setComposite(composite);
    }

    public static int getLineHeightFloor(int lineHeight, int threadsPerColumn)
    {
        lineHeight -= 1;
        while ((lineHeight - 6) % threadsPerColumn != 0)
        {
            lineHeight -= 1;
        }
        return lineHeight;
    }

    public static int getLineHeightCeil(int lineHeight, int threadsPerColumn)
    {
        while ((lineHeight - 6) % threadsPerColumn != 0)
        {
            lineHeight += 1;
        }
        return lineHeight;
    }
}