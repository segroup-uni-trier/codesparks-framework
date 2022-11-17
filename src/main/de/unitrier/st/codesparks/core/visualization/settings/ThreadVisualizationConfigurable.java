/*
 * Copyright (c) 2022. Oliver Moseler
 */
package de.unitrier.st.codesparks.core.visualization.settings;

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

public class ThreadVisualizationConfigurable implements Configurable
{
    public ThreadVisualizationConfigurable()
    {
        final CodeSparksSettings codeSparksSettings = ApplicationManager.getApplication().getService(CodeSparksSettings.class);
        codeSparksSettings.registerConfigurable(this);
    }

    @Override
    public String getDisplayName()
    {
        return LocalizationUtil.getLocalizedString("codesparks.settings.visualization.thread.title");
    }

    @Nullable
    @Override
    public JComponent createComponent()
    {
        final ThreadVisualizationConfigurableComponentWrapper instance = ThreadVisualizationConfigurableComponentWrapper.getInstance();
        return instance.getRootPanel();
    }

    @Override
    public boolean isModified()
    {
        final CheckBoxComponentWrapper instance = ThreadVisualizationConfigurableComponentWrapper.getInstance();
        final boolean checkBoxValue = instance.getCheckBoxValue();
        final boolean formerCheckBoxValue = instance.getFormerCheckBoxValue();
        return checkBoxValue != formerCheckBoxValue;
    }

    @Override
    public void apply()
    {
        final CheckBoxComponentWrapper instance = ThreadVisualizationConfigurableComponentWrapper.getInstance();
        final boolean checkBoxValue = instance.getCheckBoxValue();
        PropertiesUtil.setPropertyValue(PropertiesFile.USER_INTERFACE_PROPERTIES, PropertyKey.OVERVIEW_WINDOW_THREAD_FILTER_AREA_VISIBLE, checkBoxValue);
        instance.setFormerCheckBoxValue(checkBoxValue);
    }
}
