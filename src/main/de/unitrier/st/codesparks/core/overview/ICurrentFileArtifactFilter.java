/*
 * Copyright (c) 2021. Oliver Moseler
 */
package de.unitrier.st.codesparks.core.overview;

import com.intellij.psi.PsiFile;
import de.unitrier.st.codesparks.core.data.AArtifact;

import java.util.Collection;

public interface ICurrentFileArtifactFilter
{
    Collection<? extends AArtifact> filterArtifact(final Collection<? extends AArtifact> artifacts, final PsiFile psiFile);
}
