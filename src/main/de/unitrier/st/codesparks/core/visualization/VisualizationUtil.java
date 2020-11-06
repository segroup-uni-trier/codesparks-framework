package de.unitrier.st.codesparks.core.visualization;

import com.intellij.ui.JBColor;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;
import java.awt.*;

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

    public static Color getPerformanceColor(double runtime)
    {
        double colorParameter = Math.pow(runtime, .25);
        return ColorScale.getColor(colorParameter);
    }

    public static Color getBackgroundPerformanceColor(Color performanceColor, float alpha)
    {
        return new JBColor(new Color(performanceColor.getRed() / 255f, performanceColor.getGreen() / 255f,
                performanceColor.getBlue()
                        / 255f, alpha), new Color(0));
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

    public static void fillRectangle(@NotNull Graphics graphics, @NotNull Rectangle rectangle)
    {
        graphics.fillRect(rectangle.x, rectangle.y, rectangle.width, rectangle.height);
    }

    public static void drawRectangle(@NotNull Graphics graphics, @NotNull Rectangle rectangle)
    {
        graphics.drawRect(rectangle.x, rectangle.y, rectangle.width, rectangle.height);
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