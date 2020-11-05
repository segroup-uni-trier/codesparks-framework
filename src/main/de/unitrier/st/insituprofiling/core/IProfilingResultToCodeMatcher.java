package de.unitrier.st.insituprofiling.core;

import com.intellij.openapi.project.Project;
import com.intellij.openapi.vfs.VirtualFile;
import de.unitrier.st.insituprofiling.core.data.AProfilingArtifact;

import java.util.Collection;

public interface IProfilingResultToCodeMatcher
{
    Collection<AProfilingArtifact> matchResultsToCodeFiles(final IProfilingResult profilingResult, final Project project, final
    VirtualFile... files);
}
