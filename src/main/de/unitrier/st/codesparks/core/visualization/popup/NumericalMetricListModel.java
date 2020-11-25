package de.unitrier.st.codesparks.core.visualization.popup;

import com.intellij.ui.components.JBTextArea;
import de.unitrier.st.codesparks.core.data.*;

import javax.swing.*;
import javax.swing.event.ListDataListener;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;

public class NumericalMetricListModel extends DefaultListModel<JBTextArea>
{
    private final List<ANeighborArtifact> neighborArtifacts;
    private final List<JBTextArea> textAreas;
    private static Font defaultFont;

    private List<ANeighborArtifact> prepareNeighborMetricValues(
            final AArtifact artifact
            , final IMetricIdentifier metricIdentifier
            , final List<ANeighborArtifact> list)
    {
        double threadFilteredMetricValue = DataUtil.getThreadFilteredMetricValue(artifact, metricIdentifier);
        for (ANeighborArtifact aNeighborProfilingArtifact : list)
        {
//            aNeighborProfilingArtifact.setMetricValue(DataUtil.getThreadFilteredMetricValue(aNeighborProfilingArtifact));
            aNeighborProfilingArtifact.setMetricValue(metricIdentifier, DataUtil.getThreadFilteredMetricValue(aNeighborProfilingArtifact,
                    metricIdentifier));
            aNeighborProfilingArtifact.setRelativeMetricValue(metricIdentifier, threadFilteredMetricValue);
        }
        return list;
    }

    public NumericalMetricListModel(
            final AArtifact artifact
            , final IMetricIdentifier numericalMetricIdentifier
            , final List<ANeighborArtifact> neighborArtifacts)
    {
        this.neighborArtifacts = prepareNeighborMetricValues(artifact, numericalMetricIdentifier, neighborArtifacts);
        this.neighborArtifacts.sort(new NeighborArtifactComparator(numericalMetricIdentifier));
        textAreas = new ArrayList<>(this.neighborArtifacts.size());
        for (int i = 0; i < this.neighborArtifacts.size(); i++)
        {
            textAreas.add(new JBTextArea(neighborArtifacts.get(i).getDisplayString(numericalMetricIdentifier, 60)));
        }
        defaultFont = new JBTextArea().getFont();
    }

    @Override
    public int getSize()
    {
        return neighborArtifacts.size();
    }

    @Override
    public JBTextArea getElementAt(int index)
    {
        if (index < 0 || index > textAreas.size() - 1)
        {
            return new JBTextArea("");
        }
        return textAreas.get(index);//neighborProfilingArtifacts.get(index).getDisplayString(68);
    }

    @Override
    public void addListDataListener(ListDataListener l) { }

    @Override
    public void removeListDataListener(ListDataListener l) { }

    public void resetFont()
    {
        synchronized (this)
        {
            for (JBTextArea textArea : textAreas)
            {
                textArea.setFont(defaultFont);
            }
        }
    }

    ANeighborArtifact getArtifactAt(int index)
    {
        if (index > -1 && index < neighborArtifacts.size())
        {
            return neighborArtifacts.get(index);
        }
        return null;
    }
}
