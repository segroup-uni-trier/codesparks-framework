/*
 * Copyright (C) 2020, Oliver Moseler
 */
package de.unitrier.st.insituprofiling.core.overview;

import de.unitrier.st.insituprofiling.core.AProfilingFlow;
import de.unitrier.st.insituprofiling.core.ProfilingFlowManager;
import de.unitrier.st.insituprofiling.core.data.AProfilingArtifact;
import de.unitrier.st.insituprofiling.core.data.DataUtil;
import de.unitrier.st.insituprofiling.core.data.ThreadArtifact;
import de.unitrier.st.insituprofiling.core.visualization.AArtifactVisualizationLabelFactory;
import de.unitrier.st.insituprofiling.core.visualization.ArtifactVisualizationLabelFactoryCache;
import de.unitrier.st.insituprofiling.core.visualization.DummyArtifactVisualizationLabelFactory;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;
import javax.swing.event.TableModelListener;
import javax.swing.table.TableModel;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Created by Oliver Moseler on 05.10.2014.
 */
public class ProfilingArtifactOverViewTableModel implements TableModel
{
    private final List<AProfilingArtifact> artifacts;

    ProfilingArtifactOverViewTableModel(@NotNull List<AProfilingArtifact> artifacts)
    {
        this.artifacts =
                artifacts.stream()
                        .filter(artifact -> DataUtil.getThreadMetricValueRatio(artifact, ThreadArtifact::getMetricValue) > 0)
                        .collect(Collectors.toList());
        this.artifacts.sort(Comparator.comparingDouble(DataUtil::getThreadFilteredMetricValue).reversed());
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

    AProfilingArtifact getArtifactAt(int rowIndex)
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
        AProfilingArtifact artifact = getArtifactAt(rowIndex);//artifacts.get(rowIndex);
        switch (columnIndex)
        {
            case 0:
                AProfilingFlow profilingFlow = ProfilingFlowManager.getInstance().getCurrentProfilingFlow();

                Class<? extends AArtifactVisualizationLabelFactory> defaultVisualizationLabelFactoryClass =
                        profilingFlow.getDefaultVisualizationLabelFactoryClass();

                if (defaultVisualizationLabelFactoryClass == null)
                {
                    DummyArtifactVisualizationLabelFactory dummyArtifactVisualizationLabelFactory =
                            new DummyArtifactVisualizationLabelFactory();
                    return dummyArtifactVisualizationLabelFactory.createArtifactLabel(artifact);
                }

                //noinspection UnnecessaryLocalVariable : Not inlined because og debugging purposes
                JLabel cachedArtifactVisualizationLabel =
                        ArtifactVisualizationLabelFactoryCache.getInstance()
                                .getCachedArtifactVisualizationLabel(artifact.getIdentifier(),
                                        defaultVisualizationLabelFactoryClass, true);

//                if (cachedArtifactVisualizationLabel == null)
//                {
//                    DummyArtifactVisualizationLabelFactory dummyArtifactVisualizationLabelFactory =
//                            new DummyArtifactVisualizationLabelFactory();
//                    return dummyArtifactVisualizationLabelFactory.createArtifactLabel(artifact);
//                }
                return cachedArtifactVisualizationLabel;
            case 1:
                return artifact.getDisplayString(55);
            default:
                break;
        }
        return null;
    }

    @Override
    public void setValueAt(Object aValue, int rowIndex, int columnIndex) { }

    @Override
    public void addTableModelListener(TableModelListener l) { }

    @Override
    public void removeTableModelListener(TableModelListener l) { }
}
