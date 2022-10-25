/*
 * Copyright (c) 2022. Oliver Moseler
 */

package de.unitrier.st.codesparks.core.java;

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
}
