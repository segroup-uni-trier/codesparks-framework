/*
 * Copyright (c) 2021. Oliver Moseler
 */
package de.unitrier.st.codesparks.java;

import com.intellij.psi.*;
import de.unitrier.st.codesparks.core.logging.CodeSparksLogger;

import java.util.HashMap;
import java.util.Map;

/**
 * @author Oliver Moseler on 22.09.2014. Updated on 28.12.2019.
 */
final class JavaPsiClassNameHelper
{
    private final Map<PsiClass, Integer> anonymousLevelIndices;
    private final Map<String, Integer> innerLevelIndices;
    private final Map<PsiClass, String> classNames;

    JavaPsiClassNameHelper()
    {
        anonymousLevelIndices = new HashMap<>();
        innerLevelIndices = new HashMap<>();
        classNames = new HashMap<>();
    }

    String computeFullyQualifiedClassName(final PsiElement element)
    {
        try
        {
            PsiClass clazz = null;
            if (element instanceof PsiClass)
            {
                clazz = (PsiClass) element;
            } else
            {
                if (element instanceof PsiMethod)
                {
                    clazz = ((PsiMethod) element).getContainingClass();
                }
            }
            if (clazz == null)
            {
                throw new Exception("Cannot retrieve psiElement's class!");
            }
            if (classNames.containsKey(clazz))
            {
                return classNames.get(clazz);
            }
            PsiElement scope = clazz.getScope();
            if (clazz instanceof PsiAnonymousClass)
            {
                while (scope != null && !(scope instanceof PsiClass))
                {
                    if (scope instanceof PsiMethod)
                    {
                        scope = ((PsiMethod) scope).getContainingClass();
                    } else
                    {
                        if (scope instanceof PsiField)
                        {
                            scope = ((PsiField) scope).getContainingClass();
                        } else
                        {
                            // To not run into an infinite loop, return from here;
                            CodeSparksLogger.addText("%s: It is not defined how to proceed with the psi element's scope of type (=%s).",
                                    getClass().getName(), scope.getClass().getName());
                            return "";
                        }
                    }
//                    else
//                    {
//                        // From the Java docs:
//                        // /**
//                        //   * Returns the PSI member in which the class has been declared (for example,
//                        //   * the method containing the anonymous inner class, or the file containing a regular
//                        //   * class, or the class owning a type parameter).
//                        //   *
//                        //   * @return the member in which the class has been declared.
//                        //   */
//                        // TODO:
//                    }
                }

                final PsiClass clazzScope = (PsiClass) scope;
                if (clazzScope == null)
                {
                    throw new Exception("Cannot retrieve the scope of the psiElement's class.");
                }
                String name = computeFullyQualifiedClassName(clazzScope);
                int anonymousClassLevelIndex = 0;
                if (anonymousLevelIndices.containsKey(clazzScope))
                {
                    anonymousClassLevelIndex = anonymousLevelIndices.get(clazzScope);
                }
                anonymousLevelIndices.put(clazzScope, ++anonymousClassLevelIndex);
                name += "$" + anonymousClassLevelIndex;
                classNames.put(clazz, name);
                return name;
            } else
            {
                String name;
                if (scope instanceof PsiMethod)
                {
                    name = computeFullyQualifiedClassName(scope);
                    int innerClassLevelIndex = 0;
                    final String key = String.format("%s%s", name, clazz);
                    if (innerLevelIndices.containsKey(key))
                    {
                        innerClassLevelIndex = innerLevelIndices.get(key);
                    }
                    innerLevelIndices.put(key, ++innerClassLevelIndex);
                    name += "$" + innerClassLevelIndex + clazz.getName();
                } else
                {
                    if (scope instanceof PsiClass)
                    {
                        // Scope cold also be an inner or anonymous class.
                        name = computeFullyQualifiedClassName(scope) + "$" + clazz.getName();
                    } else
                    {
                        // Scope is the Java file itself or the most outer class, which neither is neither an inner nor an anonymous class.
                        name = clazz.getQualifiedName();
                    }
                }
                classNames.put(clazz, name);
                return name;
            }
        } catch (Exception e)
        {
            CodeSparksLogger.addText("%s: %s", getClass(), e.getMessage());
            return "";
        }
    }
}
