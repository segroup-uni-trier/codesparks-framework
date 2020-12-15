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
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.openapi.wm.RegisterToolWindowTask;
import com.intellij.openapi.wm.ToolWindow;
import com.intellij.openapi.wm.ToolWindowAnchor;
import com.intellij.openapi.wm.ToolWindowManager;
import com.intellij.ui.content.ContentFactory;
import com.intellij.ui.content.ContentManager;
import com.intellij.util.messages.MessageBus;
import com.intellij.util.ui.UIUtil;
import de.unitrier.st.codesparks.core.data.*;
import de.unitrier.st.codesparks.core.editorcoverlayer.EditorCoverLayerItem;
import de.unitrier.st.codesparks.core.editorcoverlayer.EditorCoverLayerManager;
import de.unitrier.st.codesparks.core.editorcoverlayer.IEditorCoverLayerUpdater;
import de.unitrier.st.codesparks.core.localization.LocalizationUtil;
import de.unitrier.st.codesparks.core.logging.CodeSparksLogger;
import de.unitrier.st.codesparks.core.logging.UserActivityEnum;
import de.unitrier.st.codesparks.core.logging.UserActivityLogger;
import de.unitrier.st.codesparks.core.overview.AThreadStateArtifactFilter;
import de.unitrier.st.codesparks.core.overview.ArtifactOverview;
import de.unitrier.st.codesparks.core.overview.IArtifactFilter;
import de.unitrier.st.codesparks.core.overview.ICurrentFileArtifactFilter;
import de.unitrier.st.codesparks.core.properties.PropertiesFile;
import de.unitrier.st.codesparks.core.properties.PropertiesUtil;
import de.unitrier.st.codesparks.core.properties.PropertyKey;
import de.unitrier.st.codesparks.core.visualization.AArtifactVisualizationLabelFactory;
import de.unitrier.st.codesparks.core.visualization.ADataVisualizer;
import de.unitrier.st.codesparks.core.visualization.ArtifactVisualizationLabelFactoryCache;
import de.unitrier.st.codesparks.core.visualization.thread.VisualThreadClusterPropertiesManager;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;

/*
 * Copyright (c), Oliver Moseler, 2020
 */
public abstract class ACodeSparksFlow implements Runnable, IEditorCoverLayerUpdater
{
    protected IDataProvider dataProvider;
    protected ADataVisualizer dataVisualizer;
    protected IArtifactPoolToCodeMatcher matcher;
    protected IArtifactPool artifactPool;
    protected final Project project;

    protected ACodeSparksFlow(Project project)
    {
        this.project = project;
        EditorCoverLayerManager.getInstance(project).setEditorCoverLayerUpdater(this);
        CodeSparksFlowManager.getInstance().setCurrentCodeSparksFlow(this);
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
                ArtifactOverview.getInstance().filterOverView();
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
            Collection<AArtifact> matchedResults = matchArtifactsToCodeFiles(virtualFile);

            Collection<EditorCoverLayerItem> overlayElements = createVisualization(matchedResults);

            displayVisualizations(overlayElements);
        }
    }

    private boolean collectData()
    {
        if (dataProvider != null)
        {
            return dataProvider.collectData();
        } else
        {
            CodeSparksLogger.addText(String.format("%s: data collector not setup!", getClass()));
            return false;
        }
    }

    private IArtifactPool processData()
    {
        if (dataProvider != null)
        {
            return dataProvider.processData();
        } else
        {
            CodeSparksLogger.addText(String.format("%s: data processor not setup!", getClass()));
            return null;
        }
    }

    private Collection<EditorCoverLayerItem> createVisualization(Collection<AArtifact> matchedArtifacts)
    {
        if (dataVisualizer != null)
        {
            return dataVisualizer.createVisualizations(project, matchedArtifacts);
        } else
        {
            CodeSparksLogger.addText(String.format("%s: data visualizer not setup!", getClass()));
            return new ArrayList<>();
        }
    }

    private Collection<AArtifact> matchArtifactsToCodeFiles(VirtualFile... virtualFiles)
    {
        if (matcher != null)
        {
            return matcher.matchArtifactsToCodeFiles(artifactPool, project, virtualFiles);
        } else
        {
            CodeSparksLogger.addText(String.format("%s: artifact pool matcher not setup!", getClass()));
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
                artifactPool = processData();
                if (artifactPool != null)
                {
                    ArtifactPoolManager instance = ArtifactPoolManager.getInstance();
                    instance.setArtifactPool(artifactPool);

                    FileEditor[] editors = ApplicationManager.getApplication().runReadAction((Computable<FileEditor[]>) () ->
                            FileEditorManager.getInstance(project).getAllEditors());

                    for (FileEditor editor : editors)
                    {
                        final EditorEx editorEx = EditorUtil.getEditorEx(editor);
                        if (editorEx == null)
                        {
                            continue;
                        }

                        ApplicationManager.getApplication().runReadAction(() ->
                                EditorCoverLayerManager.getInstance(project).registerEditorCoverLayer(editorEx)
                        );
                    }

                    synchronized (uiLock)
                    {
                        VirtualFile[] virtualFiles = Arrays.stream(editors).map(FileEditor::getFile).toArray(VirtualFile[]::new);
                        clearVisualizationCache();

                        Collection<AArtifact> matchedArtifacts = matchArtifactsToCodeFiles(virtualFiles);

                        Collection<EditorCoverLayerItem> overlayElements = createVisualization(matchedArtifacts);

                        displayVisualizations(overlayElements);
                    }

                    displayArtifactOverview();
                } else
                {
                    CodeSparksLogger.addText("%s: No profiling results available.", getClass().getName());
                }
            }
        } catch (Exception e)
        {
            e.printStackTrace();
            CodeSparksLogger.addText(e.toString());
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

                ArtifactPoolManager instance = ArtifactPoolManager.getInstance();

                IArtifactPool profilingResult = instance.getArtifactPool();

                profilingResult.applyThreadFilter(threadFilter);

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

                Collection<AArtifact> matchedResults = matchArtifactsToCodeFiles(virtualFiles);

                Collection<EditorCoverLayerItem> overlayElements = createVisualization(matchedResults);

                displayVisualizations(overlayElements);

                displayArtifactOverview();

            } catch (Exception e)
            {
                e.printStackTrace();
            }
        }
    }

    private void displayArtifactOverview()
    {
        final ToolWindowManager toolWindowManager = ToolWindowManager.getInstance(project);
        final String toolWindowIdName = LocalizationUtil.getLocalizedString("codesparks.ui.artifactoverview.displayname");
        UIUtil.invokeLaterIfNeeded(() -> {
            ToolWindow toolWindow = toolWindowManager.getToolWindow(toolWindowIdName);
            if (toolWindow == null)
            {
                ImageIcon defaultImageIcon = CoreUtil.getDefaultImageIcon();
                toolWindow = toolWindowManager.registerToolWindow(new RegisterToolWindowTask(
                        toolWindowIdName
                        , ToolWindowAnchor.RIGHT
                        , null
                        , true
                        , true
                        , true
                        , true
                        , null
                        , defaultImageIcon
                        , () -> toolWindowIdName
                ));
            }
            addArtifactsTo(toolWindow);
            toolWindow.show(() -> {});
        });
    }

    private void addArtifactsTo(ToolWindow toolWindow)
    {
        ContentManager contentManager = toolWindow.getContentManager();
        contentManager.removeAllContents(true);

        ArtifactOverview artifactOverview = ArtifactOverview.getInstance();

        final Boolean threadVisualizationsEnabled = PropertiesUtil.getBooleanPropertyValueOrDefault(PropertiesFile.USER_INTERFACE_PROPERTIES,
                PropertyKey.THREAD_VISUALIZATIONS_ENABLED, true);

        artifactOverview.setFilterByThreadPanelVisible(threadVisualizationsEnabled);
        artifactOverview.setArtifactPool(artifactPool);

        contentManager.addContent(ContentFactory.SERVICE.getInstance().createContent
                (artifactOverview.getRootPanel(), "", true));
    }

    private void clearVisualizations()
    {
        final VisualThreadClusterPropertiesManager propertiesManager = VisualThreadClusterPropertiesManager.getInstance();
        propertiesManager.clearProperties();
        final EditorCoverLayerManager editorCoverLayerManager = EditorCoverLayerManager.getInstance(project);
        editorCoverLayerManager.clearAllEditorCoverLayers();

        final ToolWindowManager toolWindowManager = ToolWindowManager.getInstance(project);
        final String toolWindowIdName = LocalizationUtil.getLocalizedString("codesparks.ui.artifactoverview.displayname");
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
        final EditorCoverLayerManager editorCoverLayerManager = EditorCoverLayerManager.getInstance(project);
        for (final EditorCoverLayerItem overlayElement : overlayElements)
        {
            editorCoverLayerManager.add(overlayElement);
        }
    }

    public void registerStandardLibraryArtifactFilter(IArtifactFilter standardLibraryArtifactFilter)
    {
        ArtifactOverview.getInstance().registerStandardLibraryArtifactFilter(standardLibraryArtifactFilter);
    }

    public void registerCurrentFileArtifactFilter(ICurrentFileArtifactFilter currentFileArtifactFilter)
    {
        ArtifactOverview.getInstance().registerCurrentFileArtifactFilter(currentFileArtifactFilter);
    }

    public void registerThreadStateArtifactFilter(AThreadStateArtifactFilter threadStateArtifactFilter)
    {
        ArtifactOverview.getInstance().registerThreadStateArtifactFilter(threadStateArtifactFilter);
    }

    public void registerProgramArtifactVisualizationLabelFactory(AArtifactVisualizationLabelFactory factory)
    {
        ArtifactOverview.getInstance().registerProgramArtifactVisualizationLabelFactory(factory);
    }

    public void registerMetricIdentifier(final Class<? extends AArtifact> artifactClass, final IMetricIdentifier metricIdentifier)
    {
        ArtifactOverview.getInstance().registerMetricIdentifier(artifactClass, metricIdentifier);
    }

    public AArtifactVisualizationLabelFactory getDefaultVisualizationLabelFactory()
    {
        if (dataVisualizer != null)
        {
            final AArtifactVisualizationLabelFactory defaultArtifactVisualizationLabelFactory = dataVisualizer.getDefaultArtifactVisualizationLabelFactory();
            if (defaultArtifactVisualizationLabelFactory == null)
            {
                CodeSparksLogger.addText(String.format("%s: artifact visualization label factory not setup!", getClass()));
                return null;
            }
            return defaultArtifactVisualizationLabelFactory;
        } else
        {
            CodeSparksLogger.addText(String.format("%s: data visualizer not setup!", getClass()));
            return null;
        }
    }
}
