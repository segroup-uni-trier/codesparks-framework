package de.unitrier.st.insituprofiling.core.logging;

import com.intellij.openapi.project.Project;
import com.intellij.openapi.util.IconLoader;
import com.intellij.openapi.wm.RegisterToolWindowTask;
import com.intellij.openapi.wm.ToolWindow;
import com.intellij.openapi.wm.ToolWindowAnchor;
import com.intellij.openapi.wm.ToolWindowManager;
import com.intellij.ui.content.Content;
import com.intellij.ui.content.ContentFactory;
import com.intellij.ui.content.ContentManager;
import de.unitrier.st.insituprofiling.core.localization.LocalizationUtil;

public final class ProfilingLogger
{
    private ProfilingLogger() {}

    private static ToolWindow profilingLogToolWindow;
    private static ITextView loggingTextView;

    public static void setup(Project project)
    {
        synchronized (ProfilingLogger.class)
        {
            if (profilingLogToolWindow == null)
            {
                final ToolWindowManager toolWindowManager = ToolWindowManager.getInstance(project);
                final String toolWindowIdName = LocalizationUtil.getLocalizedString("profiling.logger.view.displayname");
                profilingLogToolWindow = toolWindowManager.registerToolWindow(new RegisterToolWindowTask(
                        toolWindowIdName
                        , ToolWindowAnchor.RIGHT
                        , null
                        , true
                        , true
                        , true
                        , true
                        , null
                        , IconLoader.getIcon("/icons/profiling_13x12.png")
                        , () -> toolWindowIdName
                ));
                loggingTextView = new LoggingTextView();
                Content content = ContentFactory.SERVICE.getInstance().createContent(loggingTextView.getRootPanel(), "", true);
                ContentManager contentManager = profilingLogToolWindow.getContentManager();
                contentManager.addContent(content);
            }
        }
    }

    public static void addText(String text)
    {
        synchronized (ProfilingLogger.class)
        {
            if (loggingTextView == null)
            {
                return;
            }
            loggingTextView.addText(text);
        }
    }

    public static void addText(String format, Object... args)
    {
        synchronized (ProfilingLogger.class)
        {
            if (loggingTextView == null)
            {
                return;
            }
            loggingTextView.addText(String.format(format, args));
        }
    }
}
