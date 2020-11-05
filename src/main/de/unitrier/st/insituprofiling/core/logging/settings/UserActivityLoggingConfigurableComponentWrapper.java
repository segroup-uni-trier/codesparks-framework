package de.unitrier.st.insituprofiling.core.logging.settings;

import de.unitrier.st.insituprofiling.core.localization.LocalizationUtil;
import de.unitrier.st.insituprofiling.core.properties.PropertiesFile;
import de.unitrier.st.insituprofiling.core.properties.PropertiesUtil;
import de.unitrier.st.insituprofiling.core.properties.PropertyKey;
import de.unitrier.st.insituprofiling.core.settings.CheckBoxComponentWrapper;

public class UserActivityLoggingConfigurableComponentWrapper extends CheckBoxComponentWrapper
{
    private UserActivityLoggingConfigurableComponentWrapper()
    {
        super(
                LocalizationUtil.getLocalizedString("settings.useractivitylogging.enable.border.title")
                , LocalizationUtil.getLocalizedString("settings.useractivitylogging.enable")
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
