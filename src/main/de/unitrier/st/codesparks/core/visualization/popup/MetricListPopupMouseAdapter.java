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
import de.unitrier.st.codesparks.core.logging.UserActivityEnum;
import de.unitrier.st.codesparks.core.logging.UserActivityLogger;
import de.unitrier.st.codesparks.core.CoreUtil;

import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

public class MetricListPopupMouseAdapter extends MouseAdapter
{
    private final MetricList list;
    private final PopupPanel popupPanel;

    public MetricListPopupMouseAdapter(PopupPanel popupPanel, MetricList list)
    {
        this.popupPanel = popupPanel;
        this.list = list;
    }

    @Override
    public void mouseClicked(MouseEvent e)
    {
        Point point = e.getPoint();

        int index = list.locationToIndex(point);

        ANeighborArtifact neighborArtifactAt = ((NumericalMetricListModel) list.getModel()).getArtifactAt(index);
        if (neighborArtifactAt != null)
        {
            String neighborArtifactAtIdentifier = neighborArtifactAt.getIdentifier();
            UserActivityLogger.getInstance().log(UserActivityEnum.PopupNavigated, popupPanel.getDescription(), neighborArtifactAtIdentifier);
            CoreUtil.navigate(neighborArtifactAtIdentifier);
        }
        popupPanel.cancelPopup();
    }
}
