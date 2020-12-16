package de.unitrier.st.codesparks.core.overview;

import de.unitrier.st.codesparks.core.CoreUtil;
import de.unitrier.st.codesparks.core.data.AArtifact;
import de.unitrier.st.codesparks.core.data.DataUtil;
import de.unitrier.st.codesparks.core.data.IMetricIdentifier;
import de.unitrier.st.codesparks.core.logging.CodeSparksLogger;
import de.unitrier.st.codesparks.core.visualization.AArtifactVisualizationLabelFactory;
import de.unitrier.st.codesparks.core.visualization.ArtifactVisualizationLabelFactoryCache;
import de.unitrier.st.codesparks.core.visualization.DummyArtifactVisualizationLabelFactory;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;
import javax.swing.event.TableModelListener;
import javax.swing.table.TableModel;
import java.util.Comparator;
import java.util.List;
import java.util.function.ToDoubleFunction;
import java.util.stream.Collectors;

/**
 * Created by Oliver Moseler on 05.10.2014.
 */
/*
 * Copyright (c), Oliver Moseler, 2020
 */
public class ArtifactOverViewTableModel implements TableModel
{
    private final List<AArtifact> artifacts;

    ArtifactOverViewTableModel(@NotNull final List<AArtifact> artifacts, final IMetricIdentifier metricIdentifier)
    {
        if (metricIdentifier == null)
        {
            CodeSparksLogger.addText("%s: Metric identifier not setup! Please register a metric identifier to the overview through the CodeSparksFlow.",
                    getClass());
        }
        this.artifacts =
                artifacts.stream()
                        .filter(artifact ->
                        {
                            if (artifact.hasThreads())
                            {
                                return DataUtil.getThreadMetricValueRatio(artifact,
                                        (codeSparksThread) -> codeSparksThread.getNumericalMetricValue(metricIdentifier)) > 0;
                            } else
                            {
                                return true;//artifact.getMetricValue() > 0;
                            }
                        })
                        .collect(Collectors.toList());
//        this.artifacts.sort(Comparator.comparingDouble(DataUtil::getThreadFilteredMetricValue).reversed());
        final ToDoubleFunction<? super AArtifact> f = value -> {
            if (value != null)
            {
                return DataUtil.getThreadFilteredMetricValue(value, metricIdentifier);
            } else
            {
                return 0d;
            }
        };
        this.artifacts.sort(Comparator.comparingDouble(f).reversed());

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
                    DummyArtifactVisualizationLabelFactory dummyArtifactVisualizationLabelFactory =
                            new DummyArtifactVisualizationLabelFactory();
                    return dummyArtifactVisualizationLabelFactory.createArtifactLabel(artifact);
                }

                //noinspection UnnecessaryLocalVariable : Not inlined because og debugging purposes
                JLabel cachedArtifactVisualizationLabel =
                        ArtifactVisualizationLabelFactoryCache.getInstance()
                                .getCachedArtifactVisualizationLabel(artifact.getIdentifier(), labelFactory, true);

                return cachedArtifactVisualizationLabel;
            case 1:
                return CoreUtil.reduceToLength(artifact.getIdentifier(), 55, "...");
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
