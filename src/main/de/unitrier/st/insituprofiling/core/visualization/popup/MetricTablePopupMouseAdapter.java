package de.unitrier.st.insituprofiling.core.visualization.popup;

import de.unitrier.st.insituprofiling.core.CoreUtil;
import de.unitrier.st.insituprofiling.core.data.ANeighborProfilingArtifact;

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

        ANeighborProfilingArtifact neighborArtifactAt = ((MetricTableModel) table.getModel()).getNeighborArtifactAt(row, col);
        CoreUtil.navigate(neighborArtifactAt.getIdentifier());

        popupPanel.cancelPopup();
    }
}
