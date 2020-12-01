package de.unitrier.st.codesparks.core.data;

import org.jgrapht.io.DOTExporter;
import org.jgrapht.io.ExportException;
import org.jgrapht.io.GraphExporter;

import java.io.FileWriter;
import java.io.IOException;

/*
 * Copyright (c), Oliver Moseler, 2020
 */
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
        GraphExporter<ArtifactTrieNode, ArtifactTrieEdge> exporter = new DOTExporter<>(ArtifactTrieNode::getIdentifier,
                ArtifactTrieNode::getLabel, ArtifactTrieEdge::getLabel);
        FileWriter fileWriter = null;
        try
        {
            fileWriter = new FileWriter(destinationFilePath);
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
