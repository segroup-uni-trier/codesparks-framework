package de.unitrier.st.codesparks.core.visualization.popup;

import de.unitrier.st.codesparks.core.data.AThreadArtifact;
import de.unitrier.st.codesparks.core.data.ThreadArtifactCluster;

import javax.swing.*;
import java.util.Set;

/*
 * Copyright (c), Oliver Moseler, 2020
 */
public interface IThreadSelectable extends IThreadSelectionProvider
{
    Set<String> getFilteredThreadArtifactIdentifiers();

    Set<String> getSelectedThreadArtifactIdentifiers();

    void registerComponentToRepaintOnSelection(JComponent componentToRepaintOnSelection);

    void deselectAll();

    void selectAll();

    void invertAll();

    void toggleCluster(ThreadArtifactCluster cluster);

    //boolean hasFocus();
}
