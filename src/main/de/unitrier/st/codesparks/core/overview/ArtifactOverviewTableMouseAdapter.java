package de.unitrier.st.codesparks.core.overview;

import de.unitrier.st.codesparks.core.data.ASourceCodeArtifact;
import de.unitrier.st.codesparks.core.logging.UserActivityEnum;
import de.unitrier.st.codesparks.core.logging.UserActivityLogger;
import de.unitrier.st.codesparks.core.visualization.popup.MetricTable;

import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

/*
 * Copyright (c), Oliver Moseler, 2020
 */
public class ArtifactOverviewTableMouseAdapter extends MouseAdapter
{
    private final MetricTable table;

    ArtifactOverviewTableMouseAdapter(MetricTable table)
    {
        this.table = table;
    }

    @Override
    public void mouseClicked(MouseEvent e)
    {
        final Point point = e.getPoint();
        final int column = table.columnAtPoint(point);
        if (column < 1)
        {
            return;
        }
        final int row = table.rowAtPoint(point);
        if (row < 0)
        {
            return;
        }
        final ASourceCodeArtifact artifactAt = (ASourceCodeArtifact) ((ArtifactOverViewTableModel) table.getModel()).getArtifactAt(row);
        if (artifactAt == null)
        {
            return;
        }
        UserActivityLogger.getInstance().log(UserActivityEnum.OverviewNavigated, artifactAt.getIdentifier());
        artifactAt.navigate();
        //CoreUtil.navigate(artifactAt.getIdentifier());
    }
}
