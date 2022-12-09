/*
 * Copyright (c) 2022. Oliver Moseler
 */
package de.unitrier.st.codesparks.core.localization;

import de.unitrier.st.codesparks.core.logging.CodeSparksLogger;

import java.util.Locale;
import java.util.MissingResourceException;
import java.util.ResourceBundle;

public final class LocalizationUtil
{
    private LocalizationUtil() {}

    public static String getLocalizedString(String key)
    {
        final ResourceBundle resourceBundle = ResourceBundle.getBundle("codesparks-strings", Locale.getDefault());
        String string;
        try
        {
            string = resourceBundle.getString(key);
        } catch (MissingResourceException e)
        {
            CodeSparksLogger.addText(e.getMessage());
            return "NA";
        }
        return string;
    }
}
