package de.unitrier.st.codesparks.core.visualization.popup;

import com.intellij.ui.components.JBTextArea;
import de.unitrier.st.codesparks.core.data.*;

import javax.swing.*;
import javax.swing.event.ListDataListener;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;

/*
 * Copyright (c), Oliver Moseler, 2020
 */
public class NumericalMetricListModel extends DefaultListModel<JBTextArea>
{
    private final List<ANeighborArtifact> neighborArtifacts;
    private final List<JBTextArea> textAreas;
    private static Font defaultFont;

    public NumericalMetricListModel(
            final AArtifact artifact
            , final IMetricIdentifier numericalMetricIdentifier
            , final List<ANeighborArtifact> neighborArtifacts)
    {
        this.neighborArtifacts = neighborArtifacts;
        this.neighborArtifacts.sort(new NeighborArtifactComparator(numericalMetricIdentifier));
        textAreas = new ArrayList<>(this.neighborArtifacts.size());
        for (int i = 0; i < this.neighborArtifacts.size(); i++)
        {
            textAreas.add(new JBTextArea(neighborArtifacts.get(i).getDisplayStringRelativeTo(artifact, numericalMetricIdentifier, 60)));
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
        return textAreas.get(index);
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
