/*
 * Copyright (c) 2022. Oliver Moseler
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
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.openapi.wm.ToolWindow;
import com.intellij.openapi.wm.ToolWindowManager;
import com.intellij.ui.content.Content;
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
import de.unitrier.st.codesparks.core.matching.IArtifactPoolToCodeMatcher;
import de.unitrier.st.codesparks.core.overview.*;
import de.unitrier.st.codesparks.core.properties.PropertiesFile;
import de.unitrier.st.codesparks.core.properties.PropertiesUtil;
import de.unitrier.st.codesparks.core.properties.PropertyKey;
import de.unitrier.st.codesparks.core.visualization.AArtifactVisualizationLabelFactory;
import de.unitrier.st.codesparks.core.visualization.ADataVisualizer;
import de.unitrier.st.codesparks.core.visualization.ArtifactVisualizationLabelFactoryCache;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;

public abstract class ACodeSparksFlow implements Runnable, IEditorCoverLayerUpdater
{
    protected IDataProvider dataProvider;
    protected ADataVisualizer dataVisualizer;
    protected IArtifactPoolToCodeMatcher matcher;
    protected IArtifactPool artifactPool;
    protected final Project project;

    protected ACodeSparksFlow(final Project project)
    {
        this.project = project;
        EditorCoverLayerManager.getInstance(project).setEditorCoverLayerUpdater(this);
        CodeSparksFlowManager.getInstance().setCurrentCodeSparksFlow(this);
        final MessageBus messageBus = project.getMessageBus();
        messageBus.connect().subscribe(FileEditorManagerListener.FILE_EDITOR_MANAGER, new FileEditorManagerListener()
        {
            @Override
            public void selectionChanged(@NotNull FileEditorManagerEvent event)
            {
                final FileEditor selectedEditor = event.getManager().getSelectedEditor();
                if (selectedEditor != null)
                {
                    final VirtualFile file = selectedEditor.getFile();
                    if (file != null)
                    {
                        final String name = file.getName();
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

    public ADataVisualizer getDataVisualizer()
    {
        return dataVisualizer;
    }

    @Override
    public void updateEditorCoverLayerFor(final VirtualFile virtualFile)
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

    private void postProcess(final IArtifactPool artifactPool)
    {
        if (dataProvider != null)
        {
            dataProvider.postProcess(artifactPool);
        } else
        {
            CodeSparksLogger.addText(String.format("%s: data processor not setup!", getClass()));
        }
    }

    private Collection<EditorCoverLayerItem> createVisualization(final Collection<AArtifact> matchedArtifacts)
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

    private Collection<AArtifact> matchArtifactsToCodeFiles(final VirtualFile... virtualFiles)
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
            clearArtifactPool();
            clearVisualizations();
            if (collectData())
            {
                artifactPool = processData();
                if (artifactPool != null)
                {
                    artifactPool.registerArtifactClassDisplayNameProvider(artifactClassDisplayNameProvider);

                    final ArtifactPoolManager instance = ArtifactPoolManager.getInstance();
                    instance.setArtifactPool(artifactPool);

                    postProcess(artifactPool);

                    final FileEditor[] editors = ApplicationManager.getApplication().runReadAction((Computable<FileEditor[]>) () ->
                            FileEditorManager.getInstance(project).getAllEditors());

                    for (final FileEditor editor : editors)
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
                        final VirtualFile[] virtualFiles = Arrays.stream(editors).map(FileEditor::getFile).toArray(VirtualFile[]::new);
                        clearVisualizationCache();

                        final Collection<AArtifact> matchedArtifacts = matchArtifactsToCodeFiles(virtualFiles);

                        final Collection<EditorCoverLayerItem> overlayElements = createVisualization(matchedArtifacts);

                        displayVisualizations(overlayElements);
                    }

                    displayArtifactOverview();
                } else
                {
                    CodeSparksLogger.addText("%s: artifact pool is null.", getClass().getName());
                }
            }
        } catch (Exception e)
        {
            e.printStackTrace();
            CodeSparksLogger.addText(e.toString());
        }
    }

    private final Object uiLock = new Object();

    public void applyThreadArtifactFilter(final IThreadArtifactFilter threadFilter)
    {
        synchronized (uiLock)
        {
            try
            {
                clearVisualizations();

                clearVisualizationCache();

                final ArtifactPoolManager instance = ArtifactPoolManager.getInstance();

                final IArtifactPool artifactPool = instance.getArtifactPool();

                artifactPool.applyThreadFilter(threadFilter);

                final FileEditor[] editors = ApplicationManager.getApplication().runReadAction((Computable<FileEditor[]>) () ->
                        FileEditorManager.getInstance(project).getAllEditors());

                for (final FileEditor editor : editors)
                {
                    final EditorEx editorEx = EditorUtil.getEditorEx(editor);
                    assert editorEx != null;
                    ApplicationManager.getApplication().runReadAction(() ->
                            EditorCoverLayerManager.getInstance(project).registerEditorCoverLayer(editorEx)
                    );
                }
                final VirtualFile[] virtualFiles = Arrays.stream(editors).map(FileEditor::getFile).toArray(VirtualFile[]::new);

                final Collection<AArtifact> matchedResults = matchArtifactsToCodeFiles(virtualFiles);

                final Collection<EditorCoverLayerItem> overlayElements = createVisualization(matchedResults);

                displayVisualizations(overlayElements);

                displayArtifactOverview();

            } catch (Exception e)
            {
                e.printStackTrace();
            }
        }
    }

    private static Content currentArtifactOverViewContent;

    private void displayArtifactOverview()
    {
        final ToolWindow codeSparksToolWindow = CoreUtil.getCodeSparksToolWindow(project);
        final ContentManager contentManager = codeSparksToolWindow.getContentManager();
        final ArtifactOverview artifactOverview = ArtifactOverview.getInstance();
        artifactOverview.setArtifactPool(artifactPool);
        final Boolean threadVisualizationsEnabled = PropertiesUtil.getBooleanPropertyValueOrDefault(PropertiesFile.USER_INTERFACE_PROPERTIES,
                PropertyKey.THREAD_VISUALIZATIONS_ENABLED, true);
        artifactOverview.setFilterByThreadPanelVisible(threadVisualizationsEnabled);
        final String artifactOverviewDisplayName = LocalizationUtil.getLocalizedString("codesparks.ui.artifactoverview.displayname");
        final ContentFactory contentFactory = ContentFactory.getInstance();
        final Content content = contentFactory.createContent(artifactOverview.getRootPanel(), artifactOverviewDisplayName, true);
        UIUtil.invokeLaterIfNeeded(() -> {
            if (currentArtifactOverViewContent != null)
            {
                contentManager.removeContent(currentArtifactOverViewContent, true);
            }
            contentManager.addContent(content);
            currentArtifactOverViewContent = content;
            contentManager.setSelectedContent(content);
            codeSparksToolWindow.show();
        });
    }

    private void clearArtifactPool()
    {
        if (artifactPool != null)
        {
            artifactPool.clear();
        }
    }

    private void clearVisualizations()
    {
//        VisualThreadClusterPropertiesManager.clearInstances(); // TODO: remove since it has an empty method body
//        final VisualThreadClusterPropertiesManager propertiesManager = VisualThreadClusterPropertiesManager.getInstance();
//        propertiesManager.clearProperties();

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

    private void displayVisualizations(final Collection<EditorCoverLayerItem> overlayElements)
    {
        final EditorCoverLayerManager editorCoverLayerManager = EditorCoverLayerManager.getInstance(project);
        for (final EditorCoverLayerItem overlayElement : overlayElements)
        {
            editorCoverLayerManager.add(overlayElement);
        }
    }

    public void registerStandardLibraryArtifactFilter(final IArtifactFilter standardLibraryArtifactFilter)
    {
        ArtifactOverview.getInstance().registerStandardLibraryArtifactFilter(standardLibraryArtifactFilter);
    }

    public void registerCurrentFileArtifactFilter(final ICurrentFileArtifactFilter currentFileArtifactFilter)
    {
        ArtifactOverview.getInstance().registerCurrentFileArtifactFilter(currentFileArtifactFilter);
    }

    public void registerThreadStateArtifactFilter(final AThreadStateArtifactFilter threadStateArtifactFilter)
    {
        ArtifactOverview.getInstance().registerThreadStateArtifactFilter(threadStateArtifactFilter);
    }

    public void registerProgramArtifactVisualizationLabelFactories(final AArtifactVisualizationLabelFactory... factories)
    {
        ArtifactOverview.getInstance().registerProgramArtifactVisualizationLabelFactories(factories);
    }

    public void registerArtifactMetricComparatorForSorting(final Class<? extends AArtifact> artifactClass,
                                                           final ArtifactMetricComparator... artifactMetricComparators)
    {
        ArtifactOverview.getInstance().registerArtifactMetricComparatorForSorting(artifactClass, artifactMetricComparators);
    }

    public void registerArtifactClassVisualizationLabelFactory(
            final Class<? extends AArtifact> artifactClass
            , final AArtifactVisualizationLabelFactory factory
    )
    {
        ArtifactOverview.getInstance().registerArtifactClassVisualizationLabelFactory(artifactClass, factory);
    }

    private IArtifactClassDisplayNameProvider artifactClassDisplayNameProvider;

    public void registerArtifactClassDisplayNameProvider(final IArtifactClassDisplayNameProvider artifactClassDisplayNameProvider)
    {
        this.artifactClassDisplayNameProvider = artifactClassDisplayNameProvider;
    }

    /**
     * Override to replace the image icon, e.g. used by the tool windows.
     */
    protected ImageIcon getImageIcon()
    {
        return CoreUtil.getDefaultImageIcon();
    }
}
