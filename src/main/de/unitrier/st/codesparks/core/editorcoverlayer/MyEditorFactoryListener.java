package de.unitrier.st.codesparks.core.editorcoverlayer;

import com.intellij.openapi.editor.Document;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.editor.event.EditorFactoryEvent;
import com.intellij.openapi.editor.event.EditorFactoryListener;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.psi.PsiDocumentManager;
import com.intellij.psi.PsiFile;
import org.jetbrains.annotations.NotNull;

/*
 * Copyright (c), Oliver Moseler, 2020
 */
public class MyEditorFactoryListener implements EditorFactoryListener
{
    @Override
    public void editorCreated(@NotNull EditorFactoryEvent event)
    {
        Editor editor = event.getEditor();
        Project project = editor.getProject();
        if (project == null)
        {
            return;
        }
        Document document = editor.getDocument();
        PsiFile file = PsiDocumentManager.getInstance(project).getPsiFile(document);
        if (file == null)
        {
            return;
        }
        EditorCoverLayerManager editorCoverLayerManager = EditorCoverLayerManager.getInstance(project);
        editorCoverLayerManager.registerEditorCoverLayer(editor);
        // Prevent computation when 'hide visualization' is toggled
        if (!EditorCoverLayerManager.getInstance(project).isVisible())
        {
            return;
        }
        VirtualFile virtualFile = file.getVirtualFile();
        IEditorCoverLayerLogger logger = editorCoverLayerManager.getLogger();
        if (logger != null)
        {
            logger.log(EditorCoverLayerLogEnum.FileOpened, virtualFile.getName());
        }
        editorCoverLayerManager.updateEditorCoverLayerFor(virtualFile);
    }

    @Override
    public void editorReleased(@NotNull EditorFactoryEvent event)
    {
        Editor editor = event.getEditor();
        Project project = editor.getProject();
        if (project == null)
        {
            return;
        }
        EditorCoverLayerManager editorCoverLayerManager = EditorCoverLayerManager.getInstance(project);
        Document document = editor.getDocument();
        PsiFile file = PsiDocumentManager.getInstance(project).getPsiFile(document);
        if (file != null)
        {
            IEditorCoverLayerLogger logger = editorCoverLayerManager.getLogger();
            if (logger != null)
            {
                logger.log(EditorCoverLayerLogEnum.FileClosed, file.getVirtualFile().getName());
            }
        }
        editorCoverLayerManager.unregisterEditorCoverLayer(editor);
    }
}
