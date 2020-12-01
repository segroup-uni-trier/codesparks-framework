package de.unitrier.st.codesparks.core.visualization.popup;

import java.awt.*;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionAdapter;

/*
 * Copyright (c), Oliver Moseler, 2020
 */
public class MetricTableMouseMotionAdapter extends MouseMotionAdapter
{
    private final MetricTable table;

    public MetricTableMouseMotionAdapter(MetricTable table)
    {
        this.table = table;
    }

    @Override
    public void mouseMoved(MouseEvent e)
    {
        Point point = e.getPoint();
        int col = table.columnAtPoint(point);
        int row = table.rowAtPoint(point);
        table.setEnteredCol(col);
        table.setEnteredRow(row);
        table.updateUI();
    }
}
