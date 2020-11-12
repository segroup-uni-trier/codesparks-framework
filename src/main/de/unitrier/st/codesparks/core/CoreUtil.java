package de.unitrier.st.codesparks.core;

import com.intellij.openapi.components.ServiceManager;
import com.intellij.openapi.editor.ex.EditorEx;
import com.intellij.openapi.editor.ex.util.EditorUtil;
import com.intellij.openapi.fileEditor.FileEditor;
import com.intellij.openapi.fileEditor.FileEditorManager;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.project.ProjectManager;
import com.intellij.util.ui.UIUtil;
import de.unitrier.st.codesparks.core.data.AArtifact;
import de.unitrier.st.codesparks.core.service.ACodeSparksInstanceService;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;
import java.awt.*;
import java.text.DecimalFormat;
import java.util.Arrays;
import java.util.Collection;
import java.util.Optional;

public final class CoreUtil
{
    private CoreUtil() {}

    private static final DecimalFormat df = new DecimalFormat("0.00");

    public static String formatPercentage(double value)
    {
        String percentageText;
        if (value < 0.0001)
        {
            percentageText = "<0.01%";
        } else
        {
            synchronized (df)
            {
                percentageText = df.format(value * 100) + "%";
            }
        }
//        StringBuilder strb = new StringBuilder();
//        int length = percentageText.length();
//        for (int i = 0; i < 6 - length; i++)
//        {
//            strb.append(" ");
//        }
//        strb.append(percentageText);
//        percentageText = strb.toString();
        String errorMessage = "did not format " + value + " properly: " + percentageText;
        assert percentageText.length() > 1 : errorMessage;
        assert percentageText.length() < 10 : errorMessage;
        return percentageText;
    }

    public static String formatPercentageWithLeadingWhitespace(double value)
    {
        String percentageText;
        if (value < 0.0001)
        {
            percentageText = "<0.01%";
        } else
        {
            synchronized (df)
            {
                percentageText = df.format(value * 100) + "%";
            }
        }
        StringBuilder strb = new StringBuilder();
        int length = percentageText.length();
        for (int i = 0; i < 6 - length; i++)
        {
            strb.append(" ");
        }
        strb.append(percentageText);
        percentageText = strb.toString();
        String errorMessage = "did not format " + value + " properly: " + percentageText;
        assert percentageText.length() > 1 : errorMessage;
        assert percentageText.length() < 10 : errorMessage;
        return percentageText;
    }

    public static String reduceToLength(final String str, final int len, final String prefix)
    {
        int prefixLength = prefix.length();
        if (len <= prefixLength)
        {
            return prefix;
        }
        int length = str.length();
        if (length <= len)
        {
            return str;
        }
        int start = Math.max(length - len + prefixLength, 0);
        //noinspection UnnecessaryLocalVariable : Not inlined because of debugging reasons
        String ret = prefix + str.substring(start, length);
        return ret;
    }

    public static String reduceToLength(final String str, final int len)
    {
        return reduceToLength(str, len, "...");
    }

    @SuppressWarnings("unused")
    public static void appendAll(StringBuilder stringBuilder, @NotNull Collection<String> col)
    {
        for (String str : col)
        {
            stringBuilder.append(str).append(",");
        }
        stringBuilder.delete(stringBuilder.length() - 1, stringBuilder.length());
    }

    public static Project getCurrentlyOpenedProject()
    {
        Optional<Project> first = Arrays.stream(ProjectManager.getInstance().getOpenProjects()).filter(Project::isOpen)
                .findFirst();
        return first.orElse(null);
    }

    public static ImageIcon getDefaultImageIcon()
    {
        ACodeSparksInstanceService service = ServiceManager.getService(ACodeSparksInstanceService.class);
        assert service != null;
        return service.getDefaultPluginImageIcon();
    }

    public static EditorEx getSelectedFileEditor(final Project project)
    {
        if (project == null)
        {
            return null;
        }
        FileEditorManager instance = FileEditorManager.getInstance(project);
        FileEditor selectedEditor = instance.getSelectedEditor();
        return EditorUtil.getEditorEx(selectedEditor);
    }

    public static Color getSelectedFileEditorBackgroundColor()
    {
        Color backgroundColor = null;
        Project currentlyOpenedProject = CoreUtil.getCurrentlyOpenedProject();
        if (currentlyOpenedProject != null)
        {
            EditorEx selectedFileEditor = CoreUtil.getSelectedFileEditor(currentlyOpenedProject);
            if (selectedFileEditor != null)
            {
                backgroundColor = selectedFileEditor.getBackgroundColor();
            }
        }
        if (backgroundColor == null)
        {
            return UIUtil.isUnderDarcula() ? Color.decode("#2b2b2b") : Color.decode("#ffffff");
        }
        return backgroundColor;
    }

//    public static EditorEx getShowingFileEditor(final Project project)
//    {
//        FileEditor[] editors = ApplicationManager.getApplication().runReadAction((Computable<FileEditor[]>) () ->
//                FileEditorManager.getInstance(project).getAllEditors());
//        for (FileEditor editor : editors)
//        {
//            final EditorEx editorEx = EditorUtil.getEditorEx(editor);
//            if (editorEx != null && editorEx.getContentComponent().isShowing())
//            {
//                return editorEx;
//            }
//        }
//        return null;
//    }

    public static void navigate(final String identifier)
    {
        IArtifactPool profilingResult = ArtifactPoolManager.getInstance().getArtifactPool();
        if (profilingResult != null)
        {
            AArtifact artifact = profilingResult.getArtifact(identifier);
            if (artifact != null)
            {
                artifact.navigate();
            }
        }
    }
}
