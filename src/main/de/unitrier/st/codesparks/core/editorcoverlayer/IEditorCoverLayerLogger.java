package de.unitrier.st.codesparks.core.editorcoverlayer;

/*
 * Oliver Moseler, 2020
 */
public interface IEditorCoverLayerLogger
{
    void log(EditorCoverLayerLogEnum action, String... additionalInformation);
}
