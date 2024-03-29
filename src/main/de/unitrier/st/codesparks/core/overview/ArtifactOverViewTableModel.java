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
package de.unitrier.st.codesparks.core.overview;

import de.unitrier.st.codesparks.core.CoreUtil;
import de.unitrier.st.codesparks.core.data.AArtifact;
import de.unitrier.st.codesparks.core.visualization.AArtifactVisualizationLabelFactory;
import de.unitrier.st.codesparks.core.visualization.DummyArtifactVisualizationLabelFactory;

import javax.swing.*;
import javax.swing.event.TableModelListener;
import javax.swing.table.TableModel;
import java.util.Comparator;
import java.util.List;

public class ArtifactOverViewTableModel implements TableModel
{
    private final List<AArtifact> artifacts;

    @SuppressWarnings("unused")
    ArtifactOverViewTableModel(final List<AArtifact> artifacts, final Comparator<AArtifact> comparator)
    {
        this.artifacts = artifacts;
        if (comparator != null)
        {
            this.artifacts.sort(comparator);
        }
    }

    ArtifactOverViewTableModel(final List<AArtifact> artifacts)
    {
        this.artifacts = artifacts;
    }

    public void sortArtifacts(final Comparator<AArtifact> comparator)
    {
        if (comparator != null)
        {
            this.artifacts.sort(comparator);
        }
    }

    @Override
    public int getRowCount()
    {
        return artifacts.size();
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
                return "";
            case 1:
                return "Artifact";
            default:
                break;
        }
        return "";
    }

    @Override
    public Class<?> getColumnClass(int columnIndex)
    {
        if (columnIndex == 0)
        {
            return JLabel.class;
        }
        return String.class;
    }

    @Override
    public boolean isCellEditable(int rowIndex, int columnIndex)
    {
        return false;
    }

    AArtifact getArtifactAt(int rowIndex)
    {
        if (artifacts == null || rowIndex < 0 || rowIndex > artifacts.size() - 1)
        {
            return null;
        }
        return artifacts.get(rowIndex);
    }

    @Override
    public Object getValueAt(int rowIndex, int columnIndex)
    {
        AArtifact artifact = getArtifactAt(rowIndex);
        switch (columnIndex)
        {
            case 0:
                final ArtifactOverview overview = ArtifactOverview.getInstance();
                AArtifactVisualizationLabelFactory labelFactory = overview.getArtifactClassVisualizationLabelFactory(artifact.getClass());
                if (labelFactory == null)
                {
                    labelFactory = new DummyArtifactVisualizationLabelFactory();
                }
                return labelFactory.createArtifactLabel(artifact);
            case 1:
                return CoreUtil.reduceToLength(artifact.getName(), 55, "...");
            default:
                break;
        }
        return null;
    }

    @Override
    public void setValueAt(Object aValue, int rowIndex, int columnIndex) {}

    @Override
    public void addTableModelListener(TableModelListener l) {}

    @Override
    public void removeTableModelListener(TableModelListener l) {}
}
