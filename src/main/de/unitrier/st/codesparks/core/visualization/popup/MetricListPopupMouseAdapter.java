/*
 * Copyright (c) 2021. Oliver Moseler
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
            UserActivityLogger.getInstance().log(UserActivityEnum.PopupNavigated, popupPanel.getType(), neighborArtifactAtIdentifier);
            CoreUtil.navigate(neighborArtifactAtIdentifier);
        }
        popupPanel.cancelPopup();
    }
}
