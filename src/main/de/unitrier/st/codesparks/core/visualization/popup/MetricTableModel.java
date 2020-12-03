package de.unitrier.st.codesparks.core.visualization.popup;

import de.unitrier.st.codesparks.core.data.ANeighborArtifact;
import de.unitrier.st.codesparks.core.data.ACodeSparksArtifact;
import de.unitrier.st.codesparks.core.data.IMetricIdentifier;
import de.unitrier.st.codesparks.core.data.NeighborArtifactComparator;
import org.jetbrains.annotations.NotNull;

import javax.swing.table.DefaultTableModel;
import java.util.List;
import java.util.stream.Collectors;

/*
 * Copyright (c), Oliver Moseler, 2020
 */
public class MetricTableModel extends DefaultTableModel
{
    private final List<ANeighborArtifact> successors;
    private final List<ANeighborArtifact> predecessors;
    private final int size;
    private final int preSize;
    private final int sucSize;

    private final IMetricIdentifier metricIdentifier;
    private final ACodeSparksArtifact artifact;

    public MetricTableModel(@NotNull final ACodeSparksArtifact artifact, final IMetricIdentifier metricIdentifier)
    {
        this.artifact = artifact;
        this.metricIdentifier = metricIdentifier;
        NeighborArtifactComparator comparator = new NeighborArtifactComparator(metricIdentifier);
        predecessors = artifact.getPredecessorsList()
                .stream()
                .filter(npa -> npa.getThreadArtifacts()
                        .stream()
                        .anyMatch(threadArtifact -> !threadArtifact.isFiltered()))
                .collect(Collectors.toList());
        predecessors.sort(comparator);
        successors = artifact.getSuccessorsList()
                .stream()
                .filter(npa -> !npa.getName().toLowerCase().startsWith("self"))
                .filter(npa -> npa.getThreadArtifacts()
                        .stream()
                        .anyMatch(threadArtifact -> !threadArtifact.isFiltered()))
                .collect(Collectors.toList());
        successors.sort(comparator);
        preSize = predecessors.size();
        sucSize = successors.size();
        size = Math.max(preSize, sucSize);
    }

    @Override
    public int getRowCount()
    {
        return size;
    }

    @Override
    public int getColumnCount()
    {
        return 2;
    }

    @Override
    public String getColumnName(int columnIndex)
    {
        switch (columnIndex)
        {
            case 0:
                return "Callers";
            case 1:
                return "Callees";
            default:
                return "";
        }
    }

    @Override
    public Class<?> getColumnClass(int columnIndex)
    {
        return String.class;
    }

    @Override
    public boolean isCellEditable(int rowIndex, int columnIndex)
    {
        return false;
    }

    public ANeighborArtifact getNeighborArtifactAt(int rowIndex, int columnIndex)
    {
        if (rowIndex < 0)
        {
            return null;
        }
        switch (columnIndex)
        {
            case 0:
                if (rowIndex < preSize)
                    return predecessors.get(rowIndex);
                return null;
            case 1:
                if (rowIndex < sucSize)
                    return successors.get(rowIndex);
                return null;
            default:
                return null;
        }
    }

    @Override
    public Object getValueAt(int rowIndex, int columnIndex)
    {
        ANeighborArtifact neighborArtifactAt = getNeighborArtifactAt(rowIndex, columnIndex);
        return neighborArtifactAt == null ? "" : neighborArtifactAt.getDisplayStringRelativeTo(artifact, metricIdentifier, 34);
    }
}
