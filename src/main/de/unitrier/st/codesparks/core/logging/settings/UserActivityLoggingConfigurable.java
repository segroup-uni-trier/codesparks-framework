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

import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.options.Configurable;
import de.unitrier.st.codesparks.core.localization.LocalizationUtil;
import de.unitrier.st.codesparks.core.properties.PropertiesFile;
import de.unitrier.st.codesparks.core.properties.PropertiesUtil;
import de.unitrier.st.codesparks.core.properties.PropertyKey;
import de.unitrier.st.codesparks.core.settings.CheckBoxComponentWrapper;
import de.unitrier.st.codesparks.core.settings.CodeSparksSettings;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;

final class UserActivityLoggingConfigurable implements Configurable
{
    public UserActivityLoggingConfigurable()
    {
//        final CodeSparksSettings service = ServiceManager.getService(CodeSparksSettings.class);
        final CodeSparksSettings codeSparksSettings = ApplicationManager.getApplication().getService(CodeSparksSettings.class);
        codeSparksSettings.registerConfigurable(this);
    }

    @Override
    public String getDisplayName()
    {
        return LocalizationUtil.getLocalizedString("codesparks.settings.useractivitylogging.title");
    }

    @Nullable
    @Override
    public JComponent createComponent()
    {
        return UserActivityLoggingConfigurableComponentWrapper.getInstance().getRootPanel();
    }

    @Override
    public boolean isModified()
    {
        CheckBoxComponentWrapper instance = UserActivityLoggingConfigurableComponentWrapper.getInstance();
        final boolean checkBoxValue = instance.getCheckBoxValue();
        final boolean formerCheckBoxValue = instance.getFormerCheckBoxValue();
        return checkBoxValue != formerCheckBoxValue;
    }

    @Override
    public void apply()
    {
        CheckBoxComponentWrapper instance = UserActivityLoggingConfigurableComponentWrapper.getInstance();
        final boolean checkBoxValue = instance.getCheckBoxValue();
        PropertiesUtil.setPropertyValue(PropertiesFile.USER_INTERFACE_PROPERTIES, PropertyKey.USER_ACTIVITY_LOGGING_ENABLED, checkBoxValue);
        instance.setFormerCheckBoxValue(checkBoxValue);
    }
}
