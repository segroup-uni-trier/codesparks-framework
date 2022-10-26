/*
 * Copyright (c) 2022. Oliver Moseler
 */
package de.unitrier.st.codesparks.core;

import de.unitrier.st.codesparks.core.data.AArtifact;
import de.unitrier.st.codesparks.core.data.AThreadArtifact;

import java.util.List;
import java.util.Map;

public interface IArtifactPool extends IThreadArtifactFilterable, IArtifactPoolExportable
{
    /**
     * Add an artifact to the pool. The artifacts are distinguished by their class.
     *
     * @param artifact The artifact to add.
     */
    void addArtifact(final AArtifact artifact);

    /**
     * Get an artifact by identifier.
     * @param identifier The identifier associated with a corresponding artifact.
     * @return The artifact that is associated with the given identifier.
     */
    AArtifact getArtifact(final String identifier);

    /**
     * Get an artifact by its class and identifier.
     * @param artifactClass The class of the artifact.
     * @param identifier The identifier associated with a corresponding artifact.
     * @return The artifact associated with the specified identifier and of the type of the specified class.
     */
    AArtifact getArtifact(final Class<? extends AArtifact> artifactClass, final String identifier);

    AArtifact getOrCreateArtifact(final Class<? extends AArtifact> artifactClass, final String identifier, final Object... arguments);

    AThreadArtifact getOrCreateThreadArtifact(final Class<? extends AThreadArtifact> threadArtifactClass, final String threadIdentifier);

    List<AArtifact> getArtifacts(final Class<? extends AArtifact> artifactClass);

    Map<Class<? extends AArtifact>, List<AArtifact>> getArtifacts();

    String getArtifactClassDisplayName(final Class<? extends AArtifact> artifactClass);

    /**
     * @return An artifact which encapsulates the metric value aggregated for the entire program.
     */
    AArtifact getProgramArtifact();

    /**
     * Will create an artifact representing the program artifact. For that, it uses the constructor of the class given as parameter.
     * Note: thread safe.
     *
     * @param artifactClass The class of the artifact to create.
     * @return The created program artifact.
     */
    AArtifact getOrCreateProgramArtifact(final Class<? extends AArtifact> artifactClass);

    /**
     * Empties the artifact pool. That is, all artifacts of any type are removed.
     */
    void clear();

    /**
     * Register an artifactClassDisplayNameProvider to obtain a readable (display)name of the artifact classes.
     *
     * @param artifactClassDisplayNameProvider The artifactClassDisplayNameProvider is used to obtain a readable name of an artifact class that can be used,
     *                                         e.g., for its display in UI elements.
     */
    void registerArtifactClassDisplayNameProvider(final IArtifactClassDisplayNameProvider artifactClassDisplayNameProvider);
}
