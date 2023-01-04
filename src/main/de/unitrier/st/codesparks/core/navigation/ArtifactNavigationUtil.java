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

package de.unitrier.st.codesparks.core.navigation;

import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.editor.Document;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.util.Computable;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.openapi.vfs.VirtualFileManager;
import com.intellij.psi.*;
import com.intellij.psi.util.PsiTreeUtil;
import de.unitrier.st.codesparks.core.CoreUtil;

import java.nio.file.Path;
import java.nio.file.Paths;

public final class ArtifactNavigationUtil
{
    private ArtifactNavigationUtil() {}

    /**
     * @param fileName   The canonical path of the Java file.
     * @param lineNumber The line number within the Java file.
     * @return Whether a navigation could be performed.
     */
    public static boolean navigateToLineInFile(final String fileName, final int lineNumber)
    {
        final VirtualFileManager virtualFileManager = VirtualFileManager.getInstance();
        // Alternative:
//        final VirtualFile virtualFile = virtualFileManager.findFileByUrl("file://" + fileName);
        final Path path = Paths.get(fileName);
        final VirtualFile virtualFile = virtualFileManager.findFileByNioPath(path);
        if (virtualFile == null)
        {
            return false;
        }
        final Project project = CoreUtil.getCurrentlyOpenedProject();
        if (project == null)
        {
            return false;
        }
        final PsiManager psiManager = PsiManager.getInstance(project);
        final PsiFile psiFile = psiManager.findFile(virtualFile);
        if (psiFile == null)
        {
            return false;
        }
        final PsiDocumentManager psiDocumentManager = PsiDocumentManager.getInstance(project);
        final Document document = psiDocumentManager.getDocument(psiFile);
        if (document == null)
        {
            return false;
        }
        final int lineCount = document.getLineCount();
        if (lineNumber > lineCount)
        {
            return false;
        }
        final int lineStartOffset = document.getLineStartOffset(lineNumber);
        final PsiElement psiElement = ApplicationManager.getApplication().runReadAction(
                (Computable<PsiElement>) () -> psiFile.findElementAt(lineStartOffset)
        );
        return navigateToNextNavigatablePsi(psiElement);
    }

    /**
     * Try to navigate to the psi element given as parameter.
     * If this is not possible, find the next parent that is navigatable and try to navigate to that psi element.
     *
     * @param elementAt The psi element to navigate to.
     * @return Whether a navigation could be performed.
     */
    public static boolean navigateToNextNavigatablePsi(final PsiElement elementAt)
    {
        if (elementAt instanceof NavigatablePsiElement)
        {
            ((NavigatablePsiElement) elementAt).navigate(true);
        } else
        {
            final NavigatablePsiElement navigatablePsiElement = PsiTreeUtil.getParentOfType(elementAt, NavigatablePsiElement.class);
            if (navigatablePsiElement == null)
            {
                return false;
            }
            // assert navigatablePsiElement != null;
            navigatablePsiElement.navigate(true);
        }
        return true;
    }

}
