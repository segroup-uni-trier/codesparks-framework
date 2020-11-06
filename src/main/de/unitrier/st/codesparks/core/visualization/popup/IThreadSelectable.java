package de.unitrier.st.codesparks.core.visualization.popup;

import de.unitrier.st.codesparks.core.data.ThreadArtifact;
import de.unitrier.st.codesparks.core.data.ThreadArtifactCluster;

import javax.swing.*;
import java.util.Set;

public interface IThreadSelectable
{
    Set<ThreadArtifact> getSelectedThreadArtifacts();

    Set<ThreadArtifact> getFilteredThreadArtifacts();

    Set<ThreadArtifact> getSelectedThreadArtifactsOfCluster(ThreadArtifactCluster cluster);

    Set<String> getFilteredThreadArtifactIdentifiers();

    Set<String> getSelectedThreadArtifactIdentifiers();

    void registerComponentToRepaintOnSelection(JComponent componentToRepaintOnSelection);

    void deselectAll();

    void selectAll();

    void invertAll();

    void toggleCluster(ThreadArtifactCluster cluster);

    //boolean hasFocus();
}
