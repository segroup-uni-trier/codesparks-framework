package de.unitrier.st.codesparks.core.editorcoverlayer;

import com.intellij.openapi.vfs.VirtualFile;

/*
 * Copyright (c), Oliver Moseler, 2020
 */
public interface IEditorCoverLayerUpdater
{
    void updateEditorCoverLayerFor(VirtualFile virtualFile);
}
