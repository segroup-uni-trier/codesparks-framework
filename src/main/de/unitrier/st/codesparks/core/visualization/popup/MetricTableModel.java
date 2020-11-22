package de.unitrier.st.codesparks.core.visualization.popup;

import de.unitrier.st.codesparks.core.data.ANeighborArtifact;
import de.unitrier.st.codesparks.core.data.AArtifact;
import de.unitrier.st.codesparks.core.data.NeighborArtifactComparator;
import org.jetbrains.annotations.NotNull;

import javax.swing.table.DefaultTableModel;
import java.util.List;
import java.util.stream.Collectors;

public class MetricTableModel extends DefaultTableModel
{
    private final List<ANeighborArtifact> successors;
    private final List<ANeighborArtifact> predecessors;
    private final int size;
    private final int preSize;
    private final int sucSize;

    private List<ANeighborArtifact> prepareNeighbors(
            final AArtifact artifact
            , final List<ANeighborArtifact> list
    )
    {
        for (ANeighborArtifact aNeighborProfilingArtifact : list)
        {
            final double numericalMetricValue = artifact.getNumericalMetricValue(metricIdentifier);
            aNeighborProfilingArtifact.setRelativeMetricValue(metricIdentifier, numericalMetricValue);
        }
        return list;
    }

    private final String metricIdentifier;

    public MetricTableModel(@NotNull AArtifact artifact, final String metricIdentifier)
    {
        this.metricIdentifier = metricIdentifier;
        NeighborArtifactComparator comparator = new NeighborArtifactComparator(metricIdentifier);
        predecessors = prepareNeighbors(artifact, artifact.getPredecessorsList())
                .stream()
                .filter(npa -> npa.getThreadArtifacts()
                        .stream()
                        .anyMatch(threadArtifact -> !threadArtifact.isFiltered()))
                .collect(Collectors.toList());
        predecessors.sort(comparator);
        successors = prepareNeighbors(artifact, artifact.getSuccessorsList())
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
        return neighborArtifactAt == null ? "" : neighborArtifactAt.getDisplayString(metricIdentifier, 34);
    }
}
