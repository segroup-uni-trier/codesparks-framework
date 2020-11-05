package de.unitrier.st.insituprofiling.core.visualization.popup;

import de.unitrier.st.insituprofiling.core.CoreUtil;
import de.unitrier.st.insituprofiling.core.data.ANeighborProfilingArtifact;
import de.unitrier.st.insituprofiling.core.logging.UserActivityEnum;
import de.unitrier.st.insituprofiling.core.logging.UserActivityLogger;

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

        ANeighborProfilingArtifact neighborArtifactAt = ((MetricListModel) list.getModel()).getArtifactAt(index);
        if (neighborArtifactAt != null)
        {
            String neighborArtifactAtIdentifier = neighborArtifactAt.getIdentifier();
            UserActivityLogger.getInstance().log(UserActivityEnum.PopupNavigated, popupPanel.getType(), neighborArtifactAtIdentifier);
            CoreUtil.navigate(neighborArtifactAtIdentifier);
        }
        popupPanel.cancelPopup();
    }
}
