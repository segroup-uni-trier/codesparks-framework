package de.unitrier.st.codesparks.core.visualization.popup;

import de.unitrier.st.codesparks.core.data.ACodeSparksThread;
import de.unitrier.st.codesparks.core.data.CodeSparksThreadCluster;

import javax.swing.*;
import java.util.Set;

public interface IThreadSelectable
{
    Set<ACodeSparksThread> getSelectedThreadArtifacts();

    Set<ACodeSparksThread> getFilteredThreadArtifacts();

    Set<ACodeSparksThread> getSelectedThreadArtifactsOfCluster(CodeSparksThreadCluster cluster);

    Set<String> getFilteredThreadArtifactIdentifiers();

    Set<String> getSelectedThreadArtifactIdentifiers();

    void registerComponentToRepaintOnSelection(JComponent componentToRepaintOnSelection);

    void deselectAll();

    void selectAll();

    void invertAll();

    void toggleCluster(CodeSparksThreadCluster cluster);

    //boolean hasFocus();
}
