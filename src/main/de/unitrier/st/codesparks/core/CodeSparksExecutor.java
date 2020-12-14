package de.unitrier.st.codesparks.core;

import com.intellij.execution.Executor;
import com.intellij.icons.AllIcons;
import com.intellij.openapi.util.IconLoader;
import com.intellij.util.ui.UIUtil;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;

/*
 * Copyright (c), Oliver Moseler, 2020
 */

/*
This is a template executor. It does nothing but demonstrating the methods which have to be overridden. It can be used to create an own executor for a
plugin project. An executor needs to be registered in the plugin.xml as a child node to the 'extensions' node:
<extensions defaultExtensionNs="com.intellij">
 <executor implementation="de.unitrier.st.codesparks.core.CodeSparksExecutor"/>
 ...
</extensions>
 */
public class CodeSparksExecutor extends Executor
{
    public static final String CODESPARKS_EXECUTOR_ID = "CodeSparks-Executor-ID";

    CodeSparksExecutor() {}

    @NotNull
    @Override
    public String getToolWindowId()
    {
        return this.getId();
    }

    @NotNull
    @Override
    public Icon getToolWindowIcon()
    {
        return AllIcons.Toolwindows.ToolWindowRun;
    }

    @NotNull
    public Icon getIcon()
    {
        if (UIUtil.isUnderDarcula())
        {
            return IconLoader.getIcon("/icons/codesparks.png", getClass());
        }
        return IconLoader.getIcon("/icons/codesparks.png", getClass());
    }

    @Override
    public Icon getDisabledIcon()
    {
        return IconLoader.getIcon("/icons/codesparks.png", getClass());
    }

    @Override
    public String getDescription()
    {
        return "CodeSparks-Description";
    }

    @NotNull
    public String getActionName()
    {
        return "CodeSparks-Action-Name";
    }

    @NotNull
    public String getId()
    {
        return CODESPARKS_EXECUTOR_ID;
    }

    @NotNull
    public String getStartActionText()
    {
        return "Run";
    }

    @NotNull
    @Override
    public String getStartActionText(@NotNull String configurationName)
    {
        return super.getStartActionText(configurationName).concat(" ")
                .concat(" with CodeSparks");
    }

    @Override
    public String getContextActionId()
    {
        return this.getId() + " context-action-does-not-exist";
    }

    @Override
    public String getHelpId()
    {
        return null;
    }
}

