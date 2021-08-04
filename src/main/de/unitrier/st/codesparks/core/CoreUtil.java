/*
 * Copyright (c) 2021. Oliver Moseler
 */
package de.unitrier.st.codesparks.core;

import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.editor.ex.EditorEx;
import com.intellij.openapi.editor.ex.util.EditorUtil;
import com.intellij.openapi.fileEditor.FileEditor;
import com.intellij.openapi.fileEditor.FileEditorManager;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.project.ProjectManager;
import de.unitrier.st.codesparks.core.data.IPsiNavigable;
import de.unitrier.st.codesparks.core.service.ACodeSparksInstanceService;

import javax.swing.*;
import java.text.DecimalFormat;
import java.util.Arrays;
import java.util.Collection;
import java.util.Optional;

public final class CoreUtil
{
    private CoreUtil() {}

    private static final DecimalFormat df = new DecimalFormat("0.00");

    public static String formatPercentage(final double value)
    {
        return formatPercentage(value, false);
    }

    public static String formatPercentage(final double value, final boolean treatZeroAsIs)
    {
        String percentageText;
        if (Double.isNaN(value) || value < 0.0001)
        {
            if (treatZeroAsIs)
            {
                percentageText = "0.00%";
            } else
            {
                percentageText = "<0.01%";
            }
        } else
        {
            synchronized (df)
            {
                percentageText = df.format(value * 100) + "%";
            }
        }
        final String errorMessage = "did not format " + value + " properly: " + percentageText;
        assert percentageText.length() > 1 : errorMessage;
        assert percentageText.length() < 10 : errorMessage;
        return percentageText;
    }

    public static String formatPercentageWithLeadingWhitespace(final double value)
    {
        return formatPercentageWithLeadingWhitespace(value, false);
    }

    public static String formatPercentageWithLeadingWhitespace(final double value, final boolean treatZeroAsIs)
    {
        String percentageText;
        if (value < 0.0001)
        {
            if (treatZeroAsIs)
            {
                percentageText = "0.00%";
            } else
            {
                percentageText = "<0.01%";
            }
        } else
        {
            synchronized (df)
            {
                percentageText = df.format(value * 100) + "%";
            }
        }
        final StringBuilder strb = new StringBuilder();
        final int length = percentageText.length();
        strb.append(" ".repeat(Math.max(0, 6 - length)));
        strb.append(percentageText);
        percentageText = strb.toString();
        final String errorMessage = "did not format " + value + " properly: " + percentageText;
        assert percentageText.length() > 1 : errorMessage;
        assert percentageText.length() < 10 : errorMessage;
        return percentageText;
    }

    public static String reduceToLength(final String str, final int len, final String prefix)
    {
        final int prefixLength = prefix.length();
        if (len <= prefixLength)
        {
            return prefix;
        }
        final int length = str.length();
        if (length <= len)
        {
            return str;
        }
        final int start = Math.max(length - len + prefixLength, 0);
        //noinspection UnnecessaryLocalVariable : Not inlined because of debugging reasons
        final String ret = prefix + str.substring(start, length);
        return ret;
    }

    public static String reduceToLength(final String str, final int len)
    {
        return reduceToLength(str, len, "...");
    }

    @SuppressWarnings("unused")
    public static void appendAll(final StringBuilder stringBuilder, final Collection<String> col)
    {
        for (final String str : col)
        {
            stringBuilder.append(str).append(",");
        }
        stringBuilder.delete(stringBuilder.length() - 1, stringBuilder.length());
    }

    public static Project getCurrentlyOpenedProject()
    {
        final Optional<Project> first = Arrays.stream(ProjectManager.getInstance().getOpenProjects()).filter(Project::isOpen)
                .findFirst();
        return first.orElse(null);
    }

    public static ImageIcon getDefaultImageIcon()
    {
//        final ACodeSparksInstanceService service = ServiceManager.getService(ACodeSparksInstanceService.class);
        final ACodeSparksInstanceService service = ApplicationManager.getApplication().getService(ACodeSparksInstanceService.class);
        assert service != null;
        return service.getDefaultPluginImageIcon();
    }

    public static EditorEx getSelectedFileEditor(final Project project)
    {
        if (project == null)
        {
            return null;
        }
        final FileEditorManager instance = FileEditorManager.getInstance(project);
        final FileEditor selectedEditor = instance.getSelectedEditor();
        return EditorUtil.getEditorEx(selectedEditor);
    }

    public static void navigate(final String identifier)
    {
        final IArtifactPool artifactPool = ArtifactPoolManager.getInstance().getArtifactPool();
        if (artifactPool != null)
        {
            final IPsiNavigable artifact = artifactPool.getArtifact(identifier);
            if (artifact != null)
            {
                artifact.navigate();
            }
        }
    }
}
