package de.unitrier.st.codesparks.core.overview;

import de.unitrier.st.codesparks.core.data.AProfilingArtifact;
import de.unitrier.st.codesparks.core.logging.UserActivityEnum;
import de.unitrier.st.codesparks.core.logging.UserActivityLogger;
import de.unitrier.st.codesparks.core.visualization.popup.MetricTable;

import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

public class ProfilingArtifactOverviewTableMouseAdapter extends MouseAdapter
{
    private final MetricTable table;

    ProfilingArtifactOverviewTableMouseAdapter(MetricTable table)
    {
        this.table = table;
    }

    @Override
    public void mouseClicked(MouseEvent e)
    {
        Point point = e.getPoint();
        int row = table.rowAtPoint(point);
        if (row < 0)
        {
            return;
        }
        AProfilingArtifact artifactAt = ((ProfilingArtifactOverViewTableModel) table.getModel()).getArtifactAt(row);
        if (artifactAt == null)
        {
            return;
        }
        UserActivityLogger.getInstance().log(UserActivityEnum.OverviewNavigated, artifactAt.getIdentifier());
        artifactAt.navigate();
        //CoreUtil.navigate(artifactAt.getIdentifier());
    }
}
