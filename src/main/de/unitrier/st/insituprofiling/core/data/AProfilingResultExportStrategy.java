package de.unitrier.st.insituprofiling.core.data;

import com.intellij.openapi.project.Project;
import de.unitrier.st.insituprofiling.core.IProfilingResult;
import de.unitrier.st.insituprofiling.core.logging.ProfilingLogger;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public abstract class AProfilingResultExportStrategy
{
    private final Project project;

    public AProfilingResultExportStrategy(Project project)
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
            ProfilingLogger.addText(String.format("Export to '%s' successful.", file.getName()));
        } catch (IOException e)
        {
            e.printStackTrace();
            ProfilingLogger.addText(String.format("Export to '%s' failed.", file.getName()));
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

    public final void export(IProfilingResult result)
    {
        export(fileName(), format(result));
    }

    public final void export(IProfilingResult result, boolean includeTrie)
    {
        if (!includeTrie)
        {
            export(result);
        } else
        {
            // TODO: check fileName and test! In Order to do so, the respective threadAnalysisStrategy has to be registered first
            final ProfilingArtifactTrieDotExportStrategy profilingArtifactTrieDotExportStrategy = new ProfilingArtifactTrieDotExportStrategy(fileName());
            profilingArtifactTrieDotExportStrategy.export(result.getProfilingArtifactTrie());
        }
    }

    public abstract String format(IProfilingResult result);

    public abstract String format(AProfilingArtifact profilingArtifact);

    public abstract String fileName();

    private String getExportPath()
    {
        String exportFilePath = project.getBasePath();
        if (exportFilePath == null)
        { // Fallback
            exportFilePath =
                    System.getProperty("user.home").concat(File.separator).concat(".insituprofiling").concat(File.separator).concat(project.getName());
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
