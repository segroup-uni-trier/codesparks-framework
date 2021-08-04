/*
 * Copyright (c) 2021. Oliver Moseler
 */
package de.unitrier.st.codesparks.core.editorcoverlayer;

public interface IEditorCoverLayerLogger
{
    void log(EditorCoverLayerLogEnum action, String... additionalInformation);
}
