package de.unitrier.st.codesparks.core.visualization.popup;

import com.intellij.ui.components.JBTextArea;
import de.unitrier.st.codesparks.core.data.ANeighborArtifact;
import de.unitrier.st.codesparks.core.data.AArtifact;
import de.unitrier.st.codesparks.core.data.DataUtil;
import de.unitrier.st.codesparks.core.data.NeighborArtifactComparator;

import javax.swing.*;
import javax.swing.event.ListDataListener;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;

public class MetricListModel extends DefaultListModel<JBTextArea>
{
    private final List<ANeighborArtifact> neighborProfilingArtifacts;
    private final List<JBTextArea> textAreas;
    private static Font defaultFont;

    private List<ANeighborArtifact> prepareNeighborMetricValues(AArtifact artifact, List<ANeighborArtifact> list)
    {
        double threadFilteredMetricValue = DataUtil.getThreadFilteredMetricValue(artifact);
        for (ANeighborArtifact aNeighborProfilingArtifact : list)
        {
            aNeighborProfilingArtifact.setMetricValue(DataUtil.getThreadFilteredMetricValue(aNeighborProfilingArtifact));
            aNeighborProfilingArtifact.setRelativeMetricValue(threadFilteredMetricValue);
        }
        return list;
    }

    public MetricListModel(final AArtifact artifact, final List<ANeighborArtifact> neighborProfilingArtifacts)
    {
        this.neighborProfilingArtifacts = prepareNeighborMetricValues(artifact, neighborProfilingArtifacts);
        this.neighborProfilingArtifacts.sort(new NeighborArtifactComparator());
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

    ANeighborArtifact getArtifactAt(int index)
    {
        if (index > -1 && index < neighborProfilingArtifacts.size())
        {
            return neighborProfilingArtifacts.get(index);
        }
        return null;
    }
}
