package de.unitrier.st.codesparks.core.service;

import com.intellij.openapi.components.ServiceManager;

import javax.swing.*;

/*
 * Copyright (c), Oliver Moseler, 2020
 */
public abstract class ACodeSparksInstanceService
{
    @SuppressWarnings("unused")
    // Is used from the plugin mechanics. It is a service and thus a singleton.
    // See documentation at https://jetbrains.org/intellij/sdk/docs/basics/plugin_structure/plugin_services.html
    // I decided to use an abstract class instead of an interface so that I am able to provide implementations for some methods, e.g.
    // 'getDefaultPluginImageIcon'. If an interface is used instead, the 'unused' warning wil not appear.
    static ACodeSparksInstanceService getInstance()
    {
        return ServiceManager.getService(ACodeSparksInstanceService.class);
    }

    public abstract String getPluginIdString();

    public ImageIcon getDefaultPluginImageIcon()
    {
        return new ImageIcon(getClass().getResource("/icons/codesparks.png"));
    }
}
