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

import com.intellij.openapi.editor.Document;
import com.intellij.openapi.project.Project;
import com.intellij.psi.*;
import com.intellij.psi.search.GlobalSearchScope;
import com.intellij.psi.util.PsiTreeUtil;
import de.unitrier.st.codesparks.core.CoreUtil;
import de.unitrier.st.codesparks.core.navigation.ArtifactNavigationUtil;

import java.util.Collection;

public final class JavaArtifactNavigationUtil
{
    private JavaArtifactNavigationUtil() {}

    /**
     * Navigate (GoTo) to a Java method.
     *
     * @param methodIdentifier Fully qualified name of the method.
     * @return Whether a navigation could be performed.
     */
    public static boolean navigateToMethod(final String methodIdentifier)
    {
        if ("selftime".equals(methodIdentifier) || "self-time".equals(methodIdentifier))
        {
            return false;
        }
        final Project project = CoreUtil.getCurrentlyOpenedProject();
        if (project == null)
        {
            return false;
        }
        final String mMethodIdentifier = JavaUtil.checkAndReplaceConstructorName(methodIdentifier);
        final int braceIndex = mMethodIdentifier.indexOf("(");
        if (braceIndex < 0)
        { // No method
            return false;
        }
        final String classAndMethodName = mMethodIdentifier.substring(0, braceIndex);
        final int lastIndexOfDot = classAndMethodName.lastIndexOf(".");
        final String fullyQualifiedClassName = classAndMethodName.substring(0, lastIndexOfDot);
        final JavaPsiFacade javaPsiFacade = JavaPsiFacade.getInstance(project);
        if (fullyQualifiedClassName.contains("$"))
        {
            final int indexOfDollar = fullyQualifiedClassName.indexOf("$");
            final String outerClass = fullyQualifiedClassName.substring(0, indexOfDollar);
            final String innerClass = fullyQualifiedClassName.substring(indexOfDollar + 1);
            final PsiClass psiClass = javaPsiFacade.findClass(outerClass, GlobalSearchScope.allScope(project));
            if (psiClass == null)
            {
                return false;
            }
            final String methodName = classAndMethodName.substring(lastIndexOfDot + 1);
            String parameters = mMethodIdentifier.substring(braceIndex);
            if (innerClass.equals(methodName))
            {// Constructor, so check if the first parameter has the same type as the outer class
                final String[] parameterTypes = parameters.split(",");
                if (parameterTypes.length > 0)
                {
                    final String firstParameterType = parameterTypes[0].substring(1).trim();
                    if (firstParameterType.equals(outerClass))
                    {// First parameter is of type of the outer class so remove it
                        final StringBuilder stringBuilder = new StringBuilder("(");
                        for (int i = 1; i < parameterTypes.length; i++) // Start with index 1 so that the first
                        // parameter will be removed
                        {
                            stringBuilder.append(parameterTypes[i].trim());
                            stringBuilder.append(", ");
                        }
                        if (stringBuilder.length() > 1)
                        {
                            stringBuilder.delete(stringBuilder.length() - 2, stringBuilder.length());
                        }
                        parameters = stringBuilder.toString();
                    }
                }
            }
            final Collection<PsiMethod> psiMethods = PsiTreeUtil.findChildrenOfType(psiClass, PsiMethod.class);
            final JavaPsiClassNameHelper javaPsiClassNameHelper = new JavaPsiClassNameHelper();
            for (final PsiMethod psiMethod : psiMethods)
            {
                // TODO: there is a side effect in the computeFullyQualifiedClassName method so that it is necessary to compute the class
                //  name of all methods since the anonymous class indices are wrong otherwise (they won't count up correctly)!
                final String className = javaPsiClassNameHelper.computeFullyQualifiedClassName(psiMethod); // Do not move this statement after the
                // 'if' because of the side effect mentioned in the above TODO
                final String psiMethodName = psiMethod.getName();
                if (!psiMethodName.equals(methodName))
                {
                    continue;
                }
                if (!className.equals(fullyQualifiedClassName))
                {
                    continue;
                }
                final String psiParameterString = JavaUtil.computePsiMethodParameterString(psiMethod);
                if (psiParameterString.equals(parameters))
                {
                    psiMethod.navigate(true);
//                    return true;
                    break;
                }
            }
        } else
        {
            final PsiClass psiClass = javaPsiFacade.findClass(fullyQualifiedClassName, GlobalSearchScope.allScope(project));
            if (psiClass == null)
            {
                return false;
            }
            final String methodName = classAndMethodName.substring(lastIndexOfDot + 1);
            final PsiMethod[] methods = psiClass.findMethodsByName(methodName, true);
            if (methods.length < 1)
            {
                // For example if you want to navigate to a default constructor which doesn't exist in the code!
                psiClass.navigate(true);
                return true;
            }
            final String parameters = mMethodIdentifier.substring(braceIndex);
            boolean methodFound = false;
            for (final PsiMethod psiMethod : methods)
            {
                final String psiParameterString = JavaUtil.computePsiMethodParameterString(psiMethod);
                if (!psiParameterString.equals(parameters))
                {
                    continue;
                }
                psiMethod.navigate(true);
                methodFound = true;
                break;
            }
            if (!methodFound)
            { // If the method could not be found go to its class instead
                psiClass.navigate(true);
                //return true;
            }
        }
        return true;
    }

    /**
     * Navigate (GoTo) to a Java class.
     *
     * @param clazzIdentifier The fully qualified class name.
     * @return Whether a navigation could be performed.
     */
    public static boolean navigateToClass(final String clazzIdentifier)
    {
        final Project project = CoreUtil.getCurrentlyOpenedProject();
        if (project == null)
        {
            return false;
        }
        final JavaPsiFacade javaPsiFacade = JavaPsiFacade.getInstance(project);
        final PsiClass psiClass = javaPsiFacade.findClass(clazzIdentifier, GlobalSearchScope.allScope(project));
        if (psiClass == null)
        {
            return false;
        }
        psiClass.navigate(true);
        return true;
    }

    /**
     * Navigate (GoTo) to a specific line in a Java class.
     *
     * @param clazzIdentifier The fully qualified class name.
     * @param lineNumber      The line number within the class.
     * @return Whether a navigation could be performed.
     */
    public static boolean navigateToLineInClass(final String clazzIdentifier, final int lineNumber)
    {
        final Project project = CoreUtil.getCurrentlyOpenedProject();
        if (project == null)
        {
            return false;
        }
        final JavaPsiFacade javaPsiFacade = JavaPsiFacade.getInstance(project);
        final PsiClass psiClass = javaPsiFacade.findClass(clazzIdentifier, GlobalSearchScope.allScope(project));
        if (psiClass == null)
        {
            return false;
        }
        final PsiDocumentManager documentManager = PsiDocumentManager.getInstance(project);
        final PsiFile containingFile = psiClass.getContainingFile();
        final Document document = documentManager.getDocument(containingFile);
        if (document == null)
        {
            return false;
        }
        final int lineCount = document.getLineCount();
        if (lineNumber > lineCount)
        {
            return false;
        }
        final int lineOffset = document.getLineStartOffset(lineNumber - 2);
        final PsiElement elementAt = psiClass.findElementAt(lineOffset);
        return ArtifactNavigationUtil.navigateToNextNavigatablePsi(elementAt);
    }

    /**
     * Navigate to a lambda expression within a class determined by a line number. Very simple heuristic!
     *
     * @param clazzIdentifier The fully qualified name of the class in which the lambda is defined.
     * @param lineNumber      The line number that is somewhere in the body of the searched lambda expression.
     * @return Whether a navigation could be performed.
     */
    public static boolean navigateToLambdaInClassFromLine(final String clazzIdentifier, final int lineNumber)
    {
        final Project project = CoreUtil.getCurrentlyOpenedProject();
        if (project == null)
        {
            return false;
        }
        final JavaPsiFacade javaPsiFacade = JavaPsiFacade.getInstance(project);
        final PsiClass psiClass = javaPsiFacade.findClass(clazzIdentifier, GlobalSearchScope.allScope(project));
        if (psiClass == null)
        {
            return false;
        }
        final PsiFile containingFile = psiClass.getContainingFile();
        final PsiDocumentManager documentManager = PsiDocumentManager.getInstance(project);
        final Document document = documentManager.getDocument(containingFile);
        if (document == null)
        {
            return false;
        }
        final int lineCount = document.getLineCount();
        if (lineNumber > lineCount)
        {
            return false;
        }
        final int lineOffset = document.getLineStartOffset(lineNumber - 2);
        final PsiElement elementAt = psiClass.findElementAt(lineOffset);
        final PsiNewExpression newThreadExpression = PsiTreeUtil.getParentOfType(elementAt, PsiNewExpression.class);
        if (newThreadExpression == null)
        {
            return false;
        }
        final PsiType type = newThreadExpression.getType();
        if (type == null)
        {
            return false;
        }
        final String canonicalText = type.getCanonicalText();
        if ("java.lang.Thread".equals(canonicalText))
        {
            final PsiLambdaExpression lambdaExpression = PsiTreeUtil.findChildOfType(newThreadExpression, PsiLambdaExpression.class);
            if (lambdaExpression == null)
            {
                return false;
            }
            lambdaExpression.navigate(true);
        }
        return true;
    }
}
