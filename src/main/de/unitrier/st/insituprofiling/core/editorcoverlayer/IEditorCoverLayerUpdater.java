package de.unitrier.st.insituprofiling.core.editorcoverlayer;

import com.intellij.openapi.vfs.VirtualFile;

/*
 * Oliver Moseler, 2020
 */
public interface IEditorCoverLayerUpdater
{
    void updateEditorCoverLayerFor(VirtualFile virtualFile);
}
