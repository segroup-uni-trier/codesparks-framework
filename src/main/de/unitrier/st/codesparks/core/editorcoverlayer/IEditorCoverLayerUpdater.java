/*
 * Copyright (c) 2021. Oliver Moseler
 */
package de.unitrier.st.codesparks.core.editorcoverlayer;

import com.intellij.openapi.vfs.VirtualFile;

public interface IEditorCoverLayerUpdater
{
    void updateEditorCoverLayerFor(VirtualFile virtualFile);
}
