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
package de.unitrier.st.codesparks.core.visualization.popup;

import de.unitrier.st.codesparks.core.data.*;
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

    private final AMetricIdentifier metricIdentifier;
    private final AArtifact artifact;

    public MetricTableModel(@NotNull final AArtifact artifact, final AMetricIdentifier metricIdentifier)
    {
        this.artifact = artifact;
        this.metricIdentifier = metricIdentifier;
        NeighborArtifactComparator comparator = new NeighborArtifactComparator(metricIdentifier);
        predecessors = artifact.getPredecessorsList()
                .stream()
                .filter(npa -> npa.getThreadArtifacts()
                        .stream()
                        .anyMatch(AThreadArtifact::isNotFiltered))
                .collect(Collectors.toList());
        predecessors.sort(comparator);
        successors = artifact.getSuccessorsList()
                .stream()
                .filter(npa -> !npa.getShortName().toLowerCase().startsWith("self"))
                .filter(npa -> npa.getThreadArtifacts()
                        .stream()
                        .anyMatch(AThreadArtifact::isNotFiltered))
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
                return "Predecessors";
            case 1:
                return "Successors";
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
