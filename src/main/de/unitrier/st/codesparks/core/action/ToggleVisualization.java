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
package de.unitrier.st.codesparks.core.action;

import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.Presentation;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.fileEditor.FileEditor;
import com.intellij.openapi.fileEditor.FileEditorManager;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.util.Computable;
import com.intellij.openapi.util.IconLoader;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.openapi.wm.ToolWindow;
import com.intellij.openapi.wm.ToolWindowManager;
import com.intellij.util.ui.UIUtil;
import de.unitrier.st.codesparks.core.editorcoverlayer.EditorCoverLayerManager;
import de.unitrier.st.codesparks.core.localization.LocalizationUtil;

import javax.swing.*;
import java.util.Arrays;

public class ToggleVisualization extends AnAction
{
    private boolean visible;
    private final Icon visualizationVisibleIcon;
    private final Icon visualizationNotVisibleIcon;

    public ToggleVisualization()
    {
        visible = true;
        visualizationVisibleIcon = IconLoader.getIcon("/icons/codesparks_visible_20x13.png", getClass());
        visualizationNotVisibleIcon = IconLoader.getIcon("/icons/codesparks_not_visible_20x14.png", getClass());
    }

    @Override
    public void actionPerformed(AnActionEvent e)
    {
        final Project project = e.getProject();
        if (project == null)
        {
            return;
        }
        final EditorCoverLayerManager editorCoverLayerManager = EditorCoverLayerManager.getInstance(project);

        final Presentation presentation = e.getPresentation();
        if (visible)
        {
            //noinspection DialogTitleCapitalization
            final String description = LocalizationUtil.getLocalizedString("codesparks.ui.show.visualization.action.description");
            presentation.setIcon(visualizationNotVisibleIcon);
            presentation.setText(description);
            presentation.setDescription(description);
        } else
        {
            //noinspection DialogTitleCapitalization
            final String description = LocalizationUtil.getLocalizedString("codesparks.ui.hide.visualization.action.description");
            presentation.setIcon(visualizationVisibleIcon);
            presentation.setText(description);
            presentation.setDescription(description);
            final FileEditor[] editors = ApplicationManager.getApplication().runReadAction((Computable<FileEditor[]>) () ->
                    FileEditorManager.getInstance(project).getAllEditors());
            final VirtualFile[] virtualFiles = Arrays.stream(editors).map(FileEditor::getFile).toArray(VirtualFile[]::new);
            for (final VirtualFile virtualFile : virtualFiles)
            {
                editorCoverLayerManager.updateEditorCoverLayerFor(virtualFile);
            }
        }
        visible = !visible;
        editorCoverLayerManager.setEditorCoverLayersVisible(visible);

        // Hide/show the overview tool window
        final ToolWindowManager toolWindowManager = ToolWindowManager.getInstance(project);
        final String toolWindowIdName = LocalizationUtil.getLocalizedString("codesparks.ui.artifactoverview.displayname");
        UIUtil.invokeLaterIfNeeded(() -> {
            final ToolWindow toolWindow = toolWindowManager.getToolWindow(toolWindowIdName);
            if (toolWindow != null)
            {
                if (visible)
                    toolWindow.show();
                else
                    toolWindow.hide();
            }
        });
    }

    @Override
    public boolean displayTextInToolbar()
    {
        return true;
    }

}
