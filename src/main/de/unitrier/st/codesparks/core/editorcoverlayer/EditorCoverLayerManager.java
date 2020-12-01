package de.unitrier.st.codesparks.core.editorcoverlayer;

import com.intellij.openapi.Disposable;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.editor.EditorFactory;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.util.Computable;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.psi.PsiDocumentManager;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFile;
import org.jetbrains.annotations.NotNull;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

/*
 * Copyright (c), Oliver Moseler, 2020
 */
public final class EditorCoverLayerManager implements Disposable// implements ProjectComponent
{
    private final Project project;
    private final Map<VirtualFile, Map<Editor, EditorCoverLayer>> coverLayers;
    private IEditorCoverLayerUpdater editorCoverLayerUpdater;
    private boolean visible;

    private EditorCoverLayerManager(Project project)
    {
        this.project = project;
        this.visible = true;
        coverLayers = new HashMap<>();
        MyEditorFactoryListener myEditorFactoryListener = new MyEditorFactoryListener();
        EditorFactory.getInstance().addEditorFactoryListener(myEditorFactoryListener, this);
    }

    public void setEditorCoverLayerUpdater(IEditorCoverLayerUpdater editorCoverLayerUpdater)
    {
        this.editorCoverLayerUpdater = editorCoverLayerUpdater;
    }

    public void updateEditorCoverLayerFor(VirtualFile virtualFile)
    {
        if (editorCoverLayerUpdater == null || virtualFile == null)
        {
            return;
        }
        editorCoverLayerUpdater.updateEditorCoverLayerFor(virtualFile);
    }

    private static EditorCoverLayerManager instance;

    public static EditorCoverLayerManager getInstance(@NotNull Project project)
    {
        synchronized (EditorCoverLayerManager.class)
        {
            if (instance == null)
            {
                return (instance = new EditorCoverLayerManager(project));
            }

            if (instance.project == project) // instance.project.equals(project)
            {
                return instance;
            } else
            {
                instance.clear();
                return (instance = new EditorCoverLayerManager(project));
            }
        }
        // return project.getComponent(EditorCoverLayerManager.class);
    }

    public void registerEditorCoverLayer(Editor editor)
    {
        PsiFile psiFile = PsiDocumentManager.getInstance(project).getPsiFile(editor.getDocument());
        if (psiFile == null)
        {
            return;
        }
        VirtualFile virtualFile = psiFile.getVirtualFile();
        if (virtualFile == null)
        {
            return;
        }
        Map<Editor, EditorCoverLayer> layerMap = coverLayers.computeIfAbsent(virtualFile, k -> new HashMap<>());
        EditorCoverLayer editorCoverLayer = layerMap.get(editor);
        if (editorCoverLayer == null)
        {
            editorCoverLayer = new EditorCoverLayer(editor);
            layerMap.put(editor, editorCoverLayer);
            editor.getContentComponent().add(editorCoverLayer);
            editorCoverLayer.setVisible(true);
        }
    }

    void unregisterEditorCoverLayer(Editor editor)
    {
        PsiFile psiFile = PsiDocumentManager.getInstance(project).getPsiFile(editor.getDocument());
        if (psiFile == null)
        {
            return;
        }
        VirtualFile virtualFile = psiFile.getVirtualFile();
        Map<Editor, EditorCoverLayer> layerMap = coverLayers.get(virtualFile);
        if (layerMap == null)
        {
            return;
        }
        EditorCoverLayer editorCoverLayer = layerMap.get(editor);
        if (editorCoverLayer == null)
        {
            return;
        }
        editor.getContentComponent().remove(editorCoverLayer);
        editorCoverLayer.setVisible(false);
        layerMap.remove(editor);
    }

    public boolean add(EditorCoverLayerItem element)
    {
        return ApplicationManager.getApplication().runReadAction((Computable<Boolean>) () -> {
            PsiElement psiElement = element.getPositionalElement();
            if (psiElement == null || !psiElement.isValid())
            {
                return false;
            }
            VirtualFile virtualFile = psiElement.getContainingFile().getVirtualFile();
            if (virtualFile == null)
            {
                return false;
            }
            Map<Editor, EditorCoverLayer> layerMap = coverLayers.get(virtualFile);
            if (layerMap == null)
            {
                return false;
            }
            Collection<EditorCoverLayer> editorCoverLayers = layerMap.values();
            if (editorCoverLayers.size() == 0)
            {


                return false;
            }
            // At this point the editor cover layers (respective editors) are holding the same file, e.g. when in split mode, i.e. the same
            // file will be displayed in multiple editors
            for (EditorCoverLayer editorCoverLayer : editorCoverLayers)
            { /* In case we have multiple editors for the same file, also multiple equally profiling artifacts with respective
                 visualizations will be created. So if the same profiling artifact could not be added to the editorcoverlayer since an equal
                 one was already added
                 to it, addLayerItem it to the next editorcoverlayer
                */
                if (editorCoverLayer.addLayerItem(element))
                {
                    break;
                }
            }
            //editorCoverLayers.forEach(editorCoverLayer -> editorCoverLayer.addLayerItem(element));
            return true;
        });
    }

//    void remove(EditorCoverLayerItem element)
//    {
//        ApplicationManager.getApplication().runReadAction(() -> {
//            PsiElement psiElement = element.getPositionalElement();
//            if (psiElement == null)
//            {
//                return;
//            }
//            VirtualFile virtualFile = psiElement.getContainingFile().getVirtualFile();
//            if (virtualFile == null)
//            {
//                return;
//            }
//            Map<Editor, EditorCoverLayer> layerMap = coverLayers.get(virtualFile);
//            if (layerMap == null)
//            {
//                return;
//            }
//            layerMap.values().forEach(editorCoverLayer -> editorCoverLayer.removeLayerItem(element));
//        });
//    }

    @Override
    public void dispose()
    {
        clear();
    }
//
//    @Override
//    public void projectClosed()
//    {
//        clear();
//    }

    private void clear()
    {
        coverLayers.values().forEach(layerMap -> {
            layerMap.values().forEach(EditorCoverLayer::clear);
            layerMap.clear();
        });
        coverLayers.clear();
    }

    public void clearAllEditorCoverLayers()
    {
        coverLayers.values().forEach(layerMap -> {
            layerMap.values().forEach(EditorCoverLayer::clear);
            layerMap.keySet().forEach(editor -> editor.getComponent().repaint());
        });
    }

    public void setEditorCoverLayersVisible(boolean visible)
    {
        this.visible = visible;
        coverLayers.values().forEach(layerMap -> layerMap.values().forEach(editorCoverLayer -> editorCoverLayer.setVisible(visible)));
    }

    boolean isVisible()
    {
        return visible;
    }

    // Logging

    private IEditorCoverLayerLogger logger;

    public void registerLogger(IEditorCoverLayerLogger logger)
    {
        this.logger = logger;
    }

    public IEditorCoverLayerLogger getLogger()
    {
        return this.logger;
    }
}
