package de.unitrier.st.codesparks.core;

import com.intellij.execution.Executor;
import com.intellij.icons.AllIcons;
import com.intellij.openapi.util.IconLoader;
import com.intellij.util.ui.UIUtil;
import de.unitrier.st.codesparks.core.localization.LocalizationUtil;
import org.jetbrains.annotations.NotNull;

import javax.swing.Icon;

public class ProfilingExecutor extends Executor
{
    public static final String EXECUTOR_ID = LocalizationUtil.getLocalizedString("profiling.executor.displayname");

    ProfilingExecutor() {}

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
            return IconLoader.getIcon("/icons/profiling_darcula_16x15.png");
        }
        return IconLoader.getIcon("/icons/profiling_16x15.png");
    }

    @Override
    public Icon getDisabledIcon()
    {
        return IconLoader.getIcon("/icons/profiling_16x15_disabled.png");
    }

    @Override
    public String getDescription()
    {
        return LocalizationUtil.getLocalizedString("profiling.executor.description");
    }

    @NotNull
    public String getActionName()
    {
        return LocalizationUtil.getLocalizedString("profiling.executor.actionname");
    }

    @NotNull
    public String getId()
    {
        return EXECUTOR_ID;
    }

    @NotNull
    public String getStartActionText()
    {
        return LocalizationUtil.getLocalizedString("profiling.executor.startactiontext");
    }

    @NotNull
    @Override
    public String getStartActionText(@NotNull String configurationName)
    {
        return super.getStartActionText(configurationName).concat(" ")
                .concat(LocalizationUtil.getLocalizedString("profiling.executor.startactiontext.concat"));
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
