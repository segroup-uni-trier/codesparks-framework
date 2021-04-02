/*
 * Copyright (c), Oliver Moseler, 2021
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

/**
 * Created by Oliver Moseler on 05.10.2014.
 */
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

                final AArtifactVisualizationLabelFactory labelFactory = overview.getArtifactClassVisualizationLabelFactory(artifact.getClass());

                if (labelFactory == null)
                {
//                    final CodeSparksFlowManager codeSparksFlowManager = CodeSparksFlowManager.getInstance();
//                    final ACodeSparksFlow flow = codeSparksFlowManager.getCurrentCodeSparksFlow();
//                    final ADataVisualizer dataVisualizer = flow.getDataVisualizer();
//                    labelFactory = dataVisualizer.getFirstArtifactVisualizationLabelFactory();
//                    if (labelFactory == null)
//                    {
                    DummyArtifactVisualizationLabelFactory dummyArtifactVisualizationLabelFactory =
                            new DummyArtifactVisualizationLabelFactory();
                    return dummyArtifactVisualizationLabelFactory.createArtifactLabel(artifact);
//                    }
                }

                // TODO: enable caching again. A memory dump revealed that the cache had become about 1.35GB. There must be an error in the caching strategy!
                // Not inlined because og debugging purposes
//                JLabel cachedArtifactVisualizationLabel =
//                        ArtifactVisualizationLabelFactoryCache.getInstance()
//                                .getCachedArtifactVisualizationLabel(artifact.getIdentifier(), labelFactory, true);
//
//                return cachedArtifactVisualizationLabel;
                return labelFactory.createArtifactLabel(artifact);
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
