package de.unitrier.st.codesparks.core.visualization;

import com.intellij.openapi.editor.ex.EditorEx;
import com.intellij.openapi.project.Project;
import com.intellij.ui.JBColor;
import com.intellij.util.ui.UIUtil;
import de.unitrier.st.codesparks.core.CoreUtil;

import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.Objects;

/*
 * Copyright (c), Oliver Moseler, 2020
 */
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
        Project currentlyOpenedProject = CoreUtil.getCurrentlyOpenedProject();
        if (currentlyOpenedProject != null)
        {
            EditorEx selectedFileEditor = CoreUtil.getSelectedFileEditor(currentlyOpenedProject);
            if (selectedFileEditor != null)
            {
                backgroundColor = selectedFileEditor.getBackgroundColor();
            }
        }
        return Objects.requireNonNullElseGet(backgroundColor, () -> UIUtil.isUnderDarcula() ? Color.decode("#2b2b2b") : Color.decode("#ffffff"));
    }

    public static Color getMetricColor(double metricValue)
    {
        double colorParameter = Math.pow(metricValue, .25);
        return ColorScale.getColor(colorParameter);
    }

    public static Color getBackgroundMetricColor(Color performanceColor, float alpha)
    {
        //noinspection UseJBColor
        final Color color = new Color(performanceColor.getRed() / 255f
                , performanceColor.getGreen() / 255f
                , performanceColor.getBlue() / 255f
                , alpha);
        return new JBColor(color, color);
    }

    public static Color getTextColor(Color performanceColor)
    {
        return getTextColor(performanceColor, 1f);
    }

    public static Color getTextColor(Color performanceColor, float alpha)
    {  // Color space YIQ. Only the Y value is of interest since it determines the brightness.
        double y = (299 * performanceColor.getRed() + 587 * performanceColor.getGreen() + 114 * performanceColor.getBlue()) / 1000D;
        return y * (1 + (1 - alpha)) > 145 ? JBColor.BLACK : JBColor.WHITE;
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