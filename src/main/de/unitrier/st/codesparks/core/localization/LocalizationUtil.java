package de.unitrier.st.codesparks.core.localization;

import java.util.Locale;
import java.util.ResourceBundle;

public final class LocalizationUtil
{
    private LocalizationUtil() {}

    public static String getLocalizedString(String key)
    {
        return ResourceBundle.getBundle("strings", Locale.getDefault()).getString(key);
    }
}
