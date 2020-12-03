package de.unitrier.st.codesparks.core;

import com.intellij.openapi.project.Project;
import com.intellij.openapi.vfs.VirtualFile;
import de.unitrier.st.codesparks.core.data.ACodeSparksArtifact;

import java.util.Collection;

/*
 * Copyright (c), Oliver Moseler, 2020
 */
public interface IArtifactPoolToCodeMatcher
{
    Collection<ACodeSparksArtifact> matchArtifactsToCodeFiles(
            final IArtifactPool artifactPool
            , final Project project
            , final VirtualFile... files
    );
}
