package de.unitrier.st.insituprofiling.core.editorcoverlayer;

/*
 * Oliver Moseler, 2020
 */
public interface IEditorCoverLayerLogger
{
    void log(EditorCoverLayerLogEnum action, String... additionalInformation);
}
