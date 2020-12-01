package de.unitrier.st.codesparks.core.data;

import org.jgrapht.io.DOTExporter;
import org.jgrapht.io.ExportException;
import org.jgrapht.io.GraphExporter;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

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
        Path path = Paths.get(destinationFilePath);
        FileWriter fileWriter = null;
        try
        {
            if (Files.notExists(path))
            {
                Files.createDirectories(path);
            }
            File file = new File(destinationFilePath);
            if (!file.exists())
            {
                file.createNewFile();
            }
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
