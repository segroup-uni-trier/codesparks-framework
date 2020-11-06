package de.unitrier.st.codesparks.core;

import de.unitrier.st.codesparks.core.data.AProfilingArtifact;
import de.unitrier.st.codesparks.core.data.ProfilingArtifactTrie;

import java.util.List;
import java.util.Map;

public interface IProfilingResult extends IThreadArtifactFilterable
{
    AProfilingArtifact getArtifact(String identifier);

    List<AProfilingArtifact> getArtifacts();

    /**
     * @return A map, where the keys represent title strings of the values which are lists of artifacts.
     */
    Map<String, List<AProfilingArtifact>> getNamedArtifactTypeLists();

    /**
     * @return An artifact which encapsulates the global (total) metric value of the profiling run. Its assigned thread artifacts show the
     * global metric value's distribution over all threads.
     */
    AProfilingArtifact getGlobalArtifact();

    /**
     * @param artifact The artifact which encapsulates the global (total) metric value. Its assigned thread artifacts must represent the
     *                 global metric value's distribution over all threads.
     */
    void setGlobalArtifact(AProfilingArtifact artifact);

    /**
     * @return A full trie of the collected profiling artifacts, if the data processor is configured to compute one. An empty trie, otherwise.
     */
    ProfilingArtifactTrie getProfilingArtifactTrie();
}
