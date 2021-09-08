/*
 * Copyright (c) 2021. Oliver Moseler
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
