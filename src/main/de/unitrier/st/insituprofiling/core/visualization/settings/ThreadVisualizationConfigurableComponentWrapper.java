package de.unitrier.st.insituprofiling.core.visualization.settings;

import de.unitrier.st.insituprofiling.core.localization.LocalizationUtil;
import de.unitrier.st.insituprofiling.core.properties.PropertiesFile;
import de.unitrier.st.insituprofiling.core.properties.PropertiesUtil;
import de.unitrier.st.insituprofiling.core.properties.PropertyKey;
import de.unitrier.st.insituprofiling.core.settings.CheckBoxComponentWrapper;

public class ThreadVisualizationConfigurableComponentWrapper extends CheckBoxComponentWrapper
{
    private ThreadVisualizationConfigurableComponentWrapper()
    {
        super(
                LocalizationUtil.getLocalizedString("settings.visualization.thread.enable.border.title")
                , LocalizationUtil.getLocalizedString("settings.visualization.thread.enable")
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
