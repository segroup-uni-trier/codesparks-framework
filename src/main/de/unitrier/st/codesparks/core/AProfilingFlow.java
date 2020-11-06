/*
 * Copyright (C) 2020, Oliver Moseler
 */
package de.unitrier.st.codesparks.core;

import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.editor.ex.EditorEx;
import com.intellij.openapi.editor.ex.util.EditorUtil;
import com.intellij.openapi.fileEditor.FileEditor;
import com.intellij.openapi.fileEditor.FileEditorManager;
import com.intellij.openapi.fileEditor.FileEditorManagerEvent;
import com.intellij.openapi.fileEditor.FileEditorManagerListener;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.util.Computable;
import com.intellij.openapi.util.IconLoader;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.openapi.wm.RegisterToolWindowTask;
import com.intellij.openapi.wm.ToolWindow;
import com.intellij.openapi.wm.ToolWindowAnchor;
import com.intellij.openapi.wm.ToolWindowManager;
import com.intellij.ui.content.ContentFactory;
import com.intellij.ui.content.ContentManager;
import com.intellij.util.messages.MessageBus;
import com.intellij.util.ui.UIUtil;
import de.unitrier.st.codesparks.core.data.AProfilingArtifact;
import de.unitrier.st.codesparks.core.data.IThreadArtifactFilter;
import de.unitrier.st.codesparks.core.editorcoverlayer.EditorCoverLayerItem;
import de.unitrier.st.codesparks.core.editorcoverlayer.EditorCoverLayerManager;
import de.unitrier.st.codesparks.core.editorcoverlayer.IEditorCoverLayerUpdater;
import de.unitrier.st.codesparks.core.localization.LocalizationUtil;
import de.unitrier.st.codesparks.core.logging.ProfilingLogger;
import de.unitrier.st.codesparks.core.logging.UserActivityEnum;
import de.unitrier.st.codesparks.core.logging.UserActivityLogger;
import de.unitrier.st.codesparks.core.overview.AThreadStateArtifactFilter;
import de.unitrier.st.codesparks.core.overview.IArtifactFilter;
import de.unitrier.st.codesparks.core.overview.ICurrentFileArtifactFilter;
import de.unitrier.st.codesparks.core.overview.ProfilingArtifactOverview;
import de.unitrier.st.codesparks.core.properties.PropertiesFile;
import de.unitrier.st.codesparks.core.properties.PropertiesUtil;
import de.unitrier.st.codesparks.core.properties.PropertyKey;
import de.unitrier.st.codesparks.core.visualization.AArtifactVisualizationLabelFactory;
import de.unitrier.st.codesparks.core.visualization.AProfilingDataVisualizer;
import de.unitrier.st.codesparks.core.visualization.ArtifactVisualizationLabelFactoryCache;
import de.unitrier.st.codesparks.core.visualization.thread.VisualThreadArtifactClusterPropertiesManager;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;

public abstract class AProfilingFlow implements Runnable, IEditorCoverLayerUpdater
{
    protected IProfilingDataProvider dataProvider;
    protected AProfilingDataVisualizer dataVisualizer;
    protected IProfilingResultToCodeMatcher matcher;
    protected IProfilingResult result;
    protected final Project project;

    protected AProfilingFlow(Project project)
    {
        this.project = project;
        EditorCoverLayerManager.getInstance(project).setEditorCoverLayerUpdater(this);
        ProfilingFlowManager.getInstance().setCurrentProfilingFlow(this);
        MessageBus messageBus = project.getMessageBus();
        messageBus.connect().subscribe(FileEditorManagerListener.FILE_EDITOR_MANAGER, new FileEditorManagerListener()
        {
            @Override
            public void selectionChanged(@NotNull FileEditorManagerEvent event)
            {
                FileEditor selectedEditor = event.getManager().getSelectedEditor();
                if (selectedEditor != null)
                {
                    VirtualFile file = selectedEditor.getFile();
                    if (file != null)
                    {
                        String name = file.getName();
                        UserActivityLogger.getInstance().log(UserActivityEnum.FileSelected, name);
                    }
                }
                ProfilingArtifactOverview.getInstance().filterOverView();
            }
        });
    }

    public Project getProject()
    {
        return project;
    }

    @Override
    public void updateEditorCoverLayerFor(VirtualFile virtualFile)
    {
        synchronized (uiLock)
        {
            Collection<AProfilingArtifact> matchedResults = matchResultsToCodeFiles(virtualFile);

            Collection<EditorCoverLayerItem> overlayElements = createVisualization(matchedResults);

            displayVisualizations(overlayElements);
        }
//        createAndDisplayVisualizations(matchResults);
//        matchCreateAndDisplayVisualizations(virtualFile);
    }

    private boolean collectData()
    {
        if (dataProvider != null)
        {
            return dataProvider.collectData();
        } else
        {
            ProfilingLogger.addText(String.format("%s: data collector not setup!", getClass()));
            return false;
        }
    }

    private IProfilingResult processData()
    {
        if (dataProvider != null)
        {
            return dataProvider.processData();
        } else
        {
            ProfilingLogger.addText(String.format("%s: data processor not setup!", getClass()));
            return null;
        }
    }

    private Collection<EditorCoverLayerItem> createVisualization(Collection<AProfilingArtifact> matchedArtifacts)
    {
        if (dataVisualizer != null)
        {
            return dataVisualizer.createVisualizations(project, matchedArtifacts);
        } else
        {
            ProfilingLogger.addText(String.format("%s: data visualizer not setup!", getClass()));
            return new ArrayList<>();
        }
    }

    private Collection<AProfilingArtifact> matchResultsToCodeFiles(VirtualFile... virtualFiles)
    {
        if (matcher != null)
        {
            return matcher.matchResultsToCodeFiles(result, project, virtualFiles);
        } else
        {
            ProfilingLogger.addText(String.format("%s: results to code matcher not setup!", getClass()));
            return new ArrayList<>();
        }
    }

    @Override
    public final void run()
    {
        try
        {
            clearVisualizations();
            if (collectData())
            {
                result = processData();
                if (result != null)
                {
                    ProfilingResultManager instance = ProfilingResultManager.getInstance();
                    instance.setProfilingResult(result);

                    FileEditor[] editors = ApplicationManager.getApplication().runReadAction((Computable<FileEditor[]>) () ->
                            FileEditorManager.getInstance(project).getAllEditors());

                    for (FileEditor editor : editors)
                    {
                        final EditorEx editorEx = EditorUtil.getEditorEx(editor);
                        if (editorEx == null)
                        {
                            continue;
                        }
                        // assert editorEx != null;

//                        int lineHeight = editorEx.getLineHeight();
//                        System.out.println("editor line height=" + lineHeight);
//
//                        System.out.println("editor min font size = " + EditorFontsConstants.getMinEditorFontSize());
//                        System.out.println("editor max font size = " + EditorFontsConstants.getMaxEditorFontSize());
//                        System.out.println("editor default font size = " + EditorFontsConstants.getDefaultEditorFontSize());
//                        System.out.println("editor default font size = " + EditorFontsConstants.checkAndFixEditorFontSize(20));

                        ApplicationManager.getApplication().runReadAction(() ->
                                EditorCoverLayerManager.getInstance(project).registerEditorCoverLayer(editorEx)
                        );
                    }

                    synchronized (uiLock)
                    {
                        VirtualFile[] virtualFiles = Arrays.stream(editors).map(FileEditor::getFile).toArray(VirtualFile[]::new);
                        clearVisualizationCache();

                        Collection<AProfilingArtifact> matchedResults = matchResultsToCodeFiles(virtualFiles);

                        Collection<EditorCoverLayerItem> overlayElements = createVisualization(matchedResults);

                        displayVisualizations(overlayElements);
                    }
//                    createAndDisplayVisualizations(matchResults);

//                    matchCreateAndDisplayVisualizations(virtualFiles);
                    displayProfilingArtifactOverview();
                } else
                {
                    ProfilingLogger.addText("%s: No profiling results available.", getClass().getName());
                }
            }
        } catch (Exception e)
        {
            e.printStackTrace();
            ProfilingLogger.addText(e.toString());
        }
    }

    private final Object uiLock = new Object();

    public void applyThreadArtifactFilter(IThreadArtifactFilter threadFilter)
    {
        synchronized (uiLock)
        {
            try
            {
                clearVisualizations();

                clearVisualizationCache();

                ProfilingResultManager instance = ProfilingResultManager.getInstance();

                IProfilingResult profilingResult = instance.getProfilingResult();

                profilingResult.applyThreadArtifactFilter(threadFilter);

                FileEditor[] editors = ApplicationManager.getApplication().runReadAction((Computable<FileEditor[]>) () ->
                        FileEditorManager.getInstance(project).getAllEditors());

                for (FileEditor editor : editors)
                {
                    final EditorEx editorEx = EditorUtil.getEditorEx(editor);
                    assert editorEx != null;
                    ApplicationManager.getApplication().runReadAction(() ->
                            EditorCoverLayerManager.getInstance(project).registerEditorCoverLayer(editorEx)
                    );
                }
                VirtualFile[] virtualFiles = Arrays.stream(editors).map(FileEditor::getFile).toArray(VirtualFile[]::new);

                Collection<AProfilingArtifact> matchedResults = matchResultsToCodeFiles(virtualFiles);

                Collection<EditorCoverLayerItem> overlayElements = createVisualization(matchedResults);

                displayVisualizations(overlayElements);

                // createAndDisplayVisualizations(matchResults);

                // matchCreateAndDisplayVisualizations(virtualFiles);

                displayProfilingArtifactOverview();

            } catch (Exception e)
            {
                e.printStackTrace();
            }
        }
    }

//    private void createAndDisplayVisualizations(Collection<AProfilingArtifact> matchedResults)
//    {
//        Collection<EditorCoverLayerItem> overlayElements = createVisualization(matchedResults);
//        displayVisualizations(overlayElements);
//    }

//    private void matchCreateAndDisplayVisualizations(VirtualFile... virtualFiles)
//    {
//        synchronized (uiLock)
//        {
//            if (matcher == null)
//            {
//                ProfilingLogger.addText(String.format("%s: results to code matcher not setup!", getClass()));
//                return;
//            }
//            Collection<AProfilingArtifact> matchedResults = matcher.matchResultsToCodeFiles(result, project, virtualFiles);
//            Collection<EditorCoverLayerItem> overlayElements = createVisualization(matchedResults);
//            displayVisualizations(overlayElements);
//        }
//    }

    private void displayProfilingArtifactOverview()
    {
        final ToolWindowManager toolWindowManager = ToolWindowManager.getInstance(project);
        final String toolWindowIdName = LocalizationUtil.getLocalizedString("profiling.ui.artifactoverview.displayname");
        UIUtil.invokeLaterIfNeeded(() -> {
            ToolWindow toolWindow = toolWindowManager.getToolWindow(toolWindowIdName);
            if (toolWindow == null)
            {
                toolWindow = toolWindowManager.registerToolWindow(new RegisterToolWindowTask(
                        toolWindowIdName
                        , ToolWindowAnchor.RIGHT
                        , null
                        , true
                        , true
                        , true
                        , true
                        , null
                        , IconLoader.getIcon("/icons/profiling_13x12.png")
                        , () -> toolWindowIdName
                ));
            }
            addResultsToToolWindow(toolWindow);
            toolWindow.show(() -> {});
        });
    }

    private void addResultsToToolWindow(ToolWindow toolWindow)
    {
        ContentManager contentManager = toolWindow.getContentManager();
        contentManager.removeAllContents(true);

        ProfilingArtifactOverview profilingArtifactOverview = ProfilingArtifactOverview.getInstance();

        final Boolean threadVisualizationsEnabled = PropertiesUtil.getBooleanPropertyValueOrDefault(PropertiesFile.USER_INTERFACE_PROPERTIES,
                PropertyKey.THREAD_VISUALIZATIONS_ENABLED, true);
        profilingArtifactOverview.setFilterByThreadPanelVisible(threadVisualizationsEnabled);

        profilingArtifactOverview.setProfilingResult(result);

        contentManager.addContent(ContentFactory.SERVICE.getInstance().createContent
                (profilingArtifactOverview.getRootPanel(), "", true));
    }

    private void clearVisualizations()
    {
        final VisualThreadArtifactClusterPropertiesManager propertiesManager = VisualThreadArtifactClusterPropertiesManager.getInstance();
        propertiesManager.clearProperties();
        final EditorCoverLayerManager editorCoverLayerManager = EditorCoverLayerManager.getInstance(project);
        editorCoverLayerManager.clearAllEditorCoverLayers();

        final ToolWindowManager toolWindowManager = ToolWindowManager.getInstance(project);
        final String toolWindowIdName = LocalizationUtil.getLocalizedString("profiling.ui.artifactoverview.displayname");
        UIUtil.invokeLaterIfNeeded(() -> {
            ToolWindow toolWindow = toolWindowManager.getToolWindow(toolWindowIdName);
            if (toolWindow != null)
            {
                toolWindow.hide();
            }
        });
    }

    private void clearVisualizationCache()
    {
        ArtifactVisualizationLabelFactoryCache.getInstance().clearCache();
    }

    private void displayVisualizations(Collection<EditorCoverLayerItem> overlayElements)
    {
        EditorCoverLayerManager editorCoverLayerManager = EditorCoverLayerManager.getInstance(project);
//        overlayElements.forEach(editorCoverLayerManager::add);
        for (EditorCoverLayerItem overlayElement : overlayElements)
        {
            editorCoverLayerManager.add(overlayElement);
        }
    }

    public void registerStandardLibraryArtifactFilter(IArtifactFilter standardLibraryArtifactFilter)
    {
        ProfilingArtifactOverview.getInstance().registerStandardLibraryArtifactFilter(standardLibraryArtifactFilter);
    }

    public void registerCurrentFileArtifactFilter(ICurrentFileArtifactFilter currentFileArtifactFilter)
    {
        ProfilingArtifactOverview.getInstance().registerCurrentFileArtifactFilter(currentFileArtifactFilter);
    }

    public void registerThreadStateArtifactFilter(AThreadStateArtifactFilter threadStateArtifactFilter)
    {
        ProfilingArtifactOverview.getInstance().registerThreadStateArtifactFilter(threadStateArtifactFilter);
    }

    public Class<? extends AArtifactVisualizationLabelFactory> getDefaultVisualizationLabelFactoryClass()
    {
        if (dataVisualizer != null)
        {
            final AArtifactVisualizationLabelFactory defaultArtifactVisualizationLabelFactory = dataVisualizer.getDefaultArtifactVisualizationLabelFactory();
            if (defaultArtifactVisualizationLabelFactory == null)
            {
                ProfilingLogger.addText(String.format("%s: artifact visualization label factory not setup!", getClass()));
                return null;
            }
            return defaultArtifactVisualizationLabelFactory.getClass();
        } else
        {
            ProfilingLogger.addText(String.format("%s: data visualizer not setup!", getClass()));
            return null;
        }
    }
}
