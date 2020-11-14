package de.unitrier.st.codesparks.core;

import de.unitrier.st.codesparks.core.data.AArtifact;
import de.unitrier.st.codesparks.core.data.ArtifactTrie;

import java.util.List;
import java.util.Map;

public interface IArtifactPool extends ICodeSparksThreadFilterable, IArtifactPoolExportable
{
    AArtifact getArtifact(final String identifier);

    AArtifact getArtifact(final Class<? extends AArtifact> artifactClass, final String identifier);

    List<AArtifact> getArtifacts(final Class<? extends AArtifact> artifactClass);

    void addArtifact(final AArtifact artifact);

    /**
     * @return A map, where the keys represent title strings of the values which are lists of artifacts.
     */
    Map<String, List<AArtifact>> getNamedArtifactTypeLists();

    /**
     * @return An artifact which encapsulates the metric value aggregated for the entire program run. Its assigned thread show
     * the metric value's distribution over all threads of the entire program.
     */
    AArtifact getProgramArtifact();

    /**
     * @param artifact The artifact which encapsulates the global (total) metric value. Its assigned thread artifacts must represent the
     *                 global metric value's distribution over all threads.
     */
    void setProgramArtifact(AArtifact artifact);

    /**
     * @return A full trie of the collected profiling artifacts, if the data processor is configured to compute one. An empty trie, otherwise.
     */
    @Deprecated // The idea is to make the trie an own metric of an artifact
    ArtifactTrie getArtifactTrie();
}
