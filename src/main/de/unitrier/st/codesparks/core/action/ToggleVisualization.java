/*
 * Copyright (c) 2021. Oliver Moseler
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
import de.unitrier.st.codesparks.core.localization.LocalizationUtil;
import de.unitrier.st.codesparks.core.editorcoverlayer.EditorCoverLayerManager;

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
            final String description = LocalizationUtil.getLocalizedString("codesparks.ui.hide.visualization.action.description");
            presentation.setIcon(visualizationNotVisibleIcon);
            presentation.setText(description);
            presentation.setDescription(description);
        } else
        {
            //noinspection DialogTitleCapitalization
            final String description = LocalizationUtil.getLocalizedString("codesparks.ui.show.visualization.action.description");
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
    }

    @Override
    public boolean displayTextInToolbar()
    {
        return true;
    }

}
