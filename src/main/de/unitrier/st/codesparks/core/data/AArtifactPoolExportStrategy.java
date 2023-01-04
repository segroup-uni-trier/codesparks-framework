/*
 *  Copyright 2023 Oliver Moseler
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */
package de.unitrier.st.codesparks.core.data;

import com.intellij.openapi.project.Project;
import de.unitrier.st.codesparks.core.logging.CodeSparksLogger;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public abstract class AArtifactPoolExportStrategy implements IArtifactPoolExportStrategy
{
    private final Project project;

    public AArtifactPoolExportStrategy(Project project)
    {
        this.project = project;
    }

    private void export(String fileName, String content)
    {
        String exportPath = getExportPath();
        File file = new File(exportPath.concat(File.separator).concat(fileName));
        BufferedWriter bw = null;
        try
        {
            bw = new BufferedWriter(new FileWriter(file));
            bw.write(content);
            bw.close();
            CodeSparksLogger.addText(String.format("Export to '%s' successful.", file.getName()));
        } catch (IOException e)
        {
            e.printStackTrace();
            CodeSparksLogger.addText(String.format("Export to '%s' failed.", file.getName()));
        } finally
        {
            if (bw != null)
            {
                try
                {
                    bw.flush();
                    bw.close();
                } catch (IOException e)
                {
                    // ignored
                }
            }
        }
    }

    public final void export(IArtifactPool artifactPool)
    {
        export(fileName(), format(artifactPool));
    }

    public abstract String format(IArtifactPool artifactPool);

    public abstract String format(AArtifact artifact);

    public abstract String fileName();

    private String getExportPath()
    {
        String exportFilePath = project.getBasePath();
        if (exportFilePath == null)
        { // Fallback
            exportFilePath =
                    System.getProperty("user.home").concat(File.separator).concat(".codesparks").concat(File.separator).concat(project.getName());
        }
        try
        {
            Path path = Paths.get(exportFilePath);
            if (!Files.exists(path))
            {
                Files.createDirectories(path);
            }
        } catch (IOException e)
        {
            e.printStackTrace();
        }
        return exportFilePath;
    }
}
