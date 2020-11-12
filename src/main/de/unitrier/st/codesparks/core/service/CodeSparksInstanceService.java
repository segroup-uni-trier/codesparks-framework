package de.unitrier.st.codesparks.core.service;

import com.intellij.openapi.components.ServiceManager;

import javax.swing.*;

public abstract class CodeSparksInstanceService
{
    static CodeSparksInstanceService getInstance()
    {
        return ServiceManager.getService(CodeSparksInstanceService.class);
    }

    public abstract String getPluginIdString();

    public ImageIcon getDefaultImageIcon()
    {
        return new ImageIcon(getClass().getResource("/icons/codesparks.png"));
    }
}
