package de.unitrier.st.codesparks.core.logging;

import com.intellij.openapi.project.Project;
import com.intellij.openapi.wm.RegisterToolWindowTask;
import com.intellij.openapi.wm.ToolWindow;
import com.intellij.openapi.wm.ToolWindowAnchor;
import com.intellij.openapi.wm.ToolWindowManager;
import com.intellij.ui.content.Content;
import com.intellij.ui.content.ContentFactory;
import com.intellij.ui.content.ContentManager;
import de.unitrier.st.codesparks.core.CoreUtil;
import de.unitrier.st.codesparks.core.localization.LocalizationUtil;

import javax.swing.*;

public final class CodeSparksLogger
{
    private CodeSparksLogger() {}

    private static ToolWindow profilingLogToolWindow;
    private static ITextView loggingTextView;

    public static void setup(Project project)
    {
        synchronized (CodeSparksLogger.class)
        {
            if (profilingLogToolWindow == null)
            {
                final ToolWindowManager toolWindowManager = ToolWindowManager.getInstance(project);
                final String toolWindowIdName = LocalizationUtil.getLocalizedString("codesparks.logger.view.displayname");
                ImageIcon defaultImageIcon = CoreUtil.getDefaultImageIcon();
                profilingLogToolWindow = toolWindowManager.registerToolWindow(new RegisterToolWindowTask(
                        toolWindowIdName
                        , ToolWindowAnchor.RIGHT
                        , null
                        , true
                        , true
                        , true
                        , true
                        , null
                        , defaultImageIcon//IconLoader.getIcon("/icons/codesparks.png") // TODO: CodeSparks Logo
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
        synchronized (CodeSparksLogger.class)
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
        synchronized (CodeSparksLogger.class)
        {
            if (loggingTextView == null)
            {
                return;
            }
            loggingTextView.addText(String.format(format, args));
        }
    }
}
