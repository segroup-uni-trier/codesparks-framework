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

import com.intellij.ui.components.JBList;
import com.intellij.ui.components.JBTextArea;
import de.unitrier.st.codesparks.core.data.ANeighborArtifact;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

public class MetricList extends JBList<JBTextArea>
{
    public MetricList(ListModel<JBTextArea> model)
    {
        super(model);
        addMouseListener(new MouseAdapter()
        {
            @Override
            public void mouseExited(MouseEvent e)
            {
                ((NumericalMetricListModel) getModel()).resetFont();
                updateUI();
            }
        });
    }

    private ANeighborArtifact getArtifactAt(int index)
    {
        return ((NumericalMetricListModel) getModel()).getArtifactAt(index);
    }

    @Override
    public String getToolTipText(MouseEvent e)
    {
        Point p = e.getPoint();
        int index = locationToIndex(p);
        ANeighborArtifact neighborArtifactAt = getArtifactAt(index);
        if (neighborArtifactAt == null)
        {
            return "";
        }
        String identifier = neighborArtifactAt.getIdentifier();
        identifier = identifier.replaceAll("<", "&lt;").replaceAll(">", "&gt;");
        return identifier;
    }
}
