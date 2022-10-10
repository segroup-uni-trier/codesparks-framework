/*
 * Copyright (c) 2022. Oliver Moseler
 */

package de.unitrier.st.codesparks.core.java;

import com.intellij.openapi.editor.Document;
import com.intellij.openapi.project.Project;
import com.intellij.psi.*;
import com.intellij.psi.search.GlobalSearchScope;
import com.intellij.psi.util.PsiTreeUtil;
import de.unitrier.st.codesparks.core.CoreUtil;

import java.util.Collection;

public final class JavaArtifactNavigationUtil
{
    private JavaArtifactNavigationUtil() {}

    public static void navigateToMethod(final String methodIdentifier)
    {
        if ("selftime".equals(methodIdentifier))
        {
            return;
        }
        final Project project = CoreUtil.getCurrentlyOpenedProject();
        if (project == null)
        {
            return;
        }
        final String mMethodIdentifier = JavaUtil.checkAndReplaceConstructorName(methodIdentifier);
        final JavaPsiFacade javaPsiFacade = JavaPsiFacade.getInstance(project);
        final int braceIndex = mMethodIdentifier.indexOf("(");
        final String classAndMethodName = mMethodIdentifier.substring(0, braceIndex);
        final int lastIndexOfDot = classAndMethodName.lastIndexOf(".");
        final String fullyQualifiedClassName = classAndMethodName.substring(0, lastIndexOfDot);
        if (fullyQualifiedClassName.contains("$"))
        {
            final int indexOfDollar = fullyQualifiedClassName.indexOf("$");
            final String outerClass = fullyQualifiedClassName.substring(0, indexOfDollar);
            final String innerClass = fullyQualifiedClassName.substring(indexOfDollar + 1);
            final PsiClass psiClass = javaPsiFacade.findClass(outerClass, GlobalSearchScope.allScope(project));
            if (psiClass == null)
            {
                return;
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
                    break;
                }
            }
        } else
        {
            final PsiClass psiClass = javaPsiFacade.findClass(fullyQualifiedClassName, GlobalSearchScope.allScope(project));
            if (psiClass == null)
            {
                return;
            }
            final String methodName = classAndMethodName.substring(lastIndexOfDot + 1);
            final PsiMethod[] methods = psiClass.findMethodsByName(methodName, true);
            if (methods.length < 1)
            {
                // For example if you want to navigate to a default constructor which doesn't exist in the code!
                psiClass.navigate(true);
                return;
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
            }
        }
    }

    public static void navigateToClass(final String clazzIdentifier)
    {
        final Project project = CoreUtil.getCurrentlyOpenedProject();
        if (project == null)
        {
            return;
        }
        final JavaPsiFacade javaPsiFacade = JavaPsiFacade.getInstance(project);
        final PsiClass psiClass = javaPsiFacade.findClass(clazzIdentifier, GlobalSearchScope.allScope(project));
        if (psiClass == null)
        {
            return;
        }
        psiClass.navigate(true);
    }

    public static void navigateToLineInClass(final String clazzIdentifier, final int lineNumber)
    {
        final Project project = CoreUtil.getCurrentlyOpenedProject();
        if (project == null)
        {
            return;
        }
        final JavaPsiFacade javaPsiFacade = JavaPsiFacade.getInstance(project);
        final PsiClass psiClass = javaPsiFacade.findClass(clazzIdentifier, GlobalSearchScope.allScope(project));
        if (psiClass == null)
        {
            return;
        }

        final PsiFile containingFile = psiClass.getContainingFile();

        final PsiDocumentManager documentManager = PsiDocumentManager.getInstance(project);
        final Document document = documentManager.getDocument(containingFile);
        assert document != null;

        final int lineCount = document.getLineCount();
        if (lineNumber > lineCount)
        {
            return;
        }
        final int lineOffset = document.getLineStartOffset(lineNumber - 2);
        final PsiElement elementAt = psiClass.findElementAt(lineOffset);
        if (elementAt instanceof NavigatablePsiElement)
        {
            ((NavigatablePsiElement) elementAt).navigate(true);
        }
        final NavigatablePsiElement navigatablePsiElement = PsiTreeUtil.getParentOfType(elementAt, NavigatablePsiElement.class);
        assert navigatablePsiElement != null;
        navigatablePsiElement.navigate(true);
    }

    /**
     * @param clazzIdentifier The identifier of the class where the lambda ius defined.
     * @param lineNumber      The line number that is somewhere in the body of the searched lambda expression.
     */
    public static void navigateToLambdaInClassFromLine(final String clazzIdentifier, final int lineNumber)
    {
        final Project project = CoreUtil.getCurrentlyOpenedProject();
        if (project == null)
        {
            return;
        }
        final JavaPsiFacade javaPsiFacade = JavaPsiFacade.getInstance(project);
        final PsiClass psiClass = javaPsiFacade.findClass(clazzIdentifier, GlobalSearchScope.allScope(project));
        if (psiClass == null)
        {
            return;
        }

        final PsiFile containingFile = psiClass.getContainingFile();

        final PsiDocumentManager documentManager = PsiDocumentManager.getInstance(project);
        final Document document = documentManager.getDocument(containingFile);
        assert document != null;

        final int lineCount = document.getLineCount();
        if (lineNumber > lineCount)
        {
            return;
        }

        final int lineOffset = document.getLineStartOffset(lineNumber - 2);
        final PsiElement elementAt = psiClass.findElementAt(lineOffset);
        final PsiNewExpression newThreadExpression = PsiTreeUtil.getParentOfType(elementAt, PsiNewExpression.class);
        if (newThreadExpression == null)
        {
            return;
        }
        final PsiType type = newThreadExpression.getType();
        if (type == null)
        {
            return;
        }
        final String canonicalText = type.getCanonicalText();
        if ("java.lang.Thread".equals(canonicalText))
        {
            final PsiLambdaExpression lambdaExpression = PsiTreeUtil.findChildOfType(newThreadExpression, PsiLambdaExpression.class);
            if (lambdaExpression == null)
            {
                return;
            }
            lambdaExpression.navigate(true);
        }
    }
}
