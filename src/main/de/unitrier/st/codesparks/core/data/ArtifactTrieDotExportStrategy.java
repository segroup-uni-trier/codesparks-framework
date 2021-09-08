/*
 * Copyright (c) 2021. Oliver Moseler
 */
package de.unitrier.st.codesparks.core.data;

import org.jgrapht.io.DOTExporter;
import org.jgrapht.io.ExportException;
import org.jgrapht.io.GraphExporter;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

public class ArtifactTrieDotExportStrategy implements IArtifactTrieExportStrategy
{
    private final String destinationFilePath;

    public ArtifactTrieDotExportStrategy(String destinationFilePath)
    {
        this.destinationFilePath = destinationFilePath;
    }

    @Override
    public void export(ArtifactTrie artifactTrie)
    {
        GraphExporter<ArtifactTrieNode, ArtifactTrieEdge> exporter = new DOTExporter<>(
                ArtifactTrieNode::getIdString
                , ArtifactTrieNode::getMetricLabel
                , ArtifactTrieEdge::getLabel
        );
        FileWriter fileWriter = null;
        try
        {
            File file = new File(destinationFilePath);
            if (!file.exists())
            {
                //noinspection ResultOfMethodCallIgnored
                file.createNewFile();
            }
            fileWriter = new FileWriter(file);
            exporter.exportGraph(artifactTrie, fileWriter);
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
