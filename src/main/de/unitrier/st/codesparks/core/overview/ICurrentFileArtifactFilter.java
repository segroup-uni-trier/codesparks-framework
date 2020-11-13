package de.unitrier.st.codesparks.core.overview;

import com.intellij.psi.PsiFile;
import de.unitrier.st.codesparks.core.data.AArtifact;

import java.util.Collection;
import java.util.function.Function;

public interface ICurrentFileArtifactFilter
{
    Collection<AArtifact> filterArtifact(final Collection<? extends AArtifact> artifacts, final PsiFile psiFile);
}
