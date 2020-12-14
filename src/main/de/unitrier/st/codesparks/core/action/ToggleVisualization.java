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

/*
 * Copyright (c), Oliver Moseler, 2020
 */
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
        Project project = e.getProject();
        if (project == null)
        {
            return;
        }
        EditorCoverLayerManager editorCoverLayerManager = EditorCoverLayerManager.getInstance(project);
        final String description = LocalizationUtil.getLocalizedString("codesparks.ui.show.visualization.action.description");
        Presentation presentation = e.getPresentation();
        if (visible)
        {
            presentation.setIcon(visualizationNotVisibleIcon);
            presentation.setText(description);
            presentation.setDescription(description);
        } else
        {
            presentation.setIcon(visualizationVisibleIcon);
            presentation.setText(description);
            presentation.setDescription(description);
            FileEditor[] editors = ApplicationManager.getApplication().runReadAction((Computable<FileEditor[]>) () ->
                    FileEditorManager.getInstance(project).getAllEditors());
            VirtualFile[] virtualFiles = Arrays.stream(editors).map(FileEditor::getFile).toArray(VirtualFile[]::new);
            for (VirtualFile virtualFile : virtualFiles)
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
