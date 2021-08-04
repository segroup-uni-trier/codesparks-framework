/*
 * Copyright (c) 2021. Oliver Moseler
 */
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

public class MyEditorFactoryListener implements EditorFactoryListener
{
    @Override
    public void editorCreated(@NotNull final EditorFactoryEvent event)
    {
        final Editor editor = event.getEditor();
        final Project project = editor.getProject();
        if (project == null)
        {
            return;
        }
        final Document document = editor.getDocument();
        final PsiFile file = PsiDocumentManager.getInstance(project).getPsiFile(document);
        if (file == null)
        {
            return;
        }
        final EditorCoverLayerManager editorCoverLayerManager = EditorCoverLayerManager.getInstance(project);
        editorCoverLayerManager.registerEditorCoverLayer(editor);
        // Prevent computation when 'hide visualization' is toggled
        if (!EditorCoverLayerManager.getInstance(project).isVisible())
        {
            return;
        }
        final VirtualFile virtualFile = file.getVirtualFile();
        final IEditorCoverLayerLogger logger = editorCoverLayerManager.getLogger();
        if (logger != null)
        {
            logger.log(EditorCoverLayerLogEnum.FileOpened, virtualFile.getName());
        }
        editorCoverLayerManager.updateEditorCoverLayerFor(virtualFile);
    }

    @Override
    public void editorReleased(@NotNull final EditorFactoryEvent event)
    {
        final Editor editor = event.getEditor();
        final Project project = editor.getProject();
        if (project == null)
        {
            return;
        }
        final EditorCoverLayerManager editorCoverLayerManager = EditorCoverLayerManager.getInstance(project);
        final Document document = editor.getDocument();
        final PsiFile file = PsiDocumentManager.getInstance(project).getPsiFile(document);
        if (file != null)
        {
            final IEditorCoverLayerLogger logger = editorCoverLayerManager.getLogger();
            if (logger != null)
            {
                logger.log(EditorCoverLayerLogEnum.FileClosed, file.getVirtualFile().getName());
            }
        }
        editorCoverLayerManager.unregisterEditorCoverLayer(editor);
    }
}
