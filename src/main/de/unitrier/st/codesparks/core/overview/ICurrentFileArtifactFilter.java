package de.unitrier.st.codesparks.core.overview;

import com.intellij.psi.PsiFile;
import de.unitrier.st.codesparks.core.data.AProfilingArtifact;

import java.util.Collection;

public interface ICurrentFileArtifactFilter
{
    Collection<AProfilingArtifact> filterArtifact(final Collection<? extends AProfilingArtifact> artifacts, final PsiFile psiFile);
}
