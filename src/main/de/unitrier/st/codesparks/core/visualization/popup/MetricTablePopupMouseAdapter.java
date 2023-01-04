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

import de.unitrier.st.codesparks.core.data.ANeighborArtifact;
import de.unitrier.st.codesparks.core.CoreUtil;

import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

public class MetricTablePopupMouseAdapter extends MouseAdapter
{
    private final MetricTable table;
    private final PopupPanel popupPanel;

    public MetricTablePopupMouseAdapter(PopupPanel popupPanel, MetricTable table)
    {
        this.popupPanel = popupPanel;
        this.table = table;
    }

    @Override
    public void mouseClicked(MouseEvent e)
    {
        Point point = e.getPoint();
        int col = table.columnAtPoint(point);
        int row = table.rowAtPoint(point);

        ANeighborArtifact neighborArtifactAt = ((MetricTableModel) table.getModel()).getNeighborArtifactAt(row, col);
        CoreUtil.navigate(neighborArtifactAt.getIdentifier());

        popupPanel.cancelPopup();
    }
}
