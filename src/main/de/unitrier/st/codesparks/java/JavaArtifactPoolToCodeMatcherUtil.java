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

package de.unitrier.st.codesparks.java;

import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.util.Computable;
import com.intellij.psi.*;
import com.intellij.psi.util.PsiTreeUtil;

import java.util.Collection;

public final class JavaArtifactPoolToCodeMatcherUtil
{
    private JavaArtifactPoolToCodeMatcherUtil() {}

    public static Collection<PsiClass> getJavaClassesFrom(final PsiFile psiFile)
    {
        return ApplicationManager.getApplication().runReadAction(
                (Computable<Collection<PsiClass>>) () -> {
                    final Collection<PsiClass> psiClasses = PsiTreeUtil.findChildrenOfType(psiFile, PsiClass.class);
                    psiClasses.removeIf(psiClass -> psiClass instanceof PsiAnonymousClass || psiClass instanceof PsiTypeParameter);
                    return psiClasses;
                });
    }

    public static Collection<PsiMethod> getJavaMethodsFrom(final PsiFile psiFile)
    {
        return ApplicationManager.getApplication().runReadAction(
                (Computable<Collection<PsiMethod>>) () -> PsiTreeUtil.findChildrenOfType(psiFile, PsiMethod.class)
        );
    }

    public static Collection<PsiPackageStatement> getPackageStatementsFrom(final PsiFile psiFile)
    {
        //noinspection UnnecessaryLocalVariable
        final Collection<PsiPackageStatement> psiPackageStatements =
                ApplicationManager.getApplication().runReadAction((Computable<Collection<PsiPackageStatement>>) () ->
                        PsiTreeUtil.findChildrenOfType(psiFile, PsiPackageStatement.class));
        return psiPackageStatements;
    }
}
