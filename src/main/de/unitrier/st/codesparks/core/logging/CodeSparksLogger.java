/*
 * Copyright (c) 2021. Oliver Moseler
 */
package de.unitrier.st.codesparks.core.logging;

import com.intellij.openapi.project.Project;
import com.intellij.openapi.wm.*;
import com.intellij.ui.content.Content;
import com.intellij.ui.content.ContentFactory;
import com.intellij.ui.content.ContentManager;
import de.unitrier.st.codesparks.core.CoreUtil;
import de.unitrier.st.codesparks.core.localization.LocalizationUtil;

import javax.swing.*;

public final class CodeSparksLogger
{
    private CodeSparksLogger() {}

    private static ToolWindow codeSparksLogToolWindow;

    private static final String CODESPARKS_LOG_TOOL_WINDOW_ID = "CodeSparksLogToolWindow";
    private static ITextView loggingTextView;

    public static void setup(Project project)
    {
        synchronized (CodeSparksLogger.class)
        {
            if (codeSparksLogToolWindow == null)
            {
                final ToolWindowManager toolWindowManager = ToolWindowManager.getInstance(project);
                final String toolWindowIdName = LocalizationUtil.getLocalizedString("codesparks.logger.view.displayname");
                ImageIcon defaultImageIcon = CoreUtil.getDefaultImageIcon();
                // To test
//                final RegisterToolWindowTaskBuilder toolWindowTaskBuilder = new RegisterToolWindowTaskBuilder(toolWindowIdName);
//                toolWindowTaskBuilder.anchor = ToolWindowAnchor.RIGHT;
//                toolWindowTaskBuilder.sideTool = true;
//                toolWindowTaskBuilder.canCloseContent = true;
//                toolWindowTaskBuilder.shouldBeAvailable = true;
//                toolWindowTaskBuilder.icon = defaultImageIcon;
//                toolWindowTaskBuilder.stripeTitle = () -> toolWindowIdName;
//                @SuppressWarnings("KotlinInternalInJava") final RegisterToolWindowTask registerToolWindowTask = toolWindowTaskBuilder.build();
//                toolWindowManager.registerToolWindow(registerToolWindowTask);
                // Working
                codeSparksLogToolWindow = toolWindowManager.registerToolWindow(new RegisterToolWindowTask(
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
                Content content = ContentFactory.getInstance().createContent(loggingTextView.getRootPanel(), "", true);
                ContentManager contentManager = codeSparksLogToolWindow.getContentManager();
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
