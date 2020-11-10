package de.unitrier.st.codesparks.core.data;

import com.intellij.openapi.project.Project;
import de.unitrier.st.codesparks.core.IArtifactPool;
import de.unitrier.st.codesparks.core.logging.CodeSparksLogger;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public abstract class AArtifactPoolExportStrategy
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

    public final void export(IArtifactPool artifactPool, boolean includeTrie)
    {
        if (!includeTrie)
        {
            export(artifactPool);
        } else
        {
            // TODO: check fileName and test! In Order to do so, the respective threadAnalysisStrategy has to be registered first
            final ArtifactTrieDotExportStrategy profilingArtifactTrieDotExportStrategy = new ArtifactTrieDotExportStrategy(fileName());
            profilingArtifactTrieDotExportStrategy.export(artifactPool.getArtifactTrie());
        }
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
