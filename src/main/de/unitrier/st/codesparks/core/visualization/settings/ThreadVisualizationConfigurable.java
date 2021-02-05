package de.unitrier.st.codesparks.core.visualization.settings;

import com.intellij.openapi.components.ServiceManager;
import com.intellij.openapi.options.Configurable;
import de.unitrier.st.codesparks.core.localization.LocalizationUtil;
import de.unitrier.st.codesparks.core.properties.PropertiesFile;
import de.unitrier.st.codesparks.core.properties.PropertiesUtil;
import de.unitrier.st.codesparks.core.properties.PropertyKey;
import de.unitrier.st.codesparks.core.settings.CheckBoxComponentWrapper;
import de.unitrier.st.codesparks.core.settings.CodeSparksSettings;
import org.jetbrains.annotations.Nls;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;

/*
 * Copyright (c), Oliver Moseler, 2020
 */
public class ThreadVisualizationConfigurable implements Configurable
{
    public ThreadVisualizationConfigurable()
    {
        final CodeSparksSettings service = ServiceManager.getService(CodeSparksSettings.class);
        service.registerConfigurable(this);
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
        CheckBoxComponentWrapper instance = ThreadVisualizationConfigurableComponentWrapper.getInstance();
        final boolean checkBoxValue = instance.getCheckBoxValue();
        final boolean formerCheckBoxValue = instance.getFormerCheckBoxValue();
        return checkBoxValue != formerCheckBoxValue;
    }

    @Override
    public void apply()
    {
        CheckBoxComponentWrapper instance = ThreadVisualizationConfigurableComponentWrapper.getInstance();
        final boolean checkBoxValue = instance.getCheckBoxValue();
        PropertiesUtil.setPropertyValue(PropertiesFile.USER_INTERFACE_PROPERTIES, PropertyKey.THREAD_VISUALIZATIONS_ENABLED, checkBoxValue);
        instance.setFormerCheckBoxValue(checkBoxValue);
    }
}
