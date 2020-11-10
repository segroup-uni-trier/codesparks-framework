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
        visualizationVisibleIcon = IconLoader.getIcon("/icons/visible_20x12.png");
        visualizationNotVisibleIcon = IconLoader.getIcon("/icons/not_visible_20x15.png");
    }

    @Override
    public void actionPerformed(AnActionEvent e)
    {
        Project project = e.getProject();
        if (project == null) return;
        EditorCoverLayerManager editorCoverLayerManager = EditorCoverLayerManager.getInstance(project);
        Presentation presentation = e.getPresentation();
        if (visible)
        {
            presentation.setIcon(visualizationNotVisibleIcon);
            String showDescription = LocalizationUtil.getLocalizedString("codesparks.ui.show.visualization.action.description");
            presentation.setText(showDescription);
            presentation.setDescription(showDescription);
        } else
        {
            presentation.setIcon(visualizationVisibleIcon);
            String hideDescription = LocalizationUtil.getLocalizedString("codesparks.ui.hide.visualization.action.description");
            presentation.setText(hideDescription);
            presentation.setDescription(hideDescription);
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
