/*
 * Copyright (c), Oliver Moseler, 2021
 */
package de.unitrier.st.codesparks.core.visualization.popup;

import de.unitrier.st.codesparks.core.data.ThreadArtifactCluster;
import de.unitrier.st.codesparks.core.data.ThreadArtifactClustering;

import javax.swing.*;
import java.util.Set;

public interface IThreadSelectable extends IThreadSelectionProvider
{
    Set<String> getFilteredThreadArtifactIdentifiers();

    Set<String> getSelectedThreadArtifactIdentifiers();

    void registerComponentToRepaintOnSelection(JComponent componentToRepaintOnSelection);

    void updateAndRepaintRegisteredComponents();

    void deselectAll();

    void selectAll();

    void invertAll();

    void toggleCluster(ThreadArtifactCluster cluster);

    void setThreadArtifactClustering(ThreadArtifactClustering threadArtifactClustering);
}
