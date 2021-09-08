/*
 * Copyright (c) 2021. Oliver Moseler
 */
package de.unitrier.st.codesparks.core.visualization.settings;

import de.unitrier.st.codesparks.core.localization.LocalizationUtil;
import de.unitrier.st.codesparks.core.properties.PropertiesFile;
import de.unitrier.st.codesparks.core.properties.PropertiesUtil;
import de.unitrier.st.codesparks.core.properties.PropertyKey;
import de.unitrier.st.codesparks.core.settings.CheckBoxComponentWrapper;

public class ThreadVisualizationConfigurableComponentWrapper extends CheckBoxComponentWrapper
{
    private ThreadVisualizationConfigurableComponentWrapper()
    {
        super(
                LocalizationUtil.getLocalizedString("codesparks.settings.visualization.thread.enable.border.title")
                , LocalizationUtil.getLocalizedString("codesparks.settings.visualization.thread.enable")
                , PropertiesUtil.getBooleanPropertyValueOrDefault(
                        PropertiesFile.USER_INTERFACE_PROPERTIES, PropertyKey.THREAD_VISUALIZATIONS_ENABLED, true)
        );
    }

    private volatile static ThreadVisualizationConfigurableComponentWrapper instance;

    public static ThreadVisualizationConfigurableComponentWrapper getInstance()
    {
        if (instance == null)
        {
            synchronized (ThreadVisualizationConfigurableComponentWrapper.class)
            {
                if (instance == null)
                {
                    instance = new ThreadVisualizationConfigurableComponentWrapper();
                }
            }
        }
        return instance;
    }
}
