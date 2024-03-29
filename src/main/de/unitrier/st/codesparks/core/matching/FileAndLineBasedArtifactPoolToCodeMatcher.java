/*
 *  Copyright 2023 Oliver Moseler
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */

package de.unitrier.st.codesparks.core.matching;

import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.editor.Document;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.util.Computable;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.psi.*;
import de.unitrier.st.codesparks.core.data.AArtifact;
import de.unitrier.st.codesparks.core.data.IArtifactPool;
import de.unitrier.st.codesparks.core.logging.CodeSparksLogger;
import org.apache.commons.io.FilenameUtils;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Each artifact is assumed to store the name of the canonical file (retrieved via getFileName())
 * and the line number within that file (retrieved via getLineNumber()) in which the artifact is defined.
 * <p>
 * Works for both, Python and Java artifacts.
 */
public final class FileAndLineBasedArtifactPoolToCodeMatcher implements IArtifactPoolToCodeMatcher
{
    @Override
    public Collection<AArtifact> matchArtifactsToCodeFiles(
            final IArtifactPool artifactPool,
            final Project project,
            final VirtualFile... files
    )
    {
        final Collection<AArtifact> matchedArtifacts = new ArrayList<>();
        if (artifactPool == null)
        {
            return matchedArtifacts;
        }

        final List<AArtifact> artifacts = artifactPool.getAllArtifacts();
        final PsiDocumentManager documentManager = PsiDocumentManager.getInstance(project);
        final PsiManager psiManager = PsiManager.getInstance(project);
        for (final VirtualFile file : files)
        {
            final String canonicalPath = file.getCanonicalPath();
            assert canonicalPath != null;

            final String fileName = FilenameUtils.separatorsToSystem(canonicalPath);

            final List<AArtifact> artifactsOfCurrentFile = artifacts.stream()
                    .filter(artifact -> FilenameUtils.separatorsToSystem(artifact.getFileName()).equals(fileName))
                    .collect(Collectors.toList());

            final PsiFile psiFile = ApplicationManager.getApplication().runReadAction((Computable<PsiFile>) () -> {
                //noinspection UnnecessaryLocalVariable
                final PsiFile thePsiFile = psiManager.findFile(file);
                return thePsiFile;
            });

            final Document document = documentManager.getDocument(psiFile);
            assert document != null;

            for (final AArtifact artifact : artifactsOfCurrentFile)
            {
                final int lineNumber = artifact.getLineNumber();
                int lineStartOffset;
                try
                {
                    lineStartOffset = document.getLineStartOffset(lineNumber);
                } catch (IndexOutOfBoundsException e)
                {
                    CodeSparksLogger.addText("Could not get line-start offset: %s", e.getMessage());
                    continue;
                }
                final PsiElement psiElement = ApplicationManager.getApplication().runReadAction(
                        (Computable<PsiElement>) () -> psiFile.findElementAt(lineStartOffset)
                );

                PsiElement sibling = psiElement.getPrevSibling();
                while (sibling instanceof PsiWhiteSpace)
                {
                    sibling = sibling.getPrevSibling();
                }

                final PsiElement visPsiElement = sibling != null ? sibling : psiElement;
                artifact.setVisPsiElement(visPsiElement);
                matchedArtifacts.add(artifact);
            }
        }
        return matchedArtifacts;
    }
}
