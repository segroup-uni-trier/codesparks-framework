package de.unitrier.st.codesparks.core.localization;

import java.util.Locale;
import java.util.ResourceBundle;

/*
 * Copyright (c), Oliver Moseler, 2020
 */
public final class LocalizationUtil
{
    private LocalizationUtil() {}

    public static String getLocalizedString(String key)
    {
        return ResourceBundle.getBundle("codesparks-strings", Locale.getDefault()).getString(key);
    }
}
