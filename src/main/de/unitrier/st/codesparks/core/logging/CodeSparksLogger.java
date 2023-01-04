/*
 *  Copyright 2023 Oliver Moseler
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
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
