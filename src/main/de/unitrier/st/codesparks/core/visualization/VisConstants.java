package de.unitrier.st.codesparks.core.visualization;

import com.intellij.ui.Gray;
import com.intellij.ui.JBColor;

/*
 * Copyright (c), Oliver Moseler, 2020
 */
public abstract class VisConstants
{
    private VisConstants() {}

    public static final double STRIPES_BRIGHTNESS = .94;
    public static final JBColor BORDER_COLOR = new JBColor(Gray._100, JBColor.decode("#666d75"));//DarculaColors.RED);
    public static final JBColor STANDARD_FONT_COLOR = new JBColor(Gray._60, JBColor.decode("#666d75"));//DarculaColors.RED);
    public static int LINE_HEIGHT = 18; // the default line height of Intellij source-code editors
    public static final int RECTANGLE_WIDTH = 70;
    public static final int X_OFFSET = 6;
    public static final int X_OFFSET_THREAD = 12;
    public static final int INVOCATION_WIDTH = 60;
    public static final int CALLEE_TRIANGLES_WIDTH = 2;
    public static final int CALLEE_X_OFFSET = 2;
    public static final int CALLEE_Y_OFFSET = 4;

    public static void setLineHeight(final int lineHeight)
    {
        synchronized (VisConstants.class)
        {
            LINE_HEIGHT = lineHeight;
        }
    }

    public static synchronized int getLineHeight()
    {
        synchronized (VisConstants.class)
        {
            return LINE_HEIGHT;
        }
    }
}
