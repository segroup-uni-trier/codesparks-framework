package de.unitrier.st.codesparks.core.data;

import org.jgrapht.io.DOTExporter;
import org.jgrapht.io.ExportException;
import org.jgrapht.io.GraphExporter;

import java.io.FileWriter;
import java.io.IOException;

public class ProfilingArtifactTrieDotExportStrategy implements IProfilingArtifactTrieExportStrategy
{
    private final String destinationFilePath;

    public ProfilingArtifactTrieDotExportStrategy(String destinationFilePath)
    {
        this.destinationFilePath = destinationFilePath;
    }

    @Override
    public void export(ProfilingArtifactTrie profilingArtifactTrie)
    {
        GraphExporter<ProfilingArtifactTrieNode, ProfilingArtifactTrieEdge> exporter = new DOTExporter<>(ProfilingArtifactTrieNode::getIdentifier,
                ProfilingArtifactTrieNode::getLabel, ProfilingArtifactTrieEdge::getLabel);
        FileWriter fileWriter = null;
        try
        {
            fileWriter = new FileWriter(destinationFilePath);
            exporter.exportGraph(profilingArtifactTrie, fileWriter);
        } catch (IOException | ExportException e)
        {
            e.printStackTrace();
        } finally
        {
            if (fileWriter != null)
            {
                try
                {
                    fileWriter.close();
                } catch (IOException e)
                {
                    e.printStackTrace();
                }
            }
        }
    }
}
