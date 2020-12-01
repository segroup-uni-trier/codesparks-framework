package de.unitrier.st.codesparks.core;

import de.unitrier.st.codesparks.core.data.AArtifact;

import java.util.List;
import java.util.Map;

/*
 * Copyright (c), Oliver Moseler, 2020
 */
public interface IArtifactPool extends ICodeSparksThreadFilterable, IArtifactPoolExportable
{
    void addArtifact(final AArtifact artifact);

    AArtifact getArtifact(final String identifier);

    AArtifact getArtifact(final Class<? extends AArtifact> artifactClass, final String identifier);

    List<AArtifact> getArtifacts(final Class<? extends AArtifact> artifactClass);

    Map<Class<? extends AArtifact>, List<AArtifact>> getArtifacts();

    String getArtifactClassDisplayName(final Class<? extends AArtifact> artifactClass);

//    /**
//     * @return A map, where the keys represent title strings of the values which are lists of artifacts.
//     */
//    Map<String, List<AArtifact>> getNamedArtifactTypeLists();

    /**
     * @return An artifact which encapsulates the metric value aggregated for the entire program run. Its assigned thread show
     * the metric value's distribution over all threads of the entire program.
     */
    AArtifact getProgramArtifact();

    /**
     * Will create an artifact representing the program artifact. For that, it uses the constructor of the class given as parameter.
     * Is thread safe!
     *
     * @param artifactClass The class of the artifact to create.
     * @return The created program artifact
     */
    AArtifact getOrCreateProgramArtifact(final Class<? extends AArtifact> artifactClass);
}
