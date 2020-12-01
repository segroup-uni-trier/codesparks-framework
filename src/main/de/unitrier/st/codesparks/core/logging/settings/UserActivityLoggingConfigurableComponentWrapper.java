package de.unitrier.st.codesparks.core.logging.settings;

import de.unitrier.st.codesparks.core.localization.LocalizationUtil;
import de.unitrier.st.codesparks.core.properties.PropertiesFile;
import de.unitrier.st.codesparks.core.properties.PropertiesUtil;
import de.unitrier.st.codesparks.core.properties.PropertyKey;
import de.unitrier.st.codesparks.core.settings.CheckBoxComponentWrapper;

/*
 * Copyright (c), Oliver Moseler, 2020
 */
public class UserActivityLoggingConfigurableComponentWrapper extends CheckBoxComponentWrapper
{
    private UserActivityLoggingConfigurableComponentWrapper()
    {
        super(
                LocalizationUtil.getLocalizedString("codesparks.settings.useractivitylogging.enable.border.title")
                , LocalizationUtil.getLocalizedString("codesparks.settings.useractivitylogging.enable")
                , PropertiesUtil.getBooleanPropertyValueOrDefault(
                        PropertiesFile.USER_INTERFACE_PROPERTIES, PropertyKey.USER_ACTIVITY_LOGGING_ENABLED, true)
        );
    }

    private volatile static UserActivityLoggingConfigurableComponentWrapper instance;

    public static UserActivityLoggingConfigurableComponentWrapper getInstance()
    {
        if (instance == null)
        {
            synchronized (UserActivityLoggingConfigurableComponentWrapper.class)
            {
                if (instance == null)
                {
                    instance = new UserActivityLoggingConfigurableComponentWrapper();
                }
            }
        }
        return instance;
    }
}
