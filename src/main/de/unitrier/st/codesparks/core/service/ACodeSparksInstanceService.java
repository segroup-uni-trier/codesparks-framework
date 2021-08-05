/*
 * Copyright (c), Oliver Moseler, 2021
 */
package de.unitrier.st.codesparks.core.service;

import com.intellij.ide.plugins.IdeaPluginDescriptor;
import com.intellij.ide.plugins.PluginManagerCore;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.extensions.PluginId;

import javax.swing.*;
import java.net.URL;
import java.nio.file.Path;

public abstract class ACodeSparksInstanceService
{
    // Is used from the plugin mechanics. It is a service and thus a singleton.
    // See documentation at https://jetbrains.org/intellij/sdk/docs/basics/plugin_structure/plugin_services.html
    // I decided to use an abstract class instead of an interface so that I am able to provide implementations for some methods, e.g.
    // 'getDefaultPluginImageIcon'. If an interface is used instead, the 'unused' warning wil not appear.
    public static ACodeSparksInstanceService getInstance()
    {
        //return ServiceManager.getService(ACodeSparksInstanceService.class);
        return ApplicationManager.getApplication().getService(ACodeSparksInstanceService.class);
    }

    public abstract String getPluginIdString();

    public PluginId getPlugin()
    {
        final String pluginIdString = getPluginIdString();
        //noinspection UnnecessaryLocalVariable
        final PluginId pluginId = PluginId.getId(pluginIdString);
        return pluginId;
    }

    public IdeaPluginDescriptor getPluginDescriptor()
    {
        final PluginId pluginId = getPlugin();
        //noinspection UnnecessaryLocalVariable
        final IdeaPluginDescriptor pluginDescriptor = PluginManagerCore.getPlugin(pluginId);
        return pluginDescriptor;
    }

    public Path getPluginPath()
    {
        final IdeaPluginDescriptor pluginDescriptor = getPluginDescriptor();
        //noinspection UnnecessaryLocalVariable
        final Path pluginPath = pluginDescriptor.getPluginPath();
        return pluginPath;
    }

    public String getPluginPathString()
    {
        final Path pluginPath = getPluginPath();
        //noinspection UnnecessaryLocalVariable
        final String pathString = pluginPath.toString();
        return pathString;
    }

    public ImageIcon getDefaultPluginImageIcon()
    {
        final Class<? extends ACodeSparksInstanceService> aClass = getClass();
        if (aClass == null)
        {
            return null;
        }
        final URL resource = aClass.getResource("/icons/codesparks.png");
        if (resource == null)
        {
            return null;
        }
        return new ImageIcon(resource);
    }
}
