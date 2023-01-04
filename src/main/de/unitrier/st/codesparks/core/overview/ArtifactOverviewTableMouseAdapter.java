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
package de.unitrier.st.codesparks.core.overview;

import de.unitrier.st.codesparks.core.data.AArtifact;
import de.unitrier.st.codesparks.core.logging.UserActivityEnum;
import de.unitrier.st.codesparks.core.logging.UserActivityLogger;
import de.unitrier.st.codesparks.core.visualization.popup.MetricTable;

import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

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
        final AArtifact artifactAt = ((ArtifactOverViewTableModel) table.getModel()).getArtifactAt(row);
        if (artifactAt == null)
        {
            return;
        }
        UserActivityLogger.getInstance().log(UserActivityEnum.OverviewNavigated, artifactAt.getIdentifier());
        artifactAt.navigate();
        //CoreUtil.navigate(artifactAt.getIdentifier());
    }
}
