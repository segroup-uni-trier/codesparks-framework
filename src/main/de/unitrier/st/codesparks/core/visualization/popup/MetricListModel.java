package de.unitrier.st.codesparks.core.visualization.popup;

import com.intellij.ui.components.JBTextArea;
import de.unitrier.st.codesparks.core.data.ANeighborProfilingArtifact;
import de.unitrier.st.codesparks.core.data.AProfilingArtifact;
import de.unitrier.st.codesparks.core.data.DataUtil;
import de.unitrier.st.codesparks.core.data.NeighborProfilingArtifactComparator;

import javax.swing.*;
import javax.swing.event.ListDataListener;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;

public class MetricListModel extends DefaultListModel<JBTextArea>
{
    private final List<ANeighborProfilingArtifact> neighborProfilingArtifacts;
    private final List<JBTextArea> textAreas;
    private static Font defaultFont;

    private List<ANeighborProfilingArtifact> prepareNeighborMetricValues(AProfilingArtifact artifact, List<ANeighborProfilingArtifact> list)
    {
        double threadFilteredMetricValue = DataUtil.getThreadFilteredMetricValue(artifact);
        for (ANeighborProfilingArtifact aNeighborProfilingArtifact : list)
        {
            aNeighborProfilingArtifact.setMetricValue(DataUtil.getThreadFilteredMetricValue(aNeighborProfilingArtifact));
            aNeighborProfilingArtifact.setRelativeMetricValue(threadFilteredMetricValue);
        }
        return list;
    }

    public MetricListModel(final AProfilingArtifact artifact, final List<ANeighborProfilingArtifact> neighborProfilingArtifacts)
    {
        this.neighborProfilingArtifacts = prepareNeighborMetricValues(artifact, neighborProfilingArtifacts);
        this.neighborProfilingArtifacts.sort(new NeighborProfilingArtifactComparator());
        textAreas = new ArrayList<>(this.neighborProfilingArtifacts.size());
        for (int i = 0; i < this.neighborProfilingArtifacts.size(); i++)
        {
            textAreas.add(new JBTextArea(neighborProfilingArtifacts.get(i).getDisplayString(60)));
        }
        defaultFont = new JBTextArea().getFont();
    }

    @Override
    public int getSize()
    {
        return neighborProfilingArtifacts.size();
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

    ANeighborProfilingArtifact getArtifactAt(int index)
    {
        if (index > -1 && index < neighborProfilingArtifacts.size())
        {
            return neighborProfilingArtifacts.get(index);
        }
        return null;
    }
}
