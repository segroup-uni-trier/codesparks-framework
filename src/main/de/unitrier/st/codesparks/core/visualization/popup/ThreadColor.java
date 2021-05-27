/*
 * Copyright (c) 2021. Oliver Moseler
 */
package de.unitrier.st.codesparks.core.visualization.popup;

import com.intellij.ui.JBColor;

import java.awt.*;
import java.util.HashMap;
import java.util.Map;

public final class ThreadColor
{
    private ThreadColor() {}

    private static final int nrOfColors = 3;

    private static final JBColor[] enabledColors = {
            new JBColor(Color.decode("#5F4E95"), Color.decode("#5F4E95")),
            new JBColor(Color.decode("#B25283"), Color.decode("#B25283")),
            new JBColor(Color.decode("#3E877F"), Color.decode("#3E877F")),
    };

    private static final JBColor[] disabledColors = {
            new JBColor(Color.decode("#999999"), Color.decode("#999999")),
            new JBColor(Color.decode("#777777"), Color.decode("#777777")),
            new JBColor(Color.decode("#555555"), Color.decode("#555555"))
    };

    private static final Map<JBColor, JBColor> toDisabledMap = new HashMap<>(nrOfColors);

    private static final Map<JBColor, JBColor> toEnabledMap = new HashMap<>(nrOfColors);

    static
    {
        for (int i = 0; i < nrOfColors; i++)
        {
            toDisabledMap.put(enabledColors[i], disabledColors[i]);
            toEnabledMap.put(disabledColors[i], enabledColors[i]);
        }
    }

    public static JBColor getNextColor(final int i)
    {
        return getNextColor(i, false);
    }

    public static JBColor getNextColor(final int i, final boolean disabledColor)
    {
        if (disabledColor)
        {
            return disabledColors[i % disabledColors.length];
        }
        return enabledColors[i % enabledColors.length];
    }

    public static JBColor getEnabledColor(final JBColor disabledColor)
    {
        return toEnabledMap.get(disabledColor);
    }

    public static JBColor getDisabledColor(final JBColor enabledColor)
    {
        return toDisabledMap.get(enabledColor);
    }
}
