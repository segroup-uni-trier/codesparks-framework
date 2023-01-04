/*
 *  Copyright 2023 Oliver Moseler
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */
package de.unitrier.st.codesparks.core.logging.settings;

import de.unitrier.st.codesparks.core.localization.LocalizationUtil;
import de.unitrier.st.codesparks.core.properties.PropertiesFile;
import de.unitrier.st.codesparks.core.properties.PropertiesUtil;
import de.unitrier.st.codesparks.core.properties.PropertyKey;
import de.unitrier.st.codesparks.core.settings.CheckBoxComponentWrapper;

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
