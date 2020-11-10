package de.unitrier.st.codesparks.core.visualization.popup;

import de.unitrier.st.codesparks.core.data.CodeSparksThread;
import de.unitrier.st.codesparks.core.data.CodeSparksThreadCluster;

import javax.swing.*;
import java.util.Set;

public interface IThreadSelectable
{
    Set<CodeSparksThread> getSelectedThreadArtifacts();

    Set<CodeSparksThread> getFilteredThreadArtifacts();

    Set<CodeSparksThread> getSelectedThreadArtifactsOfCluster(CodeSparksThreadCluster cluster);

    Set<String> getFilteredThreadArtifactIdentifiers();

    Set<String> getSelectedThreadArtifactIdentifiers();

    void registerComponentToRepaintOnSelection(JComponent componentToRepaintOnSelection);

    void deselectAll();

    void selectAll();

    void invertAll();

    void toggleCluster(CodeSparksThreadCluster cluster);

    //boolean hasFocus();
}
