package de.unitrier.st.codesparks.core;

import com.intellij.openapi.project.Project;
import com.intellij.openapi.vfs.VirtualFile;
import de.unitrier.st.codesparks.core.data.AArtifact;

import java.util.Collection;

public interface IArtifactPoolToCodeMatcher
{
    Collection<AArtifact> matchArtifactsToCodeFiles(
            final IArtifactPool artifactPool
            , final Project project
            , final VirtualFile... files
    );
}
