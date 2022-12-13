/*
 * Copyright (c) 2021. Oliver Moseler
 */
package de.unitrier.st.codesparks.core.data;

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

    void setThreadArtifactClustering(ThreadArtifactClustering threadArtifactClustering, boolean retainCurrentSelection);
}