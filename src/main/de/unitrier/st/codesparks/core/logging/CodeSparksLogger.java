/*
 * Copyright (c) 2022. Oliver Moseler
 */
package de.unitrier.st.codesparks.core.logging;

import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.wm.ToolWindow;
import com.intellij.ui.content.Content;
import com.intellij.ui.content.ContentFactory;
import com.intellij.ui.content.ContentManager;
import de.unitrier.st.codesparks.core.CoreUtil;
import de.unitrier.st.codesparks.core.localization.LocalizationUtil;

import java.util.HashMap;
import java.util.Map;

import static java.lang.System.err;

public final class CodeSparksLogger
{
    private CodeSparksLogger() {}

    private static final Map<Project, ITextView> textViewMap = new HashMap<>(4);

    private static ITextView getTextView()
    {
        final Project project = CoreUtil.getCurrentlyOpenedProject();
        if (project == null)
        {
            err.printf("%s: Project is null.\n", CodeSparksLogger.class);
            return null;
        }
        ITextView loggingTextView = textViewMap.get(project);
        if (loggingTextView == null)
        {
            loggingTextView = new LoggingTextView();
            textViewMap.put(project, loggingTextView);
            final ToolWindow codeSparksToolWindow = CoreUtil.getCodeSparksToolWindow(project);
            final String displayName = LocalizationUtil.getLocalizedString("codesparks.logger.view.displayname");
            // Content createContent(@Nullable JComponent component, @Nullable @NlsContexts.TabTitle String displayName, boolean isLockable);
            final Content content = ContentFactory.getInstance().createContent(loggingTextView.getRootPanel(), displayName, true);
            final ContentManager contentManager = codeSparksToolWindow.getContentManager();
            ApplicationManager.getApplication().invokeLater(() -> contentManager.addContent(content));
        }
        return loggingTextView;
    }

    public static void addText(final String text)
    {
        synchronized (CodeSparksLogger.class)
        {
            final ITextView textView = getTextView();
            if (textView != null)
            {
                textView.addText(text);
            }
        }
    }

    public static void addText(final String format, final Object... args)
    {
        synchronized (CodeSparksLogger.class)
        {
            final ITextView textView = getTextView();
            if (textView != null)
            {
                textView.addText(String.format(format, args));
            }
        }
    }
}
