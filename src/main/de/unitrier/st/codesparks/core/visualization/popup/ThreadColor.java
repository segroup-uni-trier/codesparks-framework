package de.unitrier.st.codesparks.core.visualization.popup;

import com.intellij.ui.JBColor;

import java.awt.*;

/*
 * Copyright (c), Oliver Moseler, 2021
 */
public final class ThreadColor
{
    private ThreadColor() {}

    private static final JBColor[] disabledColorList = {
            new JBColor(Color.decode("#999999"), Color.decode("#999999")),
            new JBColor(Color.decode("#777777"), Color.decode("#777777")),
            new JBColor(Color.decode("#555555"), Color.decode("#555555"))
    };

    private static final JBColor[] colorList =
            {
//                    new JBColor(new Color(4, 49, 156), new Color(4, 49, 156)),
//                    new JBColor(new Color(252, 102, 5), new Color(252, 102, 5)),
//                    new JBColor(new Color(101, 203, 11), new Color(101, 203, 11)),
//
//                    new JBColor(new Color(0, 163, 193), new Color(0, 163, 193)),
//                    new JBColor(new Color(197, 0, 124), new Color(197, 0, 124)),
//                    new JBColor(new Color(255, 198, 0), new Color(255, 198, 0)),
//
//                    new JBColor(new Color(197, 0, 124), new Color(197, 0, 124)),
//                    new JBColor(new Color(255, 102, 0), new Color(255, 102, 0)),
//                    new JBColor(new Color(15, 173, 0), new Color(15, 173, 0)),

//                    new JBColor(new Color(99, 0, 164), new Color(99, 0, 164)),
//                    new JBColor(new Color(197, 0, 124), new Color(197, 0, 124)),
//                    new JBColor(new Color(0, 97, 181), new Color(0, 97, 191)),

                    new JBColor(Color.decode("#5F4E95"), Color.decode("#5F4E95")),
                    new JBColor(Color.decode("#B25283"), Color.decode("#B25283")),
                    new JBColor(Color.decode("#3E877F"), Color.decode("#3E877F")),
            };

    public static JBColor getNextColor(final int i)
    {
        return getNextColor(i, false);
    }

    public static JBColor getNextColor(final int i, final boolean disabledColor)
    {
        if (disabledColor)
        {
            return disabledColorList[i % disabledColorList.length];
        }
        return colorList[i % colorList.length];
    }
}
