/*
 * Copyright (c) 2021. Oliver Moseler
 */
package de.unitrier.st.codesparks.core.visualization.thread;

import com.intellij.ui.JBColor;

import java.awt.*;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ThreadLocalRandom;

public final class ThreadColor
{
    private ThreadColor() {}

    private static final int nrOfColors = 6;

    private static final JBColor[] enabledColors = {
//            new JBColor(Color.decode("#5F4E95"), Color.decode("#5F4E95")),
//            new JBColor(Color.decode("#B25283"), Color.decode("#B25283")),
//            new JBColor(Color.decode("#3E877F"), Color.decode("#3E877F")),
            new JBColor(Color.decode("#1b9e77"), Color.decode("#1b9e77")),
            new JBColor(Color.decode("#d95f02"), Color.decode("#d95f02")),
            new JBColor(Color.decode("#7570b3"), Color.decode("#7570b3")),
            new JBColor(Color.decode("#e7298a"), Color.decode("#e7298a")),
            new JBColor(Color.decode("#66a61e"), Color.decode("#66a61e")),
            new JBColor(Color.decode("#e6ab02"), Color.decode("#e6ab02"))
    };

    private static final JBColor[] disabledColors = {
//            new JBColor(Color.decode("#999999"), Color.decode("#999999")),
//            new JBColor(Color.decode("#777777"), Color.decode("#777777")),
//            new JBColor(Color.decode("#555555"), Color.decode("#555555"))

//            new JBColor(Color.decode("#d9d9d9"), Color.decode("#d9d9d9")),
//            new JBColor(Color.decode("#bdbdbd"), Color.decode("#bdbdbd")),
//            new JBColor(Color.decode("#969696"), Color.decode("#969696")),
//            new JBColor(Color.decode("#636363"), Color.decode("#636363")),
//            new JBColor(Color.decode("#252525"), Color.decode("#252525")),
//            new JBColor(Color.decode("#555555"), Color.decode("#555555"))

            new JBColor(Color.decode("#bdbdbd"), Color.decode("#bdbdbd")),
            new JBColor(Color.decode("#bdbdbd"), Color.decode("#bdbdbd")),
            new JBColor(Color.decode("#bdbdbd"), Color.decode("#bdbdbd")),
            new JBColor(Color.decode("#bdbdbd"), Color.decode("#bdbdbd")),
            new JBColor(Color.decode("#bdbdbd"), Color.decode("#bdbdbd")),
            new JBColor(Color.decode("#bdbdbd"), Color.decode("#bdbdbd"))
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
        if (i < 0)
        {
            throw new IllegalArgumentException("the index value passed must be greater than or equal to zero!");
        }
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
        if (enabledColor == null)
        {
            final ThreadLocalRandom current = ThreadLocalRandom.current();
            return disabledColors[current.nextInt(6)];
        }
        return toDisabledMap.get(enabledColor);
    }
}
